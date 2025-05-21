package test;

import Main.Historial;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de prueba para la funcionalidad de historial de movimientos
 * del juego de ajedrez
 * Verifica la correcta gestion del registro
 * de movimientos y su serializacion
 */
class HistorialTest {
    //Instancia del historial para realizar las pruebas
    private Historial historial;


    /**
     * Configura el entorno de prueba antes de cada test
     * Crea una nueva instancia del historial para garantizar
     * condiciones iniciales limpias
     */
    @BeforeEach
    void setUp() {
        historial = new Historial();
    }

    /**
     * Prueba la funcionalidad de añadir movimientos al historial
     * Verifica que:
     * 1. Los movimientos se añadan correctamente
     * 2. El tamaño de la lista de movimientos aumente adecuadamente
     * 3. Los movimientos esten en el orden correcto
     */
    @Test
    void testAddMovimiento() {
        historial.addMovimiento("e4");
        historial.addMovimiento("e5");

        assertEquals(2, historial.getMovimientos().size());
        assertEquals("e4", historial.getMovimientos().get(0));
        assertEquals("e5", historial.getMovimientos().get(1));
    }


    /**
     * Prueba la funcionalidad de establecer la lista completa de movimientos
     * Verifica que la lista proporcionada se asigne correctamente al historial
     */
    @Test
    void testSetMovimientos() {
        ArrayList<String> movimientos = new ArrayList<>();
        movimientos.add("e4");
        movimientos.add("e5");

        historial.setMovimientos(movimientos);
        assertEquals(movimientos, historial.getMovimientos());
    }

    /**
     * Prueba la serializacion y deserializacion del historial
     * Verifica que:
     * 1. El historial se pueda serializar sin errores
     * 2. El historial se pueda deserializar correctamente
     * 3. Los datos despues de la deserializacion sean identicos a los originales
     *
     * @throws IOException Si ocurre un error durante la serializacion/deserializacion
     * @throws ClassNotFoundException Si ocurre un error al reconstruir la clase
     */
    @Test
    void testSerialization() throws IOException, ClassNotFoundException {
        // Agregar algunos movimientos
        historial.addMovimiento("e4");
        historial.addMovimiento("e5");

        // Serializar
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(historial);
        oos.close();

        // Deserializar
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Historial deserialized = (Historial) ois.readObject();

        // Verificar
        assertEquals(historial.getMovimientos(), deserialized.getMovimientos());
    }
}