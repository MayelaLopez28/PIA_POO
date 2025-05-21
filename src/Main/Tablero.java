package Main;

import Piezas.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.stream.Collectors;


/**
 * Clase que representa el tablero de ajedrez
 * Gestiona la logica del juego, el dibujo del tablero y las piezas,
 * asi como la validacion de movimientos y reglas especiales del ajedrez
 */
public class Tablero extends JPanel {

    //Notacion FEN (Forsyth-Edwards Notation) para la posicion inicial estandar del ajedrez
    public String fenStartingPosition="rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    // Tamaño en pixeles de cada casilla del tablero
    public int tileSize = 85;

    //Numero de columnas y filas del tablero
    int columna=8;
    int fila=8;

    //Lista que contiene todas las piezas actualmente en el tablero
    public ArrayList<Pieza>piezasList =new ArrayList<>();

    //Referencia a la pieza que el jugador ha seleccionado para mover
    public Pieza piezaSeleccionada;

    // Escaner que verifica situaciones de jaque y jaque mate
    protected JaqueScanner js;

    Color fondo = new Color(0x762E3F);//Color de fondo del panel del tablero
    Color claro=new Color(0xE8DDDD);//Color de las casillas claras del tablero
    Color oscuro=new Color(0xB83556);//Color de las casillas oscuras del tablero

    //Indice de la casilla donde se puede realizar la captura al paso (-1 si no esta disponible)
    public int enPassantTile=-1;

    public boolean TurnoBlanco=true;//Indica si es el turno de las piezas blancas (true) o negras (false)
    public boolean GameOver=false;//Indica si el juego ha terminado

    private MejorasVisuales mv;//Referencia a las mejoras visuales del tablero
    private Historial h;//Referencia al historial de movimientos
    private Input i;//Gestor de entrada de usuario (clicks del mouse)

    /**
     * Constructor del tablero
     * Inicializa el componente, configura el tamaño, colores
     * y carga la posicion inicial
     */
    public Tablero() {
        this.js=new JaqueScanner(this);// Inicializa el escaner de jaque
        this.setPreferredSize(new Dimension(columna*tileSize,fila*tileSize));// Configura el tamaño del tablero basado en las dimensiones y tamaño de casilla
        this.setBackground(fondo);// Establece el color de fondo del panel

        // Inicializa el sistema de entrada y el escaner de jaque
        initInput();
        initJaqueScanner();

        // Carga la posicion inicial estandar del ajedrez
        loadPosition(fenStartingPosition);

    }

    /**
     * Inicializa el sistema de entrada de usuario (manejo de eventos del mouse)
     * Configura los listeners para detectar clicks y movimientos del mouse
     */
    private void initInput() {
        // Crea un nuevo objeto Input asociado a este tablero
        this.i = new Input(this);

        // Registra los listeners para eventos de mouse
        this.addMouseListener(i);
        this.addMouseMotionListener(i);
    }

    /**
     * Reconstruye el sistema de entrada, eliminando y
     * volviendo a añadir los listeners
     * Util despues de deserializar un tablero guardado
     */
    public void rebuildInput() {
        // Elimina los listeners existentes
        this.removeMouseListener(i);
        this.removeMouseMotionListener(i);

        // Vuelve a inicializar el sistema de entrada
        initInput();
    }

    /**
     * Inicializa el escaner de jaque que detecta
     * situaciones de jaque y jaque mate
     */
    private void initJaqueScanner() {
        // Crea un nuevo escaner de jaque asociado a este tablero
        this.js = new JaqueScanner(this);
    }

    /**
     * Reconstruye el escaner de jaque (util despues de deserializacion)
     * * Necesario porque las referencias se pierden durante la deserializacion
     */
    public void rebuildJaqueScanner() {
        this.js = new JaqueScanner(this);
    }

    /**
     * Establece las mejoras visuales para el tablero
     * @param mv Mejoras visuales a establecer
     */
    public void setMejorasVisuales(MejorasVisuales mv) {
        this.mv = mv;
    }

    /**
     * Obtiene las mejoras visuales
     * @return Mejoras visuales actuales
     */
    public MejorasVisuales getMejorasVisuales(){
        return this.mv;
    }

    /**
     * Obtiene el historial de movimientos
     * @return Historial de movimientos
     */
    public Historial getHistorial() {
        return this.h;
    }

    /**
     * Establece el historial de movimientos
     * @param h Historial de movimientos a establecer
     */
    public void setHistorial(Historial h) {
        this.h=h;
        if (this.h != null) {
            this.h.initComponents(); // Inicializar componentes si no lo estan
        }
    }

    /**
     * Metodo para deserializacion
     * Reconstruye objetos no serializables y restaura referencias
     *
     * @param in Stream de entrada
     * @throws IOException Si ocurre un error
     * @throws ClassNotFoundException Si no se encuentra la clase
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // Realiza la deserializacion por defecto
        in.defaultReadObject();

        // Reconstruye componentes no serializables
        this.js = new JaqueScanner(this);
        this.i = new Input(this);

        // Reinicializa los componentes del historial si existe
        if (this.h != null) {
            this.h.initComponents();
            // Forzar actualizacion de la vista
            if (this.h.getScrollPane() != null) {
                this.h.getScrollPane().revalidate();
                this.h.getScrollPane().repaint();
            }
        }

        // Restaura la referencia al tablero en cada pieza
        if (piezasList != null) {
            for (Pieza p : piezasList) {
                if (p != null) {
                    p.setTablero(this);
                }
            }
        }

        // Recarga las imagenes de las piezas que se perdieron en la serializacion
        if (piezasList != null) {
            for (Pieza p : piezasList) {
                if (p != null && p.getImagen() == null) {
                    p.loadImageFromPath();
                }
            }
        }
    }

    /**
     * Obtiene una pieza en una posicion especifica
     *
     * @param columna Columna de la pieza
     * @param fila Fila de la pieza
     * @return Pieza en la posicion o null si no hay pieza
     */
    public Pieza getPieza(int columna, int fila) {
        // Recorre la lista de piezas para encontrar una en la posicion dada
        for(Pieza p : piezasList){
            if(p.columna== columna && p.fila== fila){
                return p;
            }
        }

        // Si no se encuentra ninguna pieza, devuelve null
        return null;
    }


    /**
     * Realiza un movimiento en el tablero
     * Ejecuta todas las acciones asociadas a un movimiento valido
     *
     * @param mover Movimiento a realizar
     */
    public void hacerMovimiento(Movimientos mover){
        // Registra el movimiento en el historial si esta disponible
        if(h != null) {
            String piezaNombre = obtenerNombre(mover.pieza.name);
            String color = TurnoBlanco ? "Blanco" : "Negro";

            String movimiento = String.format("%s %s a %c%d",
                    piezaNombre,
                    color,
                    (char)('a' + mover.newColumna),
                    (8 - mover.newFila));

            h.addMovimiento(movimiento);
        }

        // Reproduce el sonido correspondiente segUn sea captura o movimiento normal
        if(mover.captura != null) {
            Sonido.playCaptureSound();
        } else {
            Sonido.playMoveSound();
        }


        // Maneja movimientos especiales segUn el tipo de pieza
        if(mover.pieza.name.equals("Peon")) {
            // Maneja movimientos especiales del peon (en passant, promocion)
            moverPeon(mover);
        } else {
            // Si no es un peon, resetea la casilla de captura al paso
            enPassantTile = -1;
        }

        if(mover.pieza.name.equals("Rey")) {
            // Maneja el enroque si es un rey
            moverRey(mover);
        }

        // Actualiza la posicion de la pieza movida
        mover.pieza.columna = mover.newColumna;
        mover.pieza.fila = mover.newFila;
        mover.pieza.xPos = mover.newColumna * tileSize;
        mover.pieza.yPos = mover.newFila * tileSize;
        mover.pieza.esPrimerMovimiento = false;

        // Elimina la pieza capturada si la hay
        captura(mover.captura);

        // Cambiar turno despues de registrar
        TurnoBlanco = !TurnoBlanco;

        // Actualiza las mejoras visuales si estan disponibles
        if(mv != null) {
            mv.cambiarClock();
            mv.updateEstatus();
        }

        // Verifica si el juego ha terminado (jaque mate, ahogado)
        actualizarJuego();


    }

    /**
     * Convierte el nombre de la pieza de ingles a español
     * Sirve mas que nada para el alfil
     * @param nombre Nombre de la pieza
     * @return Nombre en español de la pieza
     */
    private String obtenerNombre(String nombre) {
        switch(nombre) {
            case "Bishop": return "Alfil";
            case "Caballo": return "Caballo";
            case "Peon": return "Peon";
            case "Torre": return "Torre";
            case "Reina": return "Reina";
            case "Rey": return "Rey";
            default: return nombre;
        }
    }


    /**
     * Maneja el movimiento especial del rey (enroque)
     * Si el rey se mueve dos casillas, mueve tambien la torre correspondiente
     *
     * @param mover Movimiento del rey
     */
    private void moverRey(Movimientos mover){

        // Verifica si el movimiento es un enroque (movimiento horizontal de 2 casillas)
        if(Math.abs(mover.pieza.columna-mover.newColumna)==2){
            Pieza torre;

            // Enroque corto (hacia la derecha
            if(mover.pieza.columna<mover.newColumna){
                torre=getPieza(7,mover.pieza.fila);
                torre.columna=5;// Mueve la torre a la casilla adecuada
            }else{
                // Enroque largo (hacia la izquierda)
                torre=getPieza(0,mover.pieza.fila);
                torre.columna=3; // Mueve la torre a la casilla adecuada
            }
            // Actualiza la posicion visual de la torre
            torre.xPos=torre.columna*tileSize;
        }
    }

    /**
     * Maneja el movimiento especial del peon (captura al paso, promocion)
     *
     * @param mover Movimiento del peon
     */
    private void moverPeon(Movimientos mover){
        int colorIndex=mover.pieza.EsBlanco ? 1:-1;

        // Verifica si es una captura al paso
        if(getTileNum(mover.newColumna,mover.newFila)==enPassantTile){
            // Captura el peon que puede ser capturado al paso
            mover.captura=getPieza(mover.newColumna,mover.newFila+colorIndex);
        }

        // Si el peon avanza dos casillas, habilita la captura al paso
        if(Math.abs(mover.pieza.fila - mover.newFila)==2){
            // En otro caso, desactiva la casilla de captura al paso
            enPassantTile=getTileNum(mover.newColumna,mover.newFila+colorIndex);
        }else{
            enPassantTile=-1;
        }


        // Verifica si el peon llega a la Ultima fila para promocionar
        colorIndex=mover.pieza.EsBlanco? 0:7;
        if(mover.newFila==colorIndex){
            promoverPeon(mover);
        }

    }


    /**
     * Maneja la promocion de un peon al llegar a la Ultima fila
     * Muestra un dialogo para que el jugador elija la pieza de promocion
     *
     * @param mover Movimiento del peon que promociona
     */
    private void promoverPeon(Movimientos mover){
        // Opciones de promocion
        String[] opciones = {"Reina", "Torre", "Alfil", "Caballo"};

        // Muestra un dialogo para que el jugador elija la pieza
        int eleccion = JOptionPane.showOptionDialog(
                this,
                "Selecciona pieza para promocion:",
                "Promocion de Peon",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        // Crear la pieza seleccionada segUn la eleccion del jugador
        Pieza nuevaPieza = null;

        switch(eleccion) {
            case 0: // Reina
                nuevaPieza = new Reina(this, mover.newColumna, mover.newFila, mover.pieza.EsBlanco);
                break;
            case 1: // Torre
                nuevaPieza = new Torre(this, mover.newColumna, mover.newFila, mover.pieza.EsBlanco);
                break;
            case 2: // Alfil
                nuevaPieza = new Bishop(this, mover.newColumna, mover.newFila, mover.pieza.EsBlanco);
                break;
            case 3: // Caballo
                nuevaPieza = new Caballo(this, mover.newColumna, mover.newFila, mover.pieza.EsBlanco);
                break;
            default: // Por defecto Reina si el jugador cierra el dialogo sin elegir
                nuevaPieza = new Reina(this, mover.newColumna, mover.newFila, mover.pieza.EsBlanco);
        }

        // Eliminar el peon y añadir la nueva pieza
        piezasList.remove(mover.pieza);
        piezasList.add(nuevaPieza);

        // Actualizar la captura si es necesario
        captura(mover.captura);
    }

    /**
     * Verifica si un movimiento es valido segUn las reglas del ajedrez
     * Realiza mUltiples validaciones para garantizar que se cumplan las reglas
     *
     * @param mover Movimiento a verificar
     * @return true si el movimiento es valido, false en caso contrario
     */
    public boolean esMovimientoValido(Movimientos mover){

        // Si el juego ha terminado, no se permiten mas movimientos
        if (GameOver) {
            return false;
        }

        // Verifica que sea el turno del color de la pieza que se quiere mover
        if(mover.pieza.EsBlanco != TurnoBlanco){
            return false;
        }

        // Prevenir la captura del Rey - no permitido en ajedrez
        if(mover.captura != null && mover.captura.name.equals("Rey")){
            return false;
        }

        // Verifica que el movimiento sea valido segUn las reglas de la pieza
        if(!mover.pieza.esMovimientoValido(mover.newColumna, mover.newFila)){
            return false;
        }

        // Verifica que no haya piezas en el camino (segUn el tipo de pieza)
        if(mover.pieza.movimientoChocaPieza(mover.newColumna, mover.newFila)){
            return false;
        }

        // Verifica que no se intente capturar una pieza del mismo color
        if(sameColor(mover.pieza, mover.captura)){
            return false;
        }

        // Verificar que el movimiento no deja al propio rey en jaque
        if(js.EsReyJaque(mover)){
            return false;
        }

        // Si pasa todas las validaciones, el movimiento es valido
        return true;

    }

    /**
     * Captura una pieza (la elimina del tablero)
     *
     * @param p Pieza a capturar
     */
    public void captura(Pieza p){
        // Elimina la pieza de la lista si no es null
        piezasList.remove(p);
    }

    /**
     * Verifica si dos piezas son del mismo color
     *
     * @param p1 Primera pieza
     * @param p2 Segunda pieza
     * @return true si son del mismo color, false en caso contrario
     */
    public boolean sameColor(Pieza p1, Pieza p2){
        // Si alguna pieza es null, no tienen el mismo color
        if(p1==null || p2==null){
            return false;
        }

        // Compara los colores de ambas piezas
        return p1.EsBlanco==p2.EsBlanco;
    }

    /**
     * Obtiene el nUmero de casilla basado en columna y fila
     * Convierte coordenadas 2D a un indice Unico (0-63)
     *
     * @param col Columna (0-7)
     * @param f Fila (0-7)
     * @return NUmero de casilla (0-63)
     */
    public int getTileNum(int col, int f) {
        return f*fila +col;
    }

    /**
     * Encuentra el rey de un color especifico
     *
     * @param EsBlanco true para buscar el rey blanco, false para el negro
     * @return Pieza del rey o null si no se encuentra
     */
    public Pieza encontrarRey(boolean EsBlanco){
        // Busca en la lista de piezas un rey del color especificado
        for(Pieza p: piezasList){
            if(EsBlanco==p.EsBlanco && p.name.equals("Rey")){
                return p;
            }
        }

        // Si no encuentra el rey, devuelve null
        return null;
    }


    /**
     * Carga una posicion desde una cadena FEN
     * Establece las piezas, el turno, derechos de enroque y casilla de captura al paso
     *
     * @param fenString Cadena FEN que describe la posicion
     */
    public void loadPosition(String fenString) {

        // Si la cadena es nula o vacia, usa la posicion inicial estandar
        if (fenString == null || fenString.isEmpty()) {
            fenString = fenStartingPosition;
        }

        // Limpia el tablero de piezas existentes
        piezasList.clear();

        // Divide la cadena FEN en sus componentes
        String[] parts = fenString.split(" ");
        if (parts.length < 1) return;

        // Procesa la parte de posicion de piezas
        String pos = parts[0];
        int r=0;
        int c=0;

        // Recorre la cadena de posicion y coloca las piezas
        for (int i = 0; i < pos.length(); i++) {
            char ch = pos.charAt(i);
            if (ch == '/') {
                // Cambio de fila
                r++;
                c = 0;
            } else if (Character.isDigit(ch)) {
                // NUmero indica casillas vacias
                c += Character.getNumericValue(ch);
            } else {
                // Letra indica una piez
                boolean EsBlanco = Character.isUpperCase(ch);
                char piezaChar = Character.toLowerCase(ch);

                // Crea la pieza correspondiente segUn el caracter
                switch (piezaChar) {
                    case 'r':
                        piezasList.add(new Torre(this, c, r, EsBlanco));
                        break;
                    case 'n':
                        piezasList.add(new Caballo(this, c, r, EsBlanco));
                        break;
                    case 'b':
                        piezasList.add(new Bishop(this, c, r, EsBlanco));
                        break;
                    case 'q':
                        piezasList.add(new Reina(this, c, r, EsBlanco));
                        break;
                    case 'k':
                        piezasList.add(new Rey(this, c, r, EsBlanco));
                        break;
                    case 'p':
                        piezasList.add(new Peon(this, c, r, EsBlanco));
                        break;
                }
                c++;
            }
        }

        // Establece el turno segUn la notacion FEN
        if (parts.length > 1) {
            TurnoBlanco = parts[1].equals("w");
        }

        // Establece los derechos de enroque para las torres
        Pieza bqr = getPieza(0, 0);
        if (bqr instanceof Torre) {
            bqr.esPrimerMovimiento = parts[2].contains("q");
        }

        Pieza bkr = getPieza(7, 0);
        if (bkr instanceof Torre) {
            bkr.esPrimerMovimiento = parts[2].contains("k");
        }

        Pieza wqr = getPieza(0, 7);
        if (wqr instanceof Torre) {
            wqr.esPrimerMovimiento = parts[2].contains("Q");

            Pieza wkr = getPieza(7, 7);
            if (wkr instanceof Torre) {
                bqr.esPrimerMovimiento = parts[2].contains("K");
            }

            // Establece la casilla de captura al paso si existe
            if (parts[3].equals("-")) {
                enPassantTile=-1;// No hay casilla de captura al paso
            }else{
                // Convierte la notacion algebraica a indice de casilla
                enPassantTile=(7-(parts[3].charAt(1)- '1'))*8+ (parts[3].charAt(0)-'a');
            }

        }
    }

    /**
     * Actualiza el estado del juego (jaque, jaque mate,tablas)
     * Verifica si el juego ha terminado y muestra mensajes apropiados
     */
    private void actualizarJuego() {
        // Actualiza el estado visual si esta disponible
        if (mv != null) {
            mv.updateEstatus();
        }

        // Busca el rey del color que tiene el turno
        Pieza rey = encontrarRey(TurnoBlanco);
        if (rey != null) {
            // Comprobar si el rey esta en jaque
            boolean enJaque = js.EsReyJaque(new Movimientos(this, rey, rey.columna, rey.fila));

            // Verificar si el juego ha terminado
            if (js.esGameOver(rey)) {
                GameOver = true;

                // Detiene el reloj si esta disponible
                if (mv != null) {
                    mv.pausarReloj();
                }

                // Determina el resultado (jaque mate o tablas por ahogado)
                String resultado = enJaque ?
                        (TurnoBlanco ? "¡Jaque Mate! Ganan las negras" : "¡Jaque Mate! Ganan las blancas") :
                        "Tablas por ahogado";

                // Mostrar mensaje de resultado
                JOptionPane.showMessageDialog(this,
                        "<html><h2>" + resultado + "</h2></html>",
                        "Fin del Juego",
                        JOptionPane.INFORMATION_MESSAGE);

                // Guardar automaticamente la partida finalizada
                String nombreArchivo = "partida_finalizada_" + System.currentTimeMillis();
                Guardar.saveGame(this, nombreArchivo);

                // Muestra opciones al finalizar el juego
                mostrarOpcionesFinJuego();
            } else if (enJaque) {
                // Si el rey esta en jaque pero no es mate, solo actualiza el estatus
                if (mv != null) {
                    mv.updateEstatus();
                }
            }
        } else if (insuficienteMaterial(true) && insuficienteMaterial(false)) {
            // Tablas por material insuficiente (no hay piezas suficientes para dar jaque mate)
            GameOver = true;

            // Detiene el reloj si esta disponible
            if (mv != null) {
                mv.pausarReloj();
            }

            // Muestra mensaje de tablas
            JOptionPane.showMessageDialog(this,
                    "Tablas por material insuficiente",
                    "Fin del Juego",
                    JOptionPane.INFORMATION_MESSAGE);

            // Guarda la partida finalizada
            String nombreArchivo = "partida_finalizada_" + System.currentTimeMillis();
            Guardar.saveGame(this, nombreArchivo);

            // Muestra opciones al finalizar
            mostrarOpcionesFinJuego();
        }
    }

    /**
     * Muestra las opciones al finalizar el juego
     * - Nueva partida
     * - Ver movimientos
     * - Salir
     */
    void mostrarOpcionesFinJuego() {
        Object[] opciones = {"Nueva Partida", "Ver Movimientos", "Salir"};

        // Muestra un dialogo con las opciones
        int eleccion = JOptionPane.showOptionDialog(
                this,
                "¿Que deseas hacer ahora?",
                "Partida Finalizada",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        // Procesa la eleccion del usuario
        switch(eleccion) {
            case 0: // Nueva partida
                reiniciarPartida();
                break;
            case 1: // Ver movimientos
                mostrarHistorialCompleto();
                // Despues de ver movimientos, preguntar de nuevo
                mostrarOpcionesFinJuego();
                break;
            case 2: // Salir
                System.exit(0);
                break;
        }
    }

    /**
     * Reinicia la partida cerrando la ventana actual
     * y abriendo el menU principal
     */
    private void reiniciarPartida() {
        // Obtiene la ventana actual y la cierra
        JFrame frame = (JFrame)SwingUtilities.getWindowAncestor(this);
        frame.dispose();

        // Abre una nueva instancia del menU principal
        new Main().iniciarMenuPrincipal();
    }


    /**
     * Muestra el historial completo de movimientos en un dialogo
     * Da formato a los movimientos en pares numerados (blancas y negras)
     */
    private void mostrarHistorialCompleto() {
        // Verifica si hay movimientos registrados
        if (h == null || h.getMovimientos().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay movimientos registrados",
                    "Historial",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Construye la cadena con el historial formateado
        StringBuilder historialCompleto = new StringBuilder();
        historialCompleto.append("Historial completo de movimientos:\n\n");

        // Obtiene la lista de movimientos
        ArrayList<String> movimientos = h.getMovimientos();

        // Formatea los movimientos en pares (blancas/negras)
        for (int i = 0; i < movimientos.size(); i++) {
            if (i % 2 == 0) {
                // Para jugadas blancas, añade el nUmero de movimiento
                int moveNum = (i / 2) + 1;
                historialCompleto.append(String.format("%2d. %s", moveNum, movimientos.get(i)));
            } else {
                // Para jugadas negras, completa la linea
                historialCompleto.append(String.format("   %s\n", movimientos.get(i)));
            }
        }

        JTextArea textArea = new JTextArea(historialCompleto.toString(), 20, 30);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane, "Historial Completo", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Verifica si hay material insuficiente para dar jaque mate
     *
     * @param EsBlanco true para verificar las blancas, false para las negras
     * @return true si hay material insuficiente, false en caso contrario
     */
    private boolean insuficienteMaterial(boolean EsBlanco){
        // Obtiene una lista con los nombres de las piezas restantes del color especificado
        ArrayList<String> nombres=piezasList.stream().filter(p->p.EsBlanco==EsBlanco).map(p->p.name).collect(Collectors.toCollection(ArrayList::new));

        // Si existe una reina, torre o peon, hay suficiente material para dar mate
        if(nombres.contains("Reina")||nombres.contains("Torre")||nombres.contains("Peon")){
            return false;
        }

        // Si solo quedan el rey y una pieza mas (o solo el rey), el material es insuficiente
        return nombres.size()<3;
    }

    /**
     * Dibuja el componente del tablero con todas sus piezas y elementos visuales
     * @param g Objeto Graphics para dibujar en el componente
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Limpia el area antes de redibujar
        Graphics2D g2d = (Graphics2D) g;

        // Dibuja el patron del tablero alternando colores claros y oscuros
        for(int r = 0; r < fila; r++) {
            for(int c = 0; c < columna; c++) {
                g2d.setColor((c + r) % 2 == 0 ? claro : oscuro);
                g2d.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
            }
        }

        // Define un color para resaltar los movimientos validos
        Color guia=new Color(68,180,57,190);

        // Si hay una pieza seleccionada, resalta las casillas donde puede moverse
        if(piezaSeleccionada!=null){
            for(int r=0;r<fila;r++){
                for(int c=0;c<columna;c++){

                    // Verifica si mover la pieza seleccionada a esta posicion es valido
                    if(esMovimientoValido(new Movimientos(this,piezaSeleccionada,c,r))){
                        g2d.setColor(guia);//cambiar por un color mas girly
                        g2d.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
                    }
                }
            }
        }

        // Dibuja todas las piezas en el tablero
        for(Pieza pieza : piezasList) {
            pieza.paint(g2d);
        }

        // Busca el rey del jugador actual y resalta si esta en jaque
        Pieza rey=encontrarRey(TurnoBlanco);
        if(rey!=null && js.EsReyJaque(new Movimientos(this,rey,rey.columna,rey.fila))){
            // Resalta la casilla del rey con un fondo rojo semi-transparente
            g.setColor(new Color(255, 0, 0, 180));
            g.fillRect(rey.columna * tileSize, rey.fila * tileSize, tileSize, tileSize);

            // Añade un borde rojo doble para enfatizar el jaque
            g.setColor(Color.RED);
            g.drawRect(rey.columna * tileSize, rey.fila * tileSize, tileSize, tileSize);
            g.drawRect(rey.columna * tileSize + 1, rey.fila * tileSize + 1, tileSize - 2, tileSize - 2);
        }
    }
}
