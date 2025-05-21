package Piezas;

import Main.Tablero;

/**
 * Clase que representa la pieza Peon en el juego de ajedrez
 * Implementa la logica de movimiento especifica del peon, incluyendo:
 * - Avance de una casilla
 * - Avance inicial de dos casillas
 * - Captura diagonal
 * - Captura al paso (en passant)
 */
public class Peon extends Pieza{

    /**
     * Constructor para la pieza Peon
     *
     * @param tablero Tablero de juego en el que se coloca la pieza
     * @param columna Posicion inicial de la columna (0-7)
     * @param fila Posicion inicial de la fila (0-7)
     * @param EsBlanco Indica si la pieza pertenece al jugador blanco (true) o negro (false)
     */

    public Peon(Tablero tablero, int columna, int fila, boolean EsBlanco){
        super(tablero);
        this.columna=columna;
        this.fila=fila;
        this.xPos=columna*tablero.tileSize;// Posicion x en pixeles
        this.yPos=fila*tablero.tileSize;// Posicion y en pixeles
        this.EsBlanco=EsBlanco;
        this.name="Peon";

        // Carga la imagen correspondiente segun el color
        String imageName = EsBlanco ? "peon_blanco.png" : "peon_negro.png";
        loadImage(imageName);

    }


    /**
     * Verifica si el movimiento a la posicion especificada es valido para un peon
     * Los peones tienen multiples reglas especiales:
     * - Avanzan solo hacia adelante (direccion opuesta segun el color)
     * - Pueden avanzar 2 casillas en su primer movimiento
     * - Solo pueden capturar en diagonal
     * - Pueden realizar la captura "en passant"
     *
     * @param columna Columna de destino
     * @param fila Fila de destino
     * @return true si el movimiento es valido, false en caso contrario
     */
    @Override
    public boolean esMovimientoValido(int columna,int fila) {
        // Define la direccion de movimiento segun el color (blanco: hacia arriba [-1],
        // negro: hacia abajo [+1])
        int colorIndex = EsBlanco ? 1 : -1;

        // Movimiento recto 1 casilla
        if (this.columna == columna && fila == this.fila - colorIndex) {
            // Verifica que no haya pieza en la casilla destino
            if (tablero.getPieza(columna, fila) == null) {
                return true;
            }
            return false; // No puede moverse si hay una pieza bloqueando
        }

        // Movimiento inicial 2 casillas
        if (esPrimerMovimiento && this.columna == columna && fila == this.fila - colorIndex * 2) {
            // Verifica que no haya piezas en el camino
            if (tablero.getPieza(columna, this.fila - colorIndex) == null &&
                    tablero.getPieza(columna, fila) == null) {
                return true;
            }
            return false; // No puede moverse si hay piezas bloqueando
        }

        // Captura diagonal (izquierda o derecha)
        if ((columna == this.columna - 1 || columna == this.columna + 1) &&
                fila == this.fila - colorIndex) {

            // Solo puede moverse en diagonal si hay una pieza enemiga para capturar
            Pieza piezaDestino = tablero.getPieza(columna, fila);
            if (piezaDestino != null && piezaDestino.EsBlanco != this.EsBlanco) {
                return true;
            }

            // O si es una captura en passant valida
            if (tablero.getTileNum(columna, fila) == tablero.enPassantTile) {
                Pieza peonPassant = tablero.getPieza(columna, fila + colorIndex);
                if (peonPassant != null && peonPassant.name.equals("Peon") &&
                        peonPassant.EsBlanco != this.EsBlanco) {
                    return true;
                }
            }

            return false; // No puede moverse en diagonal si no hay captura
        }

        return false;


    }

    /**
     * Verifica si hay piezas en el camino del movimiento que puedan bloquear al peon
     * Esto es relevante principalmente para el movimiento inicial de 2 casillas
     *
     * @param columna Columna de destino
     * @param fila Fila de destino
     * @return true si hay alguna pieza bloqueando el camino, false si el camino esta libre
     */
    @Override
    public boolean movimientoChocaPieza(int columna, int fila) {
        int colorIndex = EsBlanco ? 1 : -1;

        // Si es movimiento vertical
        if (this.columna == columna) {
            // Verificar movimiento de 2 casillas
            if (Math.abs(this.fila - fila) == 2) {
                int filaMedio = this.fila - colorIndex;
                return tablero.getPieza(columna, filaMedio) != null ||
                        tablero.getPieza(columna, fila) != null;
            } else {
                // Movimiento de 1 casilla
                return tablero.getPieza(columna, fila) != null;
            }
        }

        return false;
    }

}