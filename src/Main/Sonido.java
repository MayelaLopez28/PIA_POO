package Main;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;


/**
 * Clase que maneja la reproduccion de efectos de sonido durante el juego
 * Proporciona metodos para reproducir sonidos asociados a acciones del juego,
 * como movimientos de piezas y capturas
 */
public class Sonido {
    /**
     * Reproduce el sonido que corresponde a un movimiento de piezas
     * Este metodo encapsula la llamada al metodo playSound con el archivo especifico
     */
    public static void playMoveSound() {
        playSound("move.wav");
    }

    /**
     * Reproduce el sonido que corresponde a una captura de pieza
     * Este metodo encapsula la llamada al metodo playSound con el archivo especifico
     */
    public static void playCaptureSound() {
        playSound("capture.wav");
    }

    /**
     * MEtodo que implementa la logica de reproduccion de sonidos
     * Este metodo intenta cargar y reproducir un archivo de audio
     * desde los recursos del proyecto
     * Si no puede encontrar o reproducir el archivo
     * mostrare mensajes de error.
     *
     * @param filename Nombre del archivo de sonido que
     *                 se desea reproducir (debe estar en la carpeta "rec/")
     */
    private static void playSound(String filename) {
        try {
            // Intenta cargar el sonido como un recurso desde el classpath
            InputStream inputStream = Sonido.class.getClassLoader().getResourceAsStream("rec/" + filename);

            if (inputStream == null) {
                // Si no se encuentra en el classpath, intenta otra ubicacion comun
                inputStream = Sonido.class.getResourceAsStream("/rec/" + filename);
            }

            if (inputStream == null) {
                System.out.println("Archivo de sonido no encontrado: rec/" + filename);
                // Intenta mostrar mas informacion sobre la ubicacion esperada
                System.out.println("Directorio de trabajo actual: " + System.getProperty("user.dir"));
                return;// Sale del metodo si no se puede encontrar el archivo
            }

            // Convertir el InputStream a AudioInputStream para poder reproducirlo
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(inputStream);

            // Obtiene un objeto Clip para manejar la reproduccion del audio
            Clip clip = AudioSystem.getClip();

            // Abre el clip con el AudioInputStream
            clip.open(audioIn);

            // Inicia la reproduccion del sonido
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            // Error si el formato de audio no es compatible
            // Error si hay problemas de lectura del archivo
            // Error si no hay una linea de audio disponible para reproduccion
            System.err.println("Error al reproducir sonido: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
