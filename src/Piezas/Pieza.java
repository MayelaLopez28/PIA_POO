package Piezas;

import Main.Tablero;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Clase base abstracta que representa una pieza de ajedrez generica
 * Define las propiedades y comportamientos comunes a todas las piezas del juego
 * Implementa Serializable para permitir guardar el estado del juego
 */
public class Pieza implements Serializable {

    public int columna,fila;//Columna actual de la pieza en el tablero
    public int xPos,yPos;//Posicion en pixeles para el dibujado en pantalla
    public boolean EsBlanco;//Indica si la pieza pertenece al jugador de piezas blancas
    public String name;//Nombre de la pieza (Rey, Reina, Torre, etc)


    /**
     * Indica si la pieza no se ha movido aun
     * Importante para movimientos especiales como el enroque
     */
    public boolean esPrimerMovimiento = true;

    /**
     * Imagen de la pieza para mostrar en el tablero
     * Se marca como transient para excluirla de la serializacion
     */
    private transient BufferedImage imagen;
    private String imagePath;//Ruta de la imagen para reconstruirla despues de deserializacion
    Tablero tablero;//Referencia al tablero donde se encuentra la pieza

    /**
     * Constructor base para todas las piezas de ajedrez
     *
     * @param tablero El tablero donde se situara la pieza
     */
    public Pieza(Tablero tablero) {
        this.tablero = tablero;
    }

    /**
     * Obtiene la imagen actual de la pieza
     *
     * @return La imagen de la pieza como BufferedImage
     */
    public BufferedImage getImagen() {
        return imagen;
    }

    /**
     * Carga la imagen de la pieza desde los recursos
     * Si no encuentra la imagen, crea una imagen de reserva
     *
     * @param imageName Nombre del archivo de imagen en el directorio /rec/
     */
    protected void loadImage(String imageName) {
        // Si el nombre de la imagen es nulo o vacio, crear imagen de respaldo
        if (imageName == null || imageName.trim().isEmpty()) {
            this.imagen = createFallbackImage(getSafeTileSize());
            return;
        }

        // Construir ruta de recurso correctamente
        this.imagePath = "/rec/" + imageName;

        try {
            // Obtener el stream de la imagen desde los recursos
            InputStream imageStream = getClass().getResourceAsStream(imagePath);

            if (imageStream == null) {
                throw new IOException("El recurso no se encontró: " + imagePath);
            }

            // Leer la imagen
            BufferedImage originalImage = ImageIO.read(imageStream);
            if (originalImage == null) {
                throw new IOException("El recurso no es una imagen válida: " + imagePath);
            }

            imageStream.close();

            // Escalar la imagen con tamaño seguro
            int targetSize = getSafeTileSize();
            if (targetSize <= 0) {
                targetSize = 85; // Tamaño por defecto si no hay tablero
            }

            this.imagen = scaleImage(originalImage, targetSize, targetSize);
        } catch (IOException e) {
            System.err.println("Error al cargar imagen '" + imageName + "': " + e.getMessage());
            // Si hay error, crear imagen de respaldo
            this.imagen = createFallbackImage(getSafeTileSize());
        }
    }

    /**
     * Obtiene un tamaño seguro para la casilla basado en el tablero
     * Si el tablero no esta disponible, usa un valor predeterminado
     *
     * @return Tamaño seguro para la casilla en pixeles
     */
    private int getSafeTileSize() {
        return (tablero != null && tablero.tileSize > 0) ? tablero.tileSize : 85;
    }


    /**
     * Crea una imagen de respaldo simple cuando la imagen original no esta disponible
     * Dibuja un rectangulo del color de la pieza con la primera letra del nombre
     *
     * @param size Tamaño de la imagen en pixeles
     * @return La imagen de respaldo creada
     */
    private BufferedImage createFallbackImage(int size) {

        // Limitar el tamaño
        size = Math.max(10, Math.min(size, 512));

        // Crear imagen nueva
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        // Dibujar fondo con el color correspondiente
        g2d.setColor(EsBlanco ? Color.WHITE : Color.BLACK);
        g2d.fillRect(0, 0, size, size);

        // Dibujar texto (primera letra del nombre) con color contrario
        g2d.setColor(EsBlanco ? Color.BLACK : Color.WHITE);
        Font font = new Font("Arial", Font.BOLD, size/2);
        g2d.setFont(font);

        // Usar la primera letra del nombre como simbolo, o "?" si no hay nombre
        String symbol = (name != null && !name.isEmpty()) ? name.substring(0, 1) : "?";

        // Centrar el texto
        FontMetrics fm = g2d.getFontMetrics();
        int x = (size - fm.stringWidth(symbol)) / 2;
        int y = ((size - fm.getHeight()) / 2) + fm.getAscent();

        g2d.drawString(symbol, x, y);
        g2d.dispose();

        return img;
    }

    /**
     * Escala una imagen a las dimensiones especificadas
     *
     * @param original Imagen original a escalar
     * @param width Ancho deseado en pixeles
     * @param height Alto deseado en pixeles
     * @return La imagen escalada
     * @throws IllegalArgumentException Si las dimensiones son invalidas
     */
    private BufferedImage scaleImage(BufferedImage original, int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Dimensiones de imagen inválidas: " + width + "x" + height);
        }

        // Crear nueva imagen con las dimensiones especificadas
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaled.createGraphics();

        // Usar interpolacion bilinear para mejor calidad
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(original, 0, 0, width, height, null);
        g2.dispose();
        return scaled;
    }

    /**
     * Carga la imagen desde la ruta almacenada
     * util despues de la deserializacion
     */
    public void loadImageFromPath() {
        if (imagePath != null && tablero != null) {
            loadImage(imagePath.substring(5));
        } else {
            // Si no hay ruta de imagen o tablero, crear imagen de respaldo
            this.imagen = createFallbackImage(tablero != null ? tablero.tileSize : 85);
        }
    }


    /**
     * Metodo para la serializacion
     * Guarda los campos normales y la ruta de la imagen
     *
     * @param out Stream de salida para la serializacion
     * @throws IOException Si ocurre un error de E/S
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        // Usar serializacion por defecto para los campos normales
        out.defaultWriteObject();

        // Guardar la ruta de la imagen para reconstruirla despues
        out.writeObject(imagePath);
    }

    /**
     * Metodo para la deserializacion
     * Reconstruye la imagen a partir de la ruta guardada
     *
     * @param in Stream de entrada para la deserializacion
     * @throws IOException Si ocurre un error de E/S
     * @throws ClassNotFoundException Si no se encuentra la clase
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // Leer los campos normales
        in.defaultReadObject();
        // Reconstruimos la imagen desde el path
        this.imagePath = (String) in.readObject();

        // Cargar la imagen desde la ruta
        loadImageFromPath();
    }

    /**
     * Establece una nueva referencia al tablero y reescala la imagen si es necesario
     *
     * @param tablero La nueva referencia al tablero
     */
    public void setTablero(Tablero tablero) {
        this.tablero = tablero;
        // Re-escalar la imagen con el nuevo tablero si es necesario
        if (imagen != null && tablero != null) {
            imagen = scaleImage(imagen, tablero.tileSize, tablero.tileSize);
        }
    }

    /**
     * Verifica si un movimiento es valido para la pieza
     * Este metodo debe ser sobreescrito por las clases hijas con la logica especifica de cada pieza
     * Por defecto, permite cualquier movimiento
     *
     * @param columna La columna destino
     * @param fila La fila destino
     * @return true si el movimiento es valido, false en caso contrario
     */
    public boolean esMovimientoValido(int columna,int fila) {
        return true;
    }

    /**
     * Verifica si el movimiento choca con alguna pieza en su trayectoria
     * Este metodo debe ser sobreescrito por las clases hijas con la logica especifica de cada pieza
     * Por defecto, no detecta choques
     *
     * @param columna La columna destino
     * @param fila La fila destino
     * @return true si hay alguna pieza en la trayectoria, false si el camino esta libre
     */
    public boolean movimientoChocaPieza(int columna,int fila) {
        return false;
    }

    /**
     * Dibuja la pieza en el tablero usando su imagen o un placeholder si no hay imagen
     *
     * @param g2d Contexto grafico para dibujar
     */
    public void paint(Graphics2D g2d) {
        if (imagen != null) {
            // Dibujar la imagen en la posicion correspondiente
            g2d.drawImage(imagen, xPos, yPos, null);
        } else {
            // Dibujar placeholder si no hay imagen
            g2d.setColor(EsBlanco ? Color.WHITE : Color.BLACK);
            g2d.fillRect(xPos, yPos, tablero.tileSize, tablero.tileSize);
            g2d.setColor(EsBlanco ? Color.BLACK : Color.WHITE);
            g2d.drawString(name.substring(0, 1), xPos + 10, yPos + 20);
        }
    }

}
