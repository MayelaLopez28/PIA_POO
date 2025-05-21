package Piezas;

import Main.Tablero;

/**
 * Clase que representa la pieza Caballo (Knight) en el juego de ajedrez
 * Implementa la logica de movimiento en forma de "L" para el caballo
 */
public class Caballo extends Pieza{

    /**
     * Constructor para la pieza Caballo
     *
     * @param tablero Tablero de juego en el que se coloca la pieza
     * @param columna Posicion inicial de la columna (0-7)
     * @param fila Posicion inicial de la fila (0-7)
     * @param EsBlanco Indica si la pieza pertenece al
     *                 jugador blanco (true) o negro (false)
     */
    public Caballo(Tablero tablero, int columna, int fila, boolean EsBlanco){
        super(tablero);
        this.columna=columna;
        this.fila=fila;
        this.xPos=columna*tablero.tileSize;// Posicion x en pixeles
        this.yPos=fila*tablero.tileSize;// Posicion y en pixeles
        this.EsBlanco=EsBlanco;
        this.name="Caballo";

        // Carga la imagen correspondiente segun el color
        String imageName = EsBlanco ? "caballo_blanco.png" : "caballo_negro.png";
        loadImage(imageName);

    }

    /**
     * Verifica si el movimiento a la posicion especificada es valido para un caballo
     * El caballo se mueve en patron de "L": 2 casillas en una direccion y
     * 1 en perpendicular o 1 casilla en una direccion y 2 en perpendicular
     *
     * @param columna Columna de destino
     * @param fila Fila de destino
     * @return true si el movimiento es valido para un caballo, false en caso contrario
     */
    @Override
    public boolean esMovimientoValido(int columna,int fila) {
        int dx= Math.abs(columna-this.columna);
        int dy= Math.abs(fila-this.fila);
        return (dx==1 && dy==2)||(dx==2 && dy==1);
    }
}
