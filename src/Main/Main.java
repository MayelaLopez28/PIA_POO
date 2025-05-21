package Main;

import javax.swing.*;
import java.awt.*;
import java.io.*;


/**
 * Clase principal del juego
 * Gestiona la ventana principal, la interfaz grafica y la inicializacion del juego
 * Contiene la logica para iniciar nuevas partidas, cargar partidas guardadas
 * y visualizar el historial de jugadas
 */
public class Main {
    // Componentes principales de la aplicacion
    private JFrame ventana;//Ventana principal del juego
    private Tablero tablero;//Componente que representa el tablero de ajedrez
    private MejorasVisuales mejorasVisuales;//Componente que contiene elementos visuales adicionales como cronometro
    private Historial historial;//Componente que almacena y muestra el historial de movimientos
    private JPanel panelContenedor;//Panel contenedor principal que organiza todos los componentes


    /**
     * Metodo principal que inicia la aplicacion
     * Utiliza SwingUtilitiesinvokeLater para garantizar que la creacion de la interfaz
     * se realice en el hilo de eventos de Swing
     *
     * @param args Argumentos de la linea de comandos
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }

    /**
     * Constructor de la clase Main
     * Inicializa la aplicacion mostrando el menu principal
     */
    public Main() {
        iniciarMenuPrincipal();
    }

    /**
     * Configura las propiedades visuales de un boton para mantener
     * una apariencia consistente en toda la interfaz
     *
     * @param boton Boton a configurar con el estilo visual
     * @param dimension Dimensiones del boton
     */
    private void configurarBoton(JButton boton, Dimension dimension) {
        boton.setPreferredSize(dimension);// Establece el tamaño preferido
        boton.setMaximumSize(dimension);// Limita el tamaño maximo
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);// Centra horizontalmente
        boton.setBackground(new Color(0xB83556));// Color de fondo
        boton.setForeground(Color.WHITE);// Color del texto
        boton.setFocusPainted(false); // Elimina el borde de foco
        boton.setFont(new Font("Arial", Font.BOLD, 16));
    }

    /**
     * Crea y muestra el menu principal del juego
     * Inicializa la interfaz con botones para las
     * diferentes opciones del juego
     */
    void iniciarMenuPrincipal() {
        // Crear ventana de menu principal con configuracion basica
        ventana = new JFrame("Ajedrez");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// Cerrar aplicacion al cerrar la venta
        ventana.setSize(500, 600);// Dimensiones de la ventana
        ventana.setLocationRelativeTo(null);// Centrar en pantalla

        // Panel principal del menu con disposicion vertical (BoxLayout)
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(0x762E3F));// Color de fondo

        // Titulo del juego
        JLabel titulo = new JLabel("AJEDREZ");
        titulo.setFont(new Font("Arial", Font.BOLD, 36));
        titulo.setForeground(new Color(0xE8DDDD));// Color para el texto
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);// Centrado horizontal

        // Creacion de los botones para las diferentes opciones
        JButton nuevaPartidaBtn = new JButton("Nueva Partida");
        JButton cargarPartidaBtn = new JButton("Cargar Partida");
        JButton verHistorialBtn = new JButton("Ver Historial");
        JButton salirBtn = new JButton("Salir");

        // Aplicar el mismo estilo visual a todos los botones
        Dimension btnDimension = new Dimension(200, 40);
        configurarBoton(nuevaPartidaBtn, btnDimension);
        configurarBoton(cargarPartidaBtn, btnDimension);
        configurarBoton(verHistorialBtn, btnDimension);
        configurarBoton(salirBtn, btnDimension);

        // Configuracion de los eventos para cada boton
        nuevaPartidaBtn.addActionListener(e -> iniciarNuevaPartida());// Iniciar juego nuevo
        cargarPartidaBtn.addActionListener(e -> cargarPartida());// Cargar partida guardada
        verHistorialBtn.addActionListener(e -> verHistorialPartidas());// Ver historial de partidas
        salirBtn.addActionListener(e -> System.exit(0));// Salir de la aplicacion

        // Agregar componentes al panel con espaciado para mejorar la apariencia
        menuPanel.add(Box.createVerticalGlue());// Espacio flexible arriba
        menuPanel.add(titulo);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 30)));// Espacio fijo entre titulo y botones
        menuPanel.add(nuevaPartidaBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 15)));// Espacio entre botones
        menuPanel.add(cargarPartidaBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        menuPanel.add(verHistorialBtn);  // Agregar nuevo boton
        menuPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        menuPanel.add(salirBtn);
        menuPanel.add(Box.createVerticalGlue());// Espacio flexible abajo

        // Añadir el panel al frame y hacerlo visible
        ventana.add(menuPanel);
        ventana.setVisible(true);
    }


    /**
     * Inicia una nueva partida de ajedrez
     * Cierra la ventana actual y crea una nueva con el tablero de juego
     */
    private void iniciarNuevaPartida() {
        ventana.dispose();// Cierra la ventana actual
        iniciarJuego(false, null);// Inicia el juego sin cargar archivo
    }

    /**
     * Permite al usuario seleccionar y cargar una partida guardada
     * Muestra un dialogo de seleccion de archivo para elegir la partida a cargar
     */
    private void cargarPartida() {
        JFileChooser fileChooser = new JFileChooser();

        // Si el usuario selecciona un archivo y confirma
        if (fileChooser.showOpenDialog(ventana) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            ventana.dispose();// Cierra la ventana actual
            iniciarJuego(true, archivo.getPath());// Inicia el juego cargando el archivo seleccionado
        }
    }

    /**
     * Configura e inicia el entorno del juego de ajedrez
     * Crea todos los componentes necesarios para jugar y
     * los organiza en la interfaz
     *
     * @param cargar true si se esta cargando una partida guardada
     * @param rutaArchivo Ruta del archivo a cargar (puede ser null)
     */
    private void iniciarJuego(boolean cargar, String rutaArchivo) {
        // Crear y configurar la ventana principal
        ventana = new JFrame("Ajedrez");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setResizable(false);// Impedir redimensionar la ventana

        // Inicializar los componentes principales del juego
        tablero = new Tablero();// Tablero de ajedrez
        historial = new Historial(); // Historial de movimientos

        // Panel contenedor principal con disposicion de bordes
        panelContenedor = new JPanel(new BorderLayout());

        // Crear el panel de mejoras visuales (cronometro, estado)
        mejorasVisuales = new MejorasVisuales(tablero);

        // Configurar referencias cruzadas entre componentes
        tablero.setMejorasVisuales(mejorasVisuales);
        tablero.setHistorial(historial);

        // Crear panel de botones con el boton para volver al menu
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton volverMenuBtn = new JButton("Volver al Menu");
        volverMenuBtn.addActionListener(e -> volverAlMenu());
        panelBotones.add(volverMenuBtn);

        // Estructurar la interfaz añadiendo los componentes al panel principal
        panelContenedor.add(mejorasVisuales, BorderLayout.NORTH);// Mejoras visuales arriba
        panelContenedor.add(tablero, BorderLayout.CENTER); // Tablero en el centro
        panelContenedor.add(historial.getScrollPane(), BorderLayout.EAST);// Historial a la derecha
        panelContenedor.add(panelBotones, BorderLayout.SOUTH);// Botones abajo

        // Añadir el panel contenedor a la ventana y mostrarla
        ventana.add(panelContenedor);
        ventana.pack();// Ajustar tamaño segun los componentes
        ventana.setLocationRelativeTo(null);// Centrar en pantalla
        ventana.setVisible(true);

        // Si se esta cargando una partida guardada, cargar el estado desde el archivo
        if (cargar && rutaArchivo != null) {
            Guardar.loadGame(tablero, rutaArchivo);
        }
    }

    /**
     * Permite visualizar el historial de movimientos de partidas guardadas
     * Muestra un dialogo para seleccionar un archivo de historial y lo
     * presenta en una ventana
     */
    public void verHistorialPartidas() {
        // Crear selector de archivos con filtro para archivos de historial
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            // Filtrar para mostrar solo archivos con extension _movimientostxt
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith("_movimientos.txt") || f.isDirectory();
            }
            public String getDescription() {
                return "Archivos de historial de partidas (*_movimientos.txt)";
            }
        });

        // Si el usuario selecciona un archivo y confirma
        if (fileChooser.showOpenDialog(ventana) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try {
                // Mostrar el contenido del archivo de movimientos
                StringBuilder contenido = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                    String linea;
                    // Leer el archivo linea por linea
                    while ((linea = reader.readLine()) != null) {
                        contenido.append(linea).append("\n");
                    }
                }

                // Crear un area de texto para mostrar el contenido
                JTextArea textArea = new JTextArea(contenido.toString(), 20, 40);
                textArea.setEditable(false);//Impedir la edicion
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

                // Mostrar el area de texto en un dialogo con barra de desplazamiento
                JScrollPane scrollPane = new JScrollPane(textArea);
                JOptionPane.showMessageDialog(ventana, scrollPane,
                        "Historial de Partida: " + archivo.getName(),
                        JOptionPane.PLAIN_MESSAGE);

            } catch (IOException e) {
                // Mostrar mensaje de error si no se puede leer el archivo
                JOptionPane.showMessageDialog(ventana,
                        "Error al leer el archivo de historial: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Cierra la ventana actual del juego y vuelve al menu principal
     * Permite al usuario abandonar la partida actual sin cerrar la aplicacion
     */
    private void volverAlMenu() {
        ventana.dispose();// Cerrar la ventana actual
        iniciarMenuPrincipal();// Mostrar nuevamente el menu principal
    }


}
