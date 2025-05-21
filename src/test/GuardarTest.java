package test;

import Main.*;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de prueba para la funcionalidad de guardar y cargar
 * partidas de ajedrez
 * Verifica que los datos del tablero se persistan correctamente en
 * archivos y se puedan restaurar posteriormente
 */
class GuardarTest {
    //Nombre del archivo de prueba para guardar/cargar los datos del juego
    private static final String TEST_FILENAME = "test_save.chess";

    //Instancia del tablero de ajedrez para realizar las pruebas
    private Tablero tablero;


    /**
     * Configura el entorno de prueba antes de cada test
     * Crea un nuevo tablero y elimina archivos de pruebas anteriores
     * para garantizar condiciones iniciales limpias
     */
    @BeforeEach
    void setUp() {
        tablero = new Tablero();
        // Asegurarse de que el archivo no existe antes de empezar
        try {
            Files.deleteIfExists(Paths.get(TEST_FILENAME));
            Files.deleteIfExists(Paths.get(TEST_FILENAME.replace(".chess", "_movimientos.txt")));
        } catch (IOException ignored) {}
    }


    /**
     * Limpia los recursos despues de cada test
     * Elimina los archivos creados durante las pruebas para evitar
     * efectos secundarios entre tests y mantener limpio el sistema
     */
    @AfterEach
    void tearDown() {
        // Limpiar archivos despues de cada test
        try {
            Files.deleteIfExists(Paths.get(TEST_FILENAME));
            Files.deleteIfExists(Paths.get(TEST_FILENAME.replace(".chess", "_movimientos.txt")));
        } catch (IOException ignored) {}
    }


    /**
     * Prueba la funcionalidad de guardar y cargar una partida de ajedrez
     * Verifica que:
     * 1.Se guarde correctamente un tablero en un archivo
     * 2.El archivo se cree exitosamente
     * 3.Se pueda cargar la partida en un nuevo tablero
     * 4.Los datos basicos del tablero se mantengan despues de cargar
     */
    @Test
    void testSaveAndLoadGame() {
        // Guardar partida
        Guardar.saveGame(tablero, TEST_FILENAME);

        // Verificar que se crearon los archivos
        assertTrue(Files.exists(Paths.get(TEST_FILENAME)));

        // Crear nuevo tablero y cargar partida
        Tablero loadedTablero = new Tablero();
        Guardar.loadGame(loadedTablero, TEST_FILENAME);

        // Verificar que se cargaron los datos basicos
        assertEquals(tablero.TurnoBlanco, loadedTablero.TurnoBlanco);
        assertEquals(tablero.enPassantTile, loadedTablero.enPassantTile);
        assertEquals(tablero.GameOver, loadedTablero.GameOver);
    }

    /**
     * Prueba el caso de guardar una partida sin especificar la extension del archivo
     * Verifica que el sistema añada automaticamente la extension "chess" al nombre del archivo
     */
    @Test
    void testSaveGameWithoutExtension() {
        String filenameWithoutExtension = "test_save";
        Guardar.saveGame(tablero, filenameWithoutExtension);
        assertTrue(Files.exists(Paths.get(filenameWithoutExtension + ".chess")));
    }

}