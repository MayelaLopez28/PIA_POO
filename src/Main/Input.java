package Main;

import Piezas.Pieza;


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;

/**
 * Clase que maneja todos los eventos de entrada del usuario mediante el mouse
 * Implementa las interfaces MouseListener y MouseMotionListener para capturar
 * los eventos del mouse como clicks, arrastres y liberaciones
 * Tambien implementa Serializable para permitir la persistencia del estado en partidas guardadas
 */
public class Input implements MouseListener, MouseMotionListener, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Referencia al tablero de ajedrez
     * Se marca como transient para que no sea serializado directamente,
     * ya que la referencia al tablero se restablece despues de la deserializacion
     */
    private transient Tablero tablero;

    /**
     * Constructor que inicializa la clase Input con una referencia al tablero de juego
     *
     * @param tablero Tablero de juego al que estara asociado este gestor de eventos
     */
    public Input(Tablero tablero) {
        this.tablero=tablero;
    }

    /**
     * Establece una nueva referencia al tablero de juego
     * Este metodo es util despues de la deserializacion para restablecer la conexion
     * entre el input y el tablero
     *
     * @param tablero Nueva referencia al tablero de juego
     */
    public void setTablero(Tablero tablero) {
        this.tablero = tablero;
    }

    /**
     * Implementacion del metodo mouseClicked de la interfaz MouseListener.
     * Este metodo no se utiliza en esta implementacion, pero debe ser definido
     * para satisfacer la interfaz.
     *
     * @param e Evento de clic del mouse
     */

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Maneja el evento que ocurre cuando el usuario presiona un boton del mouse
     * Este metodo selecciona una pieza si el usuario hace clic en una casilla que contiene
     * una pieza del color que tiene el turno actual.
     *
     * @param e Evento que contiene informacion sobre la posicion donde se presiono el mouse
     */
    @Override
    public void mousePressed(MouseEvent e) {

        // Verificar que el tablero exista
        if (tablero == null) return;

        // Calcular la columna y fila de la casilla donde se hizo clic
        int columna=e.getX()/ tablero.tileSize;
        int fila=e.getY()/ tablero.tileSize;

        // Verificar que las coordenadas esten dentro del tablero
        if(columna<0 ||columna>=8 || fila<0 ||fila>=8){
            return;
        }

        // Obtener la pieza en la posicion del clic
         Pieza piezaXY = tablero.getPieza(columna,fila);
        // Selecciona la pieza solo si existe y pertenece al jugador con el turno actual
         if(piezaXY!=null && piezaXY.EsBlanco==tablero.TurnoBlanco) {
             tablero.piezaSeleccionada=piezaXY;
         }
    }

    /**
     * Maneja el evento que ocurre cuando el usuario arrastra el mouse
     * Este metodo actualiza la posicion visual de la pieza seleccionada
     * para seguir el puntero del mouse, creando la ilusion de que el usuario
     * esta arrastrando la pieza
     *
     * @param e Evento que contiene informacion sobre la posicion actual del mouse durante el arrastre
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        // Verificar que el tablero exista y haya una pieza seleccionada
        if (tablero == null || tablero.piezaSeleccionada == null) {
            return;
        }

        // Actualizar la posicion visual de la pieza seleccionada
        // Se centra la pieza en el cursor restando la mitad del tamaño de la casilla
        if(tablero.piezaSeleccionada!=null) {
            tablero.piezaSeleccionada.xPos=e.getX()- tablero.tileSize/2;
            tablero.piezaSeleccionada.yPos=e.getY()-tablero.tileSize/2;

            // Solicitar al tablero que se redibuje para mostrar la pieza en su nueva posicion
            tablero.repaint();
        }
    }

    /**
     * Implementacion del metodo mouseMoved de la interfaz MouseMotionListener
     * Este metodo no se utiliza en esta implementacion, pero debe ser definido
     * para satisfacer la interfaz
     *
     * @param e Evento de movimiento del mouse
     */
    @Override
    public void mouseMoved(MouseEvent e) {

    }

    /**
     * Maneja el evento que ocurre cuando el usuario libera un boton del mouse
     * Este metodo intenta realizar un movimiento con la pieza seleccionada a la casilla
     * donde se solto el mouse
     * Si el movimiento es valido, se ejecuta, si no, la pieza
     * regresa a su posicion original
     *
     * @param e Evento que contiene informacion sobre la posicion donde se libero el mouse
     */
    @Override
    public void mouseReleased(MouseEvent e) {

        // Verificar que el tablero exista y haya una pieza seleccionada
        if (tablero == null || tablero.piezaSeleccionada == null) {
            return;
        }

        // Calcular la columna y fila de la casilla donde se solto la pieza
        int columna=e.getX()/ tablero.tileSize;
        int fila=e.getY()/ tablero.tileSize;

        // Crear un objeto de movimiento con la pieza seleccionada y la posicion destino
        Movimientos mover = new Movimientos(tablero, tablero.piezaSeleccionada, columna, fila);

        // Verificar si el movimiento es valido segun las reglas del ajedrez
        if (tablero.esMovimientoValido(mover)) {
            // Ejecutar el movimiento si es valido
            tablero.hacerMovimiento(mover);
        } else {
            // Si el movimiento no es valido, devolver la pieza a su posicion original
            tablero.piezaSeleccionada.xPos = tablero.piezaSeleccionada.columna * tablero.tileSize;
            tablero.piezaSeleccionada.yPos = tablero.piezaSeleccionada.fila * tablero.tileSize;
        }

        // Desselecciona la pieza despues de intentar el movimiento
        tablero.piezaSeleccionada = null;
        // Redibuja el tablero para reflejar los cambios
        tablero.repaint();

    }

    /**
     * Implementacion del metodo mouseEntered de la interfaz MouseListener
     * Este metodo se activa cuando el cursor del mouse entra en el componente,
     * pero no se utiliza en esta implementacion
     *
     * @param e Evento de entrada del mouse
     */
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * Implementacion del metodo mouseExited de la interfaz MouseListener
     * Este metodo se activa cuando el cursor del mouse sale del componente,
     * pero no se utiliza en esta implementacion
     *
     * @param e Evento de salida del mouse
     */
    @Override
    public void mouseExited(MouseEvent e) {

    }


}
