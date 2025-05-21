package Main;

import Piezas.*;

/**
 * Clase que representa un movimiento en el tablero de ajedrez
 * Almacena la informacion necesaria para realizar y deshacer movimientos,
 * incluyendo las posiciones de origen y destino, la pieza movida
 * y la pieza capturada (si existe)
 */
public class Movimientos {
    int oldColumna;//Columna de origen de la pieza antes del movimiento
    int oldFila;//Fila de origen de la pieza antes del movimiento
    int newColumna;//Columna de destino donde se movera la pieza
    int newFila;//Fila de destino donde se movera la pieza

    Pieza pieza;//Referencia a la pieza que esta siendo movida
    Pieza captura;//Referencia a la pieza que sera capturada en este movimiento (null si no hay captura)

    /**
     * Constructor de la clase Movimientos
     * Inicializa un nuevo objeto Movimientos con los datos del movimiento a realizar
     *
     * @param tablero Tablero donde se realizan los movimientos
     *                ,necesario para verificar si hay piezas en la posición destino
     * @param pieza Pieza que se esta moviendo en el tablero
     * @param newColumna Nueva columna a la que se mueve la pieza
     * @param newFila Nueva fila a la que se mueve la pieza
     */
    public Movimientos(Tablero tablero,Pieza pieza,int newColumna,int newFila) {
        // Almacena la posicion original de la pieza
        this.oldColumna=pieza.columna;
        this.oldFila=pieza.fila;

        // Almacena la posicion destino
        this.newColumna=newColumna;
        this.newFila=newFila;

        // Guarda referencia a la pieza que se esta moviendo
        this.pieza=pieza;

        // Verifica si hay una pieza en la posicion destino (potencial captura)
        this.captura=tablero.getPieza(newColumna,newFila);
    }
}
