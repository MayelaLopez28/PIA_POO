package Main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * Clase para manejar el historial de movimientos del juego de ajedrez
 * Gestiona tanto la representación visual en la interfaz grafica como
 * el almacenamiento de los movimientos para guardar y cargar partidas
 * Implementa Serializable para poder guardar el estado en archivos
 */
public class Historial implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient JList<String> movimientosList; // Componente visual que muestra la lista de movimientos
    private transient DefaultListModel<String> movimientosModel; // Modelo de datos para la JList
    private ArrayList<String> movimientos; // Lista que almacena los movimientos en notación algebraica


    /**
     * Constructor de la clase Historial
     * Inicializa la lista de movimientos y configura los componentes visuales
     */
    public Historial() {
        this.movimientos = new ArrayList<>(); // Inicializa la lista vacia de movimientos
        initComponents(); // Configura los componentes visuales
    }

    /**
     * Inicializa y configura los componentes visuales de la interfaz
     * Si ya existen, los reinicia con los datos actuales
     * Da formato a los movimientos para su visualizacion en formato de notación de ajedrez
     */
    public void initComponents() {
        // Inicializa o reinicia el modelo de la lista
        if (movimientosModel == null) {
            movimientosModel = new DefaultListModel<>();
        } else {
            movimientosModel.clear();// Limpia el modelo existente
        }

        // Inicializa o reinicia el componente JList
        if (movimientosList == null) {
            movimientosList = new JList<>(movimientosModel);
        } else {
            movimientosList.setModel(movimientosModel);// Asigna el modelo al componente
        }

        // Si hay movimientos, los carga en el modelo visual con formato adecuado
        if (movimientos != null) {
            for (int i = 0; i < movimientos.size(); i++) {
                String move = movimientos.get(i);
                if (i % 2 == 0) {
                    // Movimiento de blancas, número de jugada + movimiento
                    int moveNumber = (i / 2) + 1;
                    String formattedMove = String.format("%2d. %s", moveNumber, move);
                    movimientosModel.addElement(formattedMove);
                } else {
                    // Movimiento de negras, se agrega al final de la linea del movimiento de blancas
                    String last = movimientosModel.get(movimientosModel.size() - 1);
                    String formattedMove = String.format("%-20s %s", last, move);
                    movimientosModel.set(movimientosModel.size() - 1, formattedMove);
                }
            }
        }

        // Configura propiedades visuales de la lista
        movimientosList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Solo permite seleccion individual
        movimientosList.setLayoutOrientation(JList.VERTICAL);// Lista con orientacion vertical
    }

    /**
     * Metodo para serialización
     * Permite guardar correctamente el estado del objeto al serializar
     *
     * @param out Stream de salida para serializacion
     * @throws java.io.IOException Si ocurre un error al escribir en el stream
     */
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject(); // Realiza la serializacion
    }

    /**
     * Metodo para deserializacion
     * Permite reconstruir el objeto correctamente al deserializar, incluyendo
     * el reinicio de componentes transient que no se serializan
     *
     * @param in Stream de entrada para deserializacion
     * @throws java.io.IOException Si ocurre un error al leer del stream
     * @throws ClassNotFoundException Si no se encuentra la clase durante la deserializacion
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();// Realiza la deserializacion
        initComponents();// Reconstruye los componentes transient despues de deserializar
    }

    /**
     * Añade un nuevo movimiento al historial y actualiza la interfaz
     * Da formato al movimiento segun sean blancas o negras, y actualiza
     * la visualizacion manteniendo el formato de notacion de ajedrez
     *
     * @param move Movimiento en notacion algebraica a añadir
     */
    public void addMovimiento(String move) {
        String formattedMove = move;

        if (movimientos.size() % 2 == 0) {
            // Si es un movimiento de blancas (par), crea una nueva entrada
            int moveNumber = (movimientos.size() / 2) + 1;
            formattedMove = String.format("%2d. %-10s", moveNumber, move);
            movimientosModel.addElement(formattedMove);
        } else {
            // Si es un movimiento de negras (impar), lo añade a la línea existente
            String last = movimientosModel.get(movimientosModel.size() - 1);
            formattedMove = String.format("%s %-10s", last, move);
            movimientosModel.set(movimientosModel.size() - 1, formattedMove);
        }
        movimientos.add(move);// Guarda el movimiento original en la lista

        // Auto-scroll para mantener visible el último movimiento
        if (movimientosList != null) {
            movimientosList.ensureIndexIsVisible(movimientosModel.size() - 1);
        }
    }

    /**
     * Crea y devuelve un panel con scroll que contiene el historial de movimientos
     * Es util para integrarlo en la interfaz de usuario del tablero
     *
     * @return JScrollPane con la lista de movimientos configurada
     */
    public JScrollPane getScrollPane() {
        // Si los componentes no estan inicializados, los inicializa
        if (movimientosList == null || movimientosModel == null) {
            initComponents();
        }

        // Crea un panel de desplazamiento para la lista
        JScrollPane scrollPane = new JScrollPane(movimientosList);
        scrollPane.setPreferredSize(new Dimension(200, 600));// Tamaño recomendado
        return scrollPane;
    }

    /**
     * Obtiene la lista de todos los movimientos en su formato original
     * Es util para guardar la partida o analizar la secuencia de movimientos
     *
     * @return ArrayList con los movimientos
     */
    public ArrayList<String> getMovimientos() {
        return this.movimientos;
    }

    /**
     * Establece la lista completa de movimientos y actualiza la interfaz
     * Es util al cargar una partida guardada anteriormente
     *
     * @param movimientos Lista de movimientos
     */
    public void setMovimientos(ArrayList<String> movimientos) {
        this.movimientos = movimientos;
        initComponents();// Reconstruye la interfaz con los nuevos movimientos
    }

    /**
     * Obtiene el modelo de lista utilizado para la interfaz grafica
     * Permite realizar operaciones avanzadas sobre el modelo de datos
     *
     * @return DefaultListModel con los movimientos
     */
    public DefaultListModel<String> getMovimientosModel() {
        return movimientosModel;
    }

}