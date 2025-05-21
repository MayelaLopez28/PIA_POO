package Main;

import Piezas.Pieza;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;


/**
 * Clase para detectar y manejar situaciones de jaque en un juego de ajedrez
 * Esta clase proporciona metodos para determinar si un rey esta en jaque,
 * identificar las piezas que estan atacando al rey, y evaluar situaciones de jaque mate
 * Implementa Serializable para permitir que las instancias sean serializadas
 * junto con el estado del juego
 */
public class JaqueScanner implements Serializable{
    private static final long serialVersionUID=1L;
    private static Tablero tablero;

    /**
     * Constructor de la clase JaqueScanner
     * Inicializa un nuevo scanner de jaque asociado a un tablero
     *
     * @param tablero Tablero de juego asociado que contiene las piezas
     *                y su distribucion actual
     */
    public JaqueScanner(Tablero tablero) {
        this.tablero = tablero;
    }

    /**
     * Establece o actualiza el tablero de juego asociado a este scanner
     * Este metodo permite reutilizar el scanner con diferentes
     * instancias de tablero
     *
     * @param tablero Nuevo tablero de juego a asociar con este scanner
     */
    public void setTablero(Tablero tablero) {
        this.tablero = tablero;
    }


    /**
     * Verifica si el rey del jugador actual esta en situacion
     * de jaque despues de realizar un movimiento
     * Este metodo analiza si alguna pieza enemiga puede atacar
     * al rey despues de que se realice el movimiento especificado
     * Considera el caso especial donde el rey mismo es la pieza que se mueve
     *
     * El metodo ejecuta una verificacion en todas las direcciones posibles:
     * - Verifica ataques horizontales y verticales (torres y reinas)
     * - Verifica ataques diagonales (alfiles y reinas)
     * - Verifica ataques de caballo
     * - Verifica ataques de peones
     * - Verifica ataques del rey enemigo
     *
     * @param mover Movimiento a evaluar que contiene la pieza a mover y su nueva posicion
     * @return true si el rey esta en jaque despues del movimiento, false en caso contrario
     */
    public static boolean EsReyJaque(Movimientos mover){
        Pieza rey=tablero.encontrarRey(mover.pieza.EsBlanco);

        if(rey==null){
            return false;
        }

        int reyColumna=rey.columna;
        int reyFila=rey.fila;

        // Ajusta la posicion del rey si es la pieza que se mueve
        if(rey == mover.pieza){
            reyColumna = mover.newColumna;
            reyFila = mover.newFila;
        }

        // Verifica jaque desde todas las direcciones posibles
        return  hitByTorre(mover.newColumna,mover.newFila,rey,reyColumna,reyFila,0,1)||//arriba
                hitByTorre(mover.newColumna,mover.newFila,rey,reyColumna,reyFila,1,0)||//derecha
                hitByTorre(mover.newColumna,mover.newFila,rey,reyColumna,reyFila,0,-1)||//abajo
                hitByTorre(mover.newColumna,mover.newFila,rey,reyColumna,reyFila,-1,0)||//izquierda

                hitByBishop(mover.newColumna,mover.newFila,rey,reyColumna,reyFila,-1,-1)||//arriba izq
                hitByBishop(mover.newColumna,mover.newFila,rey,reyColumna,reyFila,1,-1)||//arriba der
                hitByBishop(mover.newColumna,mover.newFila,rey,reyColumna,reyFila,1,1)||//abajo derecha
                hitByBishop(mover.newColumna,mover.newFila,rey,reyColumna,reyFila,-1,1)||//abajo izq

                hitByCaballo(mover.newColumna,mover.newFila,rey,reyColumna,reyFila)||
                hitByPeon(mover.newColumna,mover.newFila,rey,reyColumna,reyFila)||
                hitByRey(rey,reyColumna,reyFila);
    }

    /**
     * Encuentra todas las piezas enemigas que estan atacando al rey especificado
     * Este metodo examina el tablero y determina que piezas
     * adversarias tienen al rey como objetivo valido y podrian
     * capturarlo en su proximo movimiento
     *
     * El metodo utiliza la lista completa de piezas del tablero y
     * verifica para cada pieza enemiga:
     * 1. Si la pieza es de color opuesto al rey
     * 2. Si el movimiento desde la pieza hasta el rey es valido
     * segun las reglas de movimiento de esa pieza
     * 3. Si no hay obstaculos entre la pieza y el rey que impidan el ataque
     *
     * @param rey Pieza del rey que se esta evaluando
     * @return Lista de piezas enemigas que estan atacando al rey
     * , si no hay atacantes o el rey es null, devuelve una lista vacia
     */
    public ArrayList<Pieza> encontrarAtacantes(Pieza rey) {
        ArrayList<Pieza> atacantes = new ArrayList<>();

        if (rey == null) {
            return atacantes;
        }

        for(Pieza p: tablero.piezasList){
            // Verifica que la pieza sea enemiga
            if(!tablero.sameColor(p,rey)){
                // Verifica si puede atacar al rey
                if(p.esMovimientoValido(rey.columna, rey.fila) &&
                        !p.movimientoChocaPieza(rey.columna, rey.fila)){
                    atacantes.add(p);
                }
            }
        }
        return atacantes;
    }

    /**
     * Obtiene el camino de ataque entre el rey y una pieza atacante
     * Este metodo calcula todas las casillas que estan en la linea de ataque entre
     * el rey y la pieza que lo esta atacando
     * Es util para determinar si es posible bloquear un jaque interponiendo otra
     * pieza en el camino
     *
     * Funciona de la siguiente manera:
     * 1.Para piezas que atacan en linea recta (torres, alfiles, reinas),
     * determina la direccion del ataque
     * 2.Recorre todas las casillas desde el rey hasta la pieza atacante
     * en esa direccion
     * 3.Añade cada casilla intermedia a la lista de camino
     *
     * @param rey Pieza del rey que esta siendo atacado
     * @param atacante Pieza enemiga que esta atacando al rey
     * @return Lista de puntos (coordenadas) que forman el camino de ataque desde el rey hasta
     *         la pieza atacante
     *         Si no hay camino (por ejemplo, con un caballo) o los parametros
     *         son invalidos, devuelve una lista vacia
     */
    public ArrayList<Point> getCaminoAtaque(Pieza rey, Pieza atacante) {
        ArrayList<Point> camino = new ArrayList<>();

        if (rey == null || atacante == null || atacante.name.equals("Caballo")) {
            return camino;
        }

        if (atacante.name.equals("Peon")) {
            camino.add(new Point(atacante.columna, atacante.fila));
            return camino;
        }

        // Determina la direccion del ataque
        int colDir = 0;
        int filaDir = 0;

        if (atacante.columna != rey.columna) {
            colDir = atacante.columna > rey.columna ? 1 : -1;
        }
        if (atacante.fila != rey.fila) {
            filaDir = atacante.fila > rey.fila ? 1 : -1;
        }

        if (colDir == 0 && filaDir == 0) {
            return camino;
        }

        // Recorre el camino desde el rey hasta el atacante
        int c = rey.columna + colDir;
        int f = rey.fila + filaDir;


        while (c >= 0 && c < 8 && f >= 0 && f < 8) {
            camino.add(new Point(c, f));

            // Si llegamos a la posicion del atacante, terminamos
            if (c == atacante.columna && f == atacante.fila) {
                break;
            }

            c += colDir;
            f += filaDir;
        }

        return camino;
    }

    /**
     * Verifica si el rey esta siendo atacado por una torre o reina
     * en una direccion especifica
     * Este metodo analiza si hay alguna torre o reina enemiga que
     * podria capturar al rey moviendose en linea recta (horizontal o vertical)
     * desde su posicion actual
     * Tiene en cuenta la pieza que se esta moviendo para evitar falsos positivos
     *
     * @param columna Columna de la pieza que se esta moviendo en este turno
     * @param fila Fila de la pieza que se esta moviendo en este turno
     * @param rey Pieza del rey que se esta evaluando
     * @param reyColumna Columna actual o nueva del rey (dependiendo de si el rey es la pieza que se mueve)
     * @param reyFila Fila actual o nueva del rey (dependiendo de si el rey es la pieza que se mueve)
     * @param colValor Direccion en columnas a revisar: 1 (derecha), -1 (izquierda), o 0 (sin cambio)
     * @param filaValor Direccion en filas a revisar: 1 (abajo), -1 (arriba), o 0 (sin cambio)
     * @return true si el rey esta siendo atacado por una torre o reina en esa direccion, false en caso contrario
     */
    private static boolean hitByTorre(int columna, int fila, Pieza rey, int reyColumna, int reyFila, int colValor, int filaValor){
        for(int i=1; i<8; i++){
            int checkColumna = reyColumna + (i * colValor);
            int checkFila = reyFila + (i * filaValor);

            if(checkColumna < 0 || checkColumna > 7 || checkFila < 0 || checkFila > 7){
                break;
            }

            // Si estamos evaluando la posicion de la pieza que se esta moviendo, continuamos
            if(checkColumna == columna && checkFila == fila){
                continue;// Ignora la pieza que se esta moviendo
            }

            Pieza p = tablero.getPieza(checkColumna, checkFila);
            if(p != null){
                if(!tablero.sameColor(p, rey) && (p.name.equals("Torre") || p.name.equals("Reina"))){
                    return true;// Torre o reina enemiga encontrada
                }
                break; // Otra pieza bloquea el camino
            }
        }
        return false;
    }

    /**
     * Verifica si el rey esta siendo atacado por un alfil o reina en una
     * direccion diagonal especifica
     * Este metodo analiza si hay algun alfil o reina enemiga que podria
     * capturar al rey moviendose en diagonal desde su posicion actual
     * Tiene en cuenta la pieza que se esta moviendo para evitar falsos
     * positivos
     *
     * @param columna Columna de la pieza que se esta moviendo en este turno
     * @param fila Fila de la pieza que se esta moviendo en este turno
     * @param rey Pieza del rey que se esta evaluando
     * @param reyColumna Columna actual o nueva del rey (dependiendo de si el rey es la pieza que se mueve)
     * @param reyFila Fila actual o nueva del rey (dependiendo de si el rey es la pieza que se mueve)
     * @param colValor Direccion diagonal en columnas: 1 (derecha) o -1 (izquierda)
     * @param filaValor Direccion diagonal en filas: 1 (abajo) o -1 (arriba)
     * @return true si el rey esta siendo atacado por un alfil o reina en esa direccion diagonal, false en caso contrario
     */
    private static boolean hitByBishop(int columna, int fila, Pieza rey, int reyColumna, int reyFila, int colValor, int filaValor){
        for(int i=1; i<8; i++){
            int checkColumna = reyColumna + (i * colValor);
            int checkFila = reyFila + (i * filaValor);

            if(checkColumna < 0 || checkColumna > 7 || checkFila < 0 || checkFila > 7){
                break;
            }

            // Si estamos evaluando la posicion de la pieza que se esta moviendo, continuamos
            if(checkColumna == columna && checkFila == fila){
                continue;// Ignora la pieza que se está moviendo
            }

            Pieza p = tablero.getPieza(checkColumna, checkFila);
            if(p != null){
                // Comprueba tanto "Bishop" como "Alfil"
                if(!tablero.sameColor(p, rey) && (p.name.equals("Bishop") || p.name.equals("Alfil") || p.name.equals("Reina"))){
                    return true;// Alfil o reina enemiga encontrada
                }
                break;// Otra pieza bloquea el camino
            }
        }
        return false;
    }

    /**
     * Verifica si el rey esta siendo atacado por algun caballo enemigo
     * Este metodo comprueba todas las ocho posibles posiciones desde
     * donde un caballo podria estar atacando al rey, teniendo en cuenta
     * la pieza que se esta moviendo
     *
     * @param columna Columna de la pieza que se esta moviendo en este turno
     * @param fila Fila de la pieza que se esta moviendo en este turno
     * @param rey Pieza del rey que se esta evaluando
     * @param reyColumna Columna actual o nueva del rey
     * @param reyFila Fila actual o nueva del rey
     * @return true si el rey esta siendo atacado por al menos un caballo enemigo, false en caso contrario
     */
    private static boolean hitByCaballo(int columna, int fila, Pieza rey, int reyColumna, int reyFila){
        return JaqueCaballo(tablero.getPieza(reyColumna-1,reyFila-2),rey,columna,fila)||
                JaqueCaballo(tablero.getPieza(reyColumna+1,reyFila-2),rey,columna,fila)||
                JaqueCaballo(tablero.getPieza(reyColumna+2,reyFila-1),rey,columna,fila)||
                JaqueCaballo(tablero.getPieza(reyColumna+2,reyFila+1),rey,columna,fila)||
                JaqueCaballo(tablero.getPieza(reyColumna+1,reyFila+2),rey,columna,fila)||
                JaqueCaballo(tablero.getPieza(reyColumna-1,reyFila+2),rey,columna,fila)||
                JaqueCaballo(tablero.getPieza(reyColumna-2,reyFila+1),rey,columna,fila)||
                JaqueCaballo(tablero.getPieza(reyColumna-2,reyFila-1),rey,columna,fila);
    }

    /**
     * Verifica si una pieza especifica es un caballo enemigo que esta atacando al rey
     * Este metodo auxiliar es utilizado por hitByCaballo para determinar si una pieza
     * en una posicion particular es un caballo enemigo que pone en jaque al rey
     *
     * @param p Pieza a verificar (puede ser null si no hay pieza en esa posicion)
     * @param r Pieza del rey que se esta evaluando
     * @param columna Columna de la pieza que se esta moviendo en este turno
     * @param fila Fila de la pieza que se esta moviendo en este turno
     * @return true si la pieza es un caballo enemigo que amenaza al rey y no es la pieza que se esta moviendo,
     *         false en caso contrario o si p es null
     */
    private static boolean JaqueCaballo(Pieza p, Pieza r, int columna, int fila){
        return p!=null && !tablero.sameColor(p,r) && p.name.equals("Caballo") && !(p.columna == columna && p.fila==fila);
    }

    /**
     * Verifica si el rey esta siendo atacado por el rey enemigo
     * Este metodo comprueba todas las ocho casillas adyacentes al rey para
     * determinar si el rey enemigo esta presente en alguna de ellas
     *
     * @param rey Pieza del rey que se esta evaluando
     * @param reyColumna Columna actual o nueva del rey
     * @param reyFila Fila actual o nueva del rey
     * @return true si el rey esta siendo atacado por el rey enemigo, false en caso contrario
     */
    private static boolean hitByRey(Pieza rey, int reyColumna, int reyFila){
        return JaqueRey(tablero.getPieza(reyColumna-1,reyFila-1),rey)||
                JaqueRey(tablero.getPieza(reyColumna+1,reyFila-1),rey)||
                JaqueRey(tablero.getPieza(reyColumna,reyFila-1),rey)||
                JaqueRey(tablero.getPieza(reyColumna-1,reyFila),rey)||
                JaqueRey(tablero.getPieza(reyColumna+1,reyFila),rey)||
                JaqueRey(tablero.getPieza(reyColumna-1,reyFila+1),rey)||
                JaqueRey(tablero.getPieza(reyColumna+1,reyFila+1),rey)||
                JaqueRey(tablero.getPieza(reyColumna,reyFila+1),rey);
    }

    /**
     * Verifica si una pieza especifica es el rey enemigo
     * Este metodo auxiliar es utilizado por hitByRey para
     * determinar si una pieza en una posicion adyacente al
     * rey es el rey enemigo
     *
     * @param p Pieza a verificar (puede ser null si no hay pieza en esa posicion)
     * @param r Pieza del rey que se esta evaluando
     * @return true si la pieza es el rey enemigo, false en caso contrario o si p es null
     */
    private static boolean JaqueRey(Pieza p, Pieza r){
        return p!=null && !tablero.sameColor(p,r) && p.name.equals("Rey");
    }

    /**
     * Verifica si el rey esta siendo atacado por algun peon enemigo
     * Este metodo comprueba las dos posiciones diagonales delante del rey
     * (segun su color) donde podria haber un peon atacandolo
     *
     * @param columna Columna de la pieza que se esta moviendo en este turno
     * @param fila Fila de la pieza que se esta moviendo en este turno
     * @param rey Pieza del rey que se esta evaluando
     * @param reyColumna Columna actual o nueva del rey
     * @param reyFila Fila actual o nueva del rey
     * @return true si el rey esta siendo atacado por al menos un peon enemigo, false en caso contrario
     */
    private static boolean hitByPeon(int columna, int fila, Pieza rey, int reyColumna, int reyFila){
        int colorVal=rey.EsBlanco ? -1:1; // Direccion según el color del rey
        return JaquePeon(tablero.getPieza(reyColumna+1,reyFila+colorVal),rey,columna,fila)||
                JaquePeon(tablero.getPieza(reyColumna-1,reyFila+colorVal),rey,columna,fila);

    }

    /**
     * Verifica si una pieza especifica es un peon enemigo que esta atacando al rey
     * Este metodo auxiliar es utilizado por hitByPeon para determinar si una pieza
     * en una posicion diagonal al rey es un peon enemigo que pone en jaque al rey
     *
     * @param p Pieza a verificar (puede ser null si no hay pieza en esa posicion)
     * @param r Pieza del rey que se esta evaluando
     * @param columna Columna de la pieza que se esta moviendo en este turno
     * @param fila Fila de la pieza que se esta moviendo en este turno
     * @return true si la pieza es un peon enemigo que amenaza al rey y no es la pieza que se esta moviendo,
     *         false en caso contrario o si p es null
     */
    private static boolean JaquePeon(Pieza p, Pieza r, int columna, int fila){
        return p!=null && !tablero.sameColor(p,r) && p.name.equals("Peon") && !(p.columna==columna && p.fila==fila);
    }

    /**
     * Verifica si el juego ha terminado debido a un jaque mate
     * Este metodo determina si el rey no tiene movimientos validos para escapar del jaque,
     * si no es posible capturar a la pieza atacante, y si no es posible bloquear el ataque
     * Si todas estas condiciones se cumplen, entonces es jaque mate y el juego ha terminado
     *
     * Sigue los siguientes pasos:
     * 1. Verifica si el rey puede moverse a alguna casilla segura
     * 2. Si hay mas de un atacante, solo el rey puede salvarse (y ya verificamos que no puede)
     * 3. Si hay un solo atacante, intenta:
     *    a. Capturar al atacante con otra pieza
     *    b. Bloquear el camino entre el atacante y el rey (si es posible)
     *
     * @param rey Pieza del rey que se esta evaluando
     * @return true si es jaque mate (el juego ha terminado), false si el rey aun tiene opciones para escapar del jaque
     */
    public boolean esGameOver(Pieza rey){
        if (rey == null) {
            return false;
        }

        // Verificar si el rey se puede mover a alguna casilla segura
        for(int f = Math.max(0, rey.fila-1); f <= Math.min(7, rey.fila+1); f++){
            for (int c = Math.max(0, rey.columna-1); c <= Math.min(7, rey.columna+1); c++){
                if((c != rey.columna || f != rey.fila) && rey.esMovimientoValido(c, f) && !rey.movimientoChocaPieza(c, f)){
                    Movimientos mover = new Movimientos(tablero, rey, c, f);
                    // Si al mover no queda en jaque
                    if(!EsReyJaque(mover)){
                        return false;// No es jaque mate
                    }
                }
            }
        }

        // Obtener lista de piezas atacantes
        ArrayList<Pieza> atacantes = encontrarAtacantes(rey);

        // Si hay mas de un atacante, solo el rey puede salvarse
        if (atacantes.size() > 1) {
            return true;
        }

        // Si hay un solo atacante, podemos intentar capturarlo o bloquear el ataque
        if (atacantes.size() == 1) {
            Pieza atacante = atacantes.get(0);

            // Intentar capturar al atacante con otra pieza
            for (Pieza p : tablero.piezasList) {
                if (tablero.sameColor(p, rey) && p != rey) {
                    // Pieza del mismo color que el rey
                    if (p.esMovimientoValido(atacante.columna, atacante.fila) && !p.movimientoChocaPieza(atacante.columna, atacante.fila)) {
                        // Verificar si al capturar el atacante eliminamos el jaque
                        Movimientos mover = new Movimientos(tablero, p, atacante.columna, atacante.fila);
                        if (!EsReyJaque(mover)) {
                            return false;// No es jaque mate
                        }
                    }
                }
            }

            // Si el atacante es un caballo o peon en el primer movimiento, no podemos bloquear
            if (!atacante.name.equals("Caballo")) {
                // Los caballos no pueden ser bloqueados
                // Obtener el camino entre el atacante y el rey
                ArrayList<Point> camino = getCaminoAtaque(rey, atacante);

                // Intentar bloquear el camino con alguna pieza
                for (Point punto : camino) {
                    for (Pieza p : tablero.piezasList) {
                        // Pieza del mismo color que el rey
                        if (tablero.sameColor(p, rey) && p != rey) {
                            if (p.esMovimientoValido(punto.x, punto.y) && !p.movimientoChocaPieza(punto.x, punto.y)) {
                                // Verificar si al bloquear eliminamos el jaque
                                Movimientos mover = new Movimientos(tablero, p, punto.x, punto.y);
                                if (!EsReyJaque(mover)) {
                                    return false;// No es jaque mate
                                }
                            }
                        }
                    }
                }
            }
        }

        // Si no hay forma de evitar el jaque, es jaque mate
        return true;
    }
}
