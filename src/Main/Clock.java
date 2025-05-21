package Main;

import javax.swing.*;
import java.io.Serializable;

/**
 * Esta clase implementa la funcionalidad de reloj para la partida de ajedrez
 * Se encarga de controlar los tiempos de ambos jugadores, actualizar la interfaz
 * y manejar el fin de partida por tiempo agotado
 *
 * Implementa Serializable para permitir guardar y cargar partidas con su estado temporal
 */
public class Clock implements Serializable {
    private static final long serialVersionUID = 1L;
    private int blancoTime; // Tiempo restante en segundos para el jugador de piezas blancas
    private int negroTime; // Tiempo restante en segundos para el jugador de piezas negras
    private transient Timer timer; // Timer de Swing que ejecuta la actualizacion del reloj cada segundo
    private boolean turnoBlanco; // Indica si actualmente es el turno del jugador blanco
    private transient JLabel blancoClock; // Etiqueta que muestra el tiempo restante del jugador blanco
    private transient JLabel negroClock; // Etiqueta que muestra el tiempo restante del jugador negro
    private transient Tablero tablero; // Referencia al tablero para notificar fin de juego
    private boolean isPaused; // Indica si el reloj esta pausado actualmente

    /**
     * Constructor de la clase Clock, esta va a encargarce de llevar el tiempo por partida
     * Inicializa los tiempos para ambos jugadores y configura el temporizador que actualizara
     * el reloj cada segundo
     *
     * @param minutos Tiempo inicial en minutos para ambos jugadores
     * @param blancoLabel JLabel para mostrar el tiempo del jugador de las piezas blancas
     * @param negroLabel JLabel para mostrar el tiempo del jugador de las piezas negras
     * @param tablero  Hace referencia al tablero de juego para notificar cuando termine la partida por tiempo
     */
    public Clock(int minutos,JLabel blancoLabel,JLabel negroLabel,Tablero tablero) {
        this.tablero = tablero;
        this.blancoTime = minutos*60; // Convierte minutos a segundos
        this.negroTime=minutos*60; // Convierte minutos a segundos
        this.blancoClock=blancoLabel;
        this.negroClock=negroLabel;
        this.turnoBlanco=true; // Por defecto comienza el blanco
        this.isPaused=false;

        // Configura el temporizador para que actualice el reloj cada segundo (1000ms)
        timer=new Timer(1000,e-> updateClock());
        updateLabels(); // Actualiza las etiquetas inicialmente
    }

    /**
     * Actualiza el reloj decrementando el tiempo del jugador correspondiente
     * Esta funcion se ejecuta cada segundo mientras el temporizador este activo
     * Si el tiempo de algun jugador llega a cero, detiene el temporizador y finaliza el juego
     */
    public void updateClock(){
        //Si el reloj esta pausado, no se actualiza
        if(isPaused){
            return;
        }

        //Decrementa el tiempo del jugador actual en 1 segundo
        if(turnoBlanco){
            blancoTime--;
        }else{
            negroTime--;
        }
        updateLabels(); // Actualiza las etiquetas con el nuevo tiempo

        // Verifica si algun jugador se ha quedado sin tiempo
        if(blancoTime <= 0 || negroTime <= 0){
            timer.stop();//Detiene el temporizador
            finJuego(); //Finaliza el juego por tiempo agotado
        }
    }

    /**
     * Verifica si es el turno del jugador blanco
     * Este metodo permite a otras clases consultar de quien es el turno segun el reloj
     *
     * @return true si es turno del blanco, false si es del negro
     */
    public boolean isTurnoBlanco() {
        return turnoBlanco;
    }


    /**
     *Maneja el fin del juego si es por tiempo agotado
     * Muestra un mensaje indicando quien gano, actualiza el estado visual,
     * guarda la partida finalizada y muestra opciones para continuar
     */
    private void finJuego(){
        boolean blancoPierde=blancoTime<=0; //Determina si el blanco perdio por tiempo

        tablero.GameOver=true;//Marca el juego como finalizado

        //Muestra un mensaje con el resultado
        String mensaje= blancoPierde ? "Negro Gana (por tiempo)" : "Blanco Gana (por tiempo)";
        JOptionPane.showMessageDialog(null,mensaje,"Fin del Juego",JOptionPane.INFORMATION_MESSAGE);

        //Actualiza la interfaz visual si esta disponible
        if(tablero.getMejorasVisuales()!=null){
            tablero.getMejorasVisuales().updateEstatus();
        }

        //Genera un nombre unico para guardar la partida finalizada
        String nombreArchivo = "partida_finalizada_" + System.currentTimeMillis();
        Guardar.saveGame(tablero, nombreArchivo);//Guarda automaticamente la partida

        // Mostrar opciones despues de guardar
        tablero.mostrarOpcionesFinJuego();
    }

    /**
     * Inicia el reloj para comenzar a contar el tiempo
     * Activa el temporizador que actualizara el tiempo cada segundo
     */
    public void start(){
        timer.start();
    }

    /**
     * Pausa el reloj estableciendo la bandera de pausa
     * El tiempo dejara de decrementarse pero el temporizador sigue activo
     */
    public void pause() {
        isPaused = true;
    }

    /**
     * Reanuda el reloj despues de una pausa
     * Permite que el tiempo vuelva a decrementarse para el jugador en turno
     */
    public void resume() {
        isPaused = false;
    }

    /**
     * Cambia el turno entre jugadores
     * Invierte la bandera de turno, afectando a que reloj se decrementa
     */
    public void cambioTurno(){
        turnoBlanco=!turnoBlanco;
    }

    /**
     * Actualiza las etiquetas de tiempo en la interfaz grafica
     * Convierte los tiempos en segundos al formato MM:SS y actualiza los JLabels
     */
    public void updateLabels(){
        blancoClock.setText(formatTime(blancoTime));
        negroClock.setText(formatTime(negroTime));
    }

    /**
     * Da el formato del reloj en MM:SS para mostrar en la interfaz
     * Convierte los segundos totales en minutos y segundos con formato
     *
     * @param segundos Tiempo en segundos
     * @return String con el tiempo en el formato MM:SS (con ceros a la izquierda)
     */
    private String formatTime(int segundos){
        int min=segundos/60; // Calcula los minutos enteros
        int seg=segundos%60; // Calcula los segundos restantes
        return String.format("%02d:%02d",min,seg); // Formatea con ceros a la izquierda
    }

    /**
     * Establece el tiempo del jugador blanco y actualiza la interfaz
     * util para restaurar una partida guardada o configurar tiempos personalizados
     *
     * @param tiempo Tiempo en segundos para el jugador blanco
     */
    public void setBlancoTime(int tiempo) {
        this.blancoTime = tiempo;
        updateLabels(); // Actualiza las etiquetas con el nuevo tiempo
    }

    /**
     * Establece el tiempo del jugador negro y actualiza la interfaz
     * util para restaurar una partida guardada o configurar tiempos personalizados
     *
     * @param tiempo Tiempo en segundos
     */
    public void setNegroTime(int tiempo) {
        this.negroTime = tiempo;
        updateLabels(); // Actualiza las etiquetas con el nuevo tiempo
    }

    /**
     * Obtiene el tiempo del jugador blanco
     * @return Tiempo en segundos restante para el jugador blanco
     */
    public int getBlancoTime() {
        return blancoTime;
    }

    /**
     * Obtiene el tiempo del jugador negro
     *
     * @return Tiempo en segundos para el jugador negro
     */
    public int getNegroTime() {
        return negroTime;
    }

    /**
     * Establece manualmente de quien es el turno en el reloj
     * util cuando se carga una partida guardada para sincronizar el estado del reloj
     *
     * @param turnoBlanco true para turno del blanco, false para turno del negro
     */
    public void setTurnoBlanco(boolean turnoBlanco) {
        this.turnoBlanco = turnoBlanco;
    }

}
