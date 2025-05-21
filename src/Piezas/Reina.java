package Piezas;


import Main.Tablero;


/**
 * Clase que representa la pieza Reina en el juego de ajedrez
 * La Reina puede moverse en cualquier direccion (horizontal, vertical o diagonal)
 * y por cualquier numero de casillas, siempre y cuando no haya piezas en su camino
 */
public class Reina extends Pieza {

    /**
     * Constructor de la pieza Reina
     *
     * @param tablero El tablero donde se situa la pieza
     * @param columna La columna inicial de la pieza (0-7)
     * @param fila La fila inicial de la pieza (0-7)
     * @param EsBlanco Indica si la pieza pertenece al jugador de piezas blancas
     */
    public Reina(Tablero tablero, int columna, int fila, boolean EsBlanco) {
        super(tablero);
        this.columna = columna;
        this.fila = fila;
        this.xPos = columna * tablero.tileSize;
        this.yPos = fila * tablero.tileSize;
        this.EsBlanco = EsBlanco;
        this.name = "Reina";

        // Carga la imagen correspondiente segun el color de la pieza
        String imageName = EsBlanco ? "reina_blanco.png" : "reina_negro.png";
        loadImage(imageName);

    }

    /**
     * Verifica si un movimiento es valido para la Reina
     * La Reina puede moverse en horizontal, vertical o diagonal
     *
     * @param columna La columna destino
     * @param fila La fila destino
     * @return true si el movimiento es valido, false en caso contrario
     */
    @Override
    public boolean esMovimientoValido(int columna, int fila) {
        // La Reina se mueve como Torre (horizontal/vertical) o como Alfil (diagonal)
        return this.columna == columna || this.fila == fila || Math.abs(this.columna - columna) == Math.abs(this.fila - fila);
    }

    /**
     * Verifica si el movimiento de la Reina choca con alguna pieza en su trayectoria
     *
     * @param columna La columna destino
     * @param fila La fila destino
     * @return true si hay alguna pieza en la trayectoria, false si el camino esta libre
     */
    @Override
    public boolean movimientoChocaPieza(int columna, int fila) {

        // Si el movimiento es horizontal o vertical (como Torre)
        if (this.columna == columna || this.fila == fila) {
            // Movimiento hacia la izquierda
            if (this.columna > columna) {
                for (int c = this.columna - 1; c > columna; c--) {
                    if (tablero.getPieza(c, this.fila) != null) {
                        return true;// Hay una pieza en el camino
                    }
                }
            }

            // Movimiento hacia la derecha
            if (this.columna < columna) {
                for (int c = this.columna + 1; c < columna; c++) {
                    if (tablero.getPieza(c, this.fila) != null) {
                        return true;// Hay una pieza en el camino
                    }
                }
            }

            // Movimiento hacia arriba
            if (this.fila > fila) {
                for (int r = this.fila - 1; r > fila; r--) {
                    if (tablero.getPieza(this.columna, r) != null) {
                        return true;// Hay una pieza en el camino
                    }
                }
            }

            // Movimiento hacia abajo
            if (this.fila < fila) {
                for (int r = this.fila + 1; r < fila; r++) {
                    if (tablero.getPieza(this.columna, r) != null) {
                        return true;// Hay una pieza en el camino
                    }
                }
            }
        }else{
            // Si el movimiento es diagonal (como Alfil)
            // Diagonal hacia arriba e izquierda
            if(this.columna>columna && this.fila>fila){
                for(int i=1;i<Math.abs(this.columna-columna);i++){
                    if(tablero.getPieza(this.columna-i,this.fila-i)!=null){
                        return true;
                    }
                }
            }

            // Diagonal hacia arriba y derecha
            if(this.columna<columna && this.fila>fila){
                for(int i=1;i<Math.abs(this.columna - columna);i++){
                    if(tablero.getPieza(this.columna+i,this.fila-i)!=null){
                        return true;// Hay una pieza en el camino
                    }
                }
            }

            // Diagonal hacia abajo e izquierda
            if(this.columna>columna && this.fila<fila){
                for(int i=1;i<Math.abs(this.columna-columna);i++){
                    if(tablero.getPieza(this.columna-i,this.fila+i)!=null){
                        return true;// Hay una pieza en el camino
                    }
                }
            }

            // Diagonal hacia abajo y derecha
            if(this.columna<columna && this.fila<fila){
                for(int i=1;i<Math.abs(this.columna - columna);i++){
                    if(tablero.getPieza(this.columna+i,this.fila+i)!=null){
                        return true;// Hay una pieza en el camino
                    }
                }
            }

        }
        return false;// No hay piezas en el camino
    }

}