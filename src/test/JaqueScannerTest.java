package test;

import Main.*;
import Piezas.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de prueba para la funcionalidad de
 * deteccion de jaques en el juego de ajedrez
 * Verifica la correcta identificacion de situaciones
 * de jaque y jaque mate
 */
class JaqueScannerTest {
    //Instancia del tablero de ajedrez para realizar las pruebas
    private Tablero tablero;

    //Instancia del escaner de jaques
    private JaqueScanner jaqueScanner;


    /**
     * Configura el entorno de prueba antes de cada test
     * Crea nuevas instancias del tablero y del escaner de jaques
     * para garantizar condiciones iniciales limpias
     */
    @BeforeEach
    void setUp() {
        tablero = new Tablero();
        jaqueScanner = new JaqueScanner(tablero);
    }

    /**
     * Prueba la deteccion de jaque cuando no hay amenaza al rey
     * Verifica que el metodo EsReyJaque devuelva false cuando
     * el rey no esta bajo amenaza de captura
     */
    @Test
    void testEsReyJaqueWithNoCheck() {
        Pieza rey = tablero.encontrarRey(true); // Rey blanco
        Movimientos mover = new Movimientos(tablero, rey, rey.columna, rey.fila);
        assertFalse(jaqueScanner.EsReyJaque(mover));
    }

    /**
     * Prueba la funcionalidad de encontrar piezas atacantes al rey
     * Configura un escenario donde una torre negra ataca al rey blanco
     * y verifica que se identifique correctamente como atacante
     */
    @Test
    void testEncontrarAtacantes() {
        // Configurar un jaque basico (torre atacando al rey)
        tablero.piezasList.clear();
        Pieza reyBlanco = new Rey(tablero, 4, 0, true);
        Pieza torreNegra = new Torre(tablero, 4, 7, false);
        tablero.piezasList.add(reyBlanco);
        tablero.piezasList.add(torreNegra);

        ArrayList<Pieza> atacantes = jaqueScanner.encontrarAtacantes(reyBlanco);
        assertEquals(1, atacantes.size());
        assertEquals(torreNegra, atacantes.get(0));
    }


    /**
     * Prueba la deteccion de jaque mate
     * Configura un escenario donde el rey blanco esta en jaque mate
     * por dos torres negras y verifica que se identifique correctamente
     * como fin de juego
     */
    @Test
    void testEsGameOverWithCheckmate() {
        // Configurar un jaque mate basico
        tablero.piezasList.clear();
        Pieza reyBlanco = new Rey(tablero, 0, 0, true);
        Pieza torreNegra1 = new Torre(tablero, 1, 7, false);
        Pieza torreNegra2 = new Torre(tablero, 7, 1, false);
        tablero.piezasList.add(reyBlanco);
        tablero.piezasList.add(torreNegra1);
        tablero.piezasList.add(torreNegra2);

        assertTrue(jaqueScanner.esGameOver(reyBlanco));
    }


}