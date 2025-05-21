package Piezas;


import Main.JaqueScanner;
import Main.Movimientos;
import Main.Tablero;

/**
 * Clase que representa la pieza Rey en el juego de ajedrez
 * El Rey puede moverse una casilla en cualquier direccion (horizontal, vertical o diagonal)
 * Tambien implementa la jugada especial del enroque, tanto corto como largo
 */
public class Rey extends Pieza{

    /**
     * Constructor de la pieza Rey
     *
     * @param tablero El tablero donde se situa la pieza
     * @param columna La columna inicial de la pieza (0-7)
     * @param fila La fila inicial de la pieza (0-7)
     * @param EsBlanco Indica si la pieza pertenece al jugador de piezas blancas
     */
    public Rey (Tablero tablero, int columna, int fila, boolean EsBlanco){
        super(tablero);
        this.columna=columna;
        this.fila=fila;
        this.xPos=columna*tablero.tileSize;
        this.yPos=fila*tablero.tileSize;
        this.EsBlanco=EsBlanco;
        this.name="Rey";

        // Carga la imagen correspondiente segun el color de la pieza
        String imageName = EsBlanco ? "rey_blanco.png" : "rey_negro.png";
        loadImage(imageName);

    }

    /**
     * Verifica si un movimiento es valido para el Rey
     * El Rey puede moverse una casilla en cualquier direccion o realizar un enroque
     *
     * @param columna La columna destino
     * @param fila La fila destino
     * @return true si el movimiento es valido, false en caso contrario
     */
    @Override
    public boolean esMovimientoValido(int columna,int fila) {
        // El Rey se mueve una casilla en cualquier direccion o hace enroque
        int dx= Math.abs(columna-this.columna);
        int dy= Math.abs(fila-this.fila);
        return (dx<=1 && dy<=1)||canCastle(columna,fila);
    }

    /**
     * Verifica si es posible realizar un enroque hacia la posicion especificada
     * Para el enroque se comprueban las siguientes condiciones:
     * 1 El Rey no esta en jaque
     * 2 Es el primer movimiento tanto del Rey como de la Torre
     * 3 No hay piezas entre el Rey y la Torre
     * 4 El Rey no pasa por casillas atacadas durante el movimiento
     *
     * @param columna La columna destino para el enroque
     * @param fila La fila destino (debe ser la misma que la actual)
     * @return true si el enroque es posible, false en caso contrario
     */
    private boolean canCastle(int columna, int fila){
        JaqueScanner js = new JaqueScanner(tablero);

        // Si el Rey esta en jaque, no puede hacer enroque
        if (js.EsReyJaque(new Movimientos(tablero, this, this.columna, this.fila))) {
            return false;
        }

        // Verificar que es el primer movimiento y que se mantiene en la misma fila
        if (this.fila == fila && esPrimerMovimiento) {
            // Enroque corto (hacia la derecha)
            if (columna == 6) {
                Pieza torre = tablero.getPieza(7, fila);
                if (torre != null && torre.name.equals("Torre") && torre.esPrimerMovimiento) {
                    // Verificar que las casillas entre el rey y la torre esten vacias
                    if (tablero.getPieza(5, fila) == null && tablero.getPieza(6, fila) == null) {
                        // Verificar que el rey no pase por jaque durante el enroque
                        return !js.EsReyJaque(new Movimientos(tablero, this, 5, fila));
                    }
                }
            }
            // Enroque largo (hacia la izquierda)
            else if (columna == 2) {
                Pieza torre = tablero.getPieza(0, fila);
                if (torre != null && torre.name.equals("Torre") && torre.esPrimerMovimiento) {
                    // Verificar que las casillas entre el rey y la torre esten vacias
                    if (tablero.getPieza(1, fila) == null &&
                            tablero.getPieza(2, fila) == null &&
                            tablero.getPieza(3, fila) == null) {
                        // Verificar que el rey no pase por jaque durante el enroque
                        return !js.EsReyJaque(new Movimientos(tablero, this, 3, fila));
                    }
                }
            }
        }
        return false;
    }
}