package Piezas;

import Main.Tablero;

/**
 * Clase que representa la pieza Alfil (Bishop) en el juego de ajedrez
 * Implementa la logica de movimiento en diagonal para el alfil
 */
public class Bishop extends Pieza{

    /**
     * Constructor para la pieza Alfil
     *
     * @param tablero Tablero de juego en el que se coloca la pieza
     * @param columna Posicion inicial de la columna (0-7)
     * @param fila Posicion inicial de la fila (0-7)
     * @param EsBlanco Indica si la pieza pertenece al jugador blanco (true) o negro (false)
     */
    public Bishop(Tablero tablero, int columna, int fila, boolean EsBlanco){
        super(tablero);
        this.columna=columna;
        this.fila=fila;
        this.xPos=columna*tablero.tileSize;// Posicion x en pixeles
        this.yPos=fila*tablero.tileSize;// Posicion y en pixeles
        this.EsBlanco=EsBlanco;
        this.name="Bishop";

        // Carga la imagen correspondiente segun el color
        String imageName = EsBlanco ? "bishop_blanco.png" : "bishop_negro.png";
        loadImage(imageName);

    }

    /**
     * Verifica si el movimiento a la posicion especificada es valido para un alfil
     * El alfil solo puede moverse en diagonal (igual numero de casillas en x e y)
     *
     * @param columna Columna de destino
     * @param fila Fila de destino
     * @return true si el movimiento es valido, false en caso contrario
     */
    @Override
    public boolean esMovimientoValido(int columna, int fila){
        return Math.abs(this.columna-columna)==Math.abs(this.fila-fila);
    }

    /**
     * Verifica si hay piezas en el camino del movimiento
     * que puedan bloquear al alfil
     * Comprueba todas las casillas intermedias en la
     * diagonal correspondiente
     *
     * @param columna Columna de destino
     * @param fila Fila de destino
     * @return true si hay alguna pieza bloqueando el camino,
     * false si el camino esta libre
     */
    @Override
    public boolean movimientoChocaPieza(int columna, int fila){

        //arriba, izquierda
        if(this.columna > columna && this.fila > fila){
            for(int i = 1; i < Math.abs(this.columna - columna); i++){
                if(tablero.getPieza(this.columna - i, this.fila - i) != null){
                    return true;
                }
            }
        }

        //arriba, derecha
        if(this.columna < columna && this.fila > fila){
            for(int i = 1; i < Math.abs(this.columna - columna); i++){
                if(tablero.getPieza(this.columna + i, this.fila - i) != null){
                    return true;
                }
            }
        }

        //abajo, izquierda
        if(this.columna > columna && this.fila < fila){
            for(int i = 1; i < Math.abs(this.columna - columna); i++){
                if(tablero.getPieza(this.columna - i, this.fila + i) != null){
                    return true;
                }
            }
        }

        //abajo, derecha
        if(this.columna < columna && this.fila < fila){
            for(int i = 1; i < Math.abs(this.columna - columna); i++){
                if(tablero.getPieza(this.columna + i, this.fila + i) != null){
                    return true;
                }
            }
        }

        return false;
    }
}