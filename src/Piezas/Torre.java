package Piezas;


import Main.Tablero;

/**
 * Clase que representa la pieza Torre en el juego de ajedrez
 * La Torre puede moverse en direccion horizontal o vertical
 * por cualquier numero de casillas, siempre y cuando no haya piezas en su camino
 */
public class Torre extends Pieza{

    /**
     * Constructor de la pieza Torre
     *
     * @param tablero El tablero donde se situa la pieza
     * @param columna La columna inicial de la pieza (0-7)
     * @param fila La fila inicial de la pieza (0-7)
     * @param EsBlanco Indica si la pieza pertenece al jugador de piezas blancas
     */
    public Torre(Tablero tablero, int columna, int fila, boolean EsBlanco){
        super(tablero);
        this.columna=columna;
        this.fila=fila;
        this.xPos=columna*tablero.tileSize;
        this.yPos=fila*tablero.tileSize;
        this.EsBlanco=EsBlanco;
        this.name="Torre";

        // Carga la imagen correspondiente segun el color de la pieza
        String imageName = EsBlanco ? "torre_blanco.png" : "torre_negro.png";
        loadImage(imageName);

    }

    /**
     * Verifica si un movimiento es valido para la Torre
     * La Torre solo puede moverse en horizontal o vertical
     *
     * @param columna La columna destino
     * @param fila La fila destino
     * @return true si el movimiento es valido, false en caso contrario
     */
    @Override
    public boolean esMovimientoValido(int columna,int fila){
        // La Torre solo se mueve en horizontal o vertical
        return this.columna==columna || this.fila==fila;
    }

    /**
     * Verifica si el movimiento de la Torre choca con alguna pieza en su trayectoria
     *
     * @param columna La columna destino
     * @param fila La fila destino
     * @return true si hay alguna pieza en la trayectoria, false si el camino esta libre
     */
    @Override
    public boolean movimientoChocaPieza(int columna, int fila){

        // Movimiento hacia la izquierda
        if(this.columna>columna){
            for(int c=this.columna -1;c>columna;c--){
                if(tablero.getPieza(c,this.fila)!=null){
                    return true;// Hay una pieza en el camino
                }
            }
        }

        // Movimiento hacia la derecha
        if(this.columna<columna){
            for(int c=this.columna+1;c<columna;c++){
                if(tablero.getPieza(c,this.fila)!=null){
                    return true;// Hay una pieza en el camino
                }
            }
        }

        // Movimiento hacia arriba
        if(this.fila>fila){
            for(int r=this.fila -1;r>fila;r--){
                if(tablero.getPieza(this.columna,r)!=null){
                    return true;// Hay una pieza en el camino
                }
            }
        }

        // Movimiento hacia abajo
        if(this.fila<fila){
            for(int r=this.fila+1;r<fila;r++){
                if(tablero.getPieza(this.columna,r)!=null){
                    return true;// Hay una pieza en el camino
                }
            }
        }
        return false;// No hay piezas en el camino
    }
}