package Main;

import Piezas.Pieza;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Clase que implementa mejoras visuales para la interfaz del juego de ajedrez
 * Proporciona elementos como el reloj de los jugadores, etiquetas de estado
 * (para mostrar turno, jaque) y botones para guardar la partida
 */
public class MejorasVisuales extends JPanel {
    private JLabel estatus; // Etiqueta para mostrar el estado actual del juego (turno, jaque, mate)
    private Tablero tablero; // Referencia al tablero principal del juego

    private JLabel blancoClock; // Etiqueta para mostrar el tiempo del jugador blanco
    private JLabel negroClock; // Etiqueta para mostrar el tiempo del jugador negro
    private transient Clock clock;// Objeto que maneja la logica del reloj

    private JButton saveButton; // Boton para guardar la partida

    /**
     * Constructor de MejorasVisuales
     * Inicializa y configura todos los componentes visuales adicionales
     * como reloj, etiquetas de estado y botones de accion
     *
     * @param tablero Hace referencia al tablero del juego para actualizar estados y acciones
     */
    public MejorasVisuales(Tablero tablero) {
        this.tablero = tablero;

        // Configura el panel principal
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setPreferredSize(new Dimension(tablero.getWidth(), 30));
        setBackground(Color.WHITE);

        // Inicializa componentes del reloj
        blancoClock = new JLabel("10:00"); // Etiqueta inicial para tiempo blanco
        negroClock = new JLabel("10:00");// Etiqueta inicial para tiempo negro
        clock = new Clock(10, blancoClock, negroClock, tablero);// Reloj con 10 minutos por jugador

        // Inicializa etiqueta de estado
        estatus = new JLabel();
        estatus.setForeground(Color.BLACK);
        estatus.setFont(new Font("Arial", Font.BOLD, 22));

        // Inicializa etiqueta de estado
        saveButton = new JButton("Guardar");
        configurarBoton(saveButton, new Dimension(100, 25));

        // Añade todos los componentes al panel en el orden deseado
        add(blancoClock);
        add(estatus);
        add(negroClock);
        add(saveButton);

        // Configura los eventos de los botones y otros componentes
        configurarEventos();

        // Inicia el reloj y actualiza la visualizacion inicial
        clock.start();
        updateEstatus();
    }

    /**
     * Configura las propiedades visuales de los botones
     * Establece wl aspcto para todos los botones de la interfaz
     * con colores, bordes y tipografia consistentes
     *
     * @param boton Representa el boton a configurar
     * @param dimension Representa las dimensiones que se le daran al boton
     */
    private void configurarBoton(JButton boton, Dimension dimension) {
        // Establece el tamaño fijo del boton
        boton.setPreferredSize(dimension);
        boton.setMaximumSize(dimension);

        // Establece colores personalizados
        boton.setBackground(new Color(0xB83556));
        boton.setForeground(Color.WHITE);

        // Elimina el borde azul al hacer foco
        boton.setFocusPainted(false);

        // Establece la fuente para el texto del boton
        boton.setFont(new Font("Arial", Font.BOLD, 12));
    }

    /**
     * Configura los eventos de los componentes interactivos
     * Establece los listeners para manejar las acciones del usuario
     * Actualmente maneja el evento de guardar partida
     */
    private void configurarEventos() {
        saveButton.addActionListener(new ActionListener() {
            // Evento del boton guardar
            // muestra un dialogo para seleccionar ubicacion y nombre del archivo
            @Override
            public void actionPerformed(ActionEvent e) {
                pausarReloj();// Pausa el reloj mientras se guarda

                // Muestra un dialogo para elegir ubicacion y nombre
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showSaveDialog(null);

                // Si el usuario selecciona una ubicacion ,guarda el juego
                if(result == JFileChooser.APPROVE_OPTION) {
                    Guardar.saveGame(tablero, fileChooser.getSelectedFile().getPath());
                }

                // Reanuda el reloj solo si el juego no ha terminado
                if(!tablero.GameOver) {
                    reanudarReloj();
                }
            }
        });
    }

    /**
     * Pausa el reloj del juego
     * Detiene temporalmente la cuenta atras del reloj sin detener el temporizador
     * Es util durante dialogos o cuando se necesita interrumpir el juego
     */
    public void pausarReloj() {
        if (clock != null) {
            clock.pause();//Llama al metodo pause del reloj
        }
    }

    /**
     * Reanuda el reloj del juego tras una pausa
     * Solo reanuda si el juego no ha terminado para evitar
     * seguir contando tiempo innecesariamente
     */
    public void reanudarReloj() {
        if (clock != null && !tablero.GameOver) {
            clock.resume();// Llama al metodo resume del reloj
        }
    }

    /**
     * Obtiene la instancia del reloj del juego
     * Permite a otras clases acceder y manipular el reloj directamente
     *
     * @return Instancia de Clock utilizada para este juego
     */
    public Clock getReloj() {
        return clock;
    }

    /**
     * Cambia el turno en el reloj
     * Indica al reloj que debe comenzar a descontar tiempo del otro jugador
     * Se llama despues de cada movimiento valido en el tablero
     */
    public void cambiarClock() {
        clock.cambioTurno();// Notifica al reloj del cambio de turno
    }

    /**
     * Actualiza la etiqueta de estado del juego
     * Refleja el turno actual, si hay jaque o jaque mate, o si hay tablas
     * Utiliza colores para destacar situaciones importantes (rojo para jaque o jaque mate)
     */
    public void updateEstatus() {
        // Determina de quien es el turno
        String turno = tablero.TurnoBlanco ? "Blancas" : "Negras";
        String estado = "";
        Color colorEstado = Color.BLACK; // Color por defecto para el texto

        // Busca el rey del jugador actual para verificar si esta en jaque
        Pieza rey = tablero.encontrarRey(tablero.TurnoBlanco);
        if (rey != null && JaqueScanner.EsReyJaque(new Movimientos(tablero, rey, rey.columna, rey.fila))) {
            // Si hay jaque, lo indica y cambia el color a rojo
            estado = " - ¡JAQUE!";
            colorEstado = Color.RED;
        }

        // Si el juego ha terminado, actualiza el mensaje final
        if (tablero.GameOver) {
            if (rey != null && JaqueScanner.EsReyJaque(new Movimientos(tablero, rey, rey.columna, rey.fila))) {
                // Jaque mate, el jugador en turno ha perdido
                estado = " - ¡JAQUE MATE! " + (tablero.TurnoBlanco ? "Negras ganan" : "Blancas ganan");
            } else {
                // No hay jaque pero el juego termino, tablas por ahogado
                estado = " - Tablas por ahogado";
            }
            colorEstado = new Color(0x8B0000);
        }

        // Actualiza la etiqueta con el nuevo estado usando formato HTML para colores
        estatus.setText("<html><b>Turno:</b> " + turno + "<font color='" +
                String.format("#%02x%02x%02x", colorEstado.getRed(),
                        colorEstado.getGreen(), colorEstado.getBlue()) + "'>" +
                estado + "</font></html>");
    }
}