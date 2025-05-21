package Main;

import Piezas.Pieza;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Clase encargada de guardar los datos del juego de ajedrez
 * Proporciona funcionalidades para guardar y cargar partidas completas,
 * incluyendo el estado del tablero, las piezas, tiempo restante
 * y el historial de movimientos
 */
public class Guardar {

    /**
     * Guarda una partida completa en un archivo con extension chess
     * Además, crea un archivo de texto con el historial de movimientos
     *
     * @param tablero Tablero de juego a guardar con su estado actual
     * @param filename Nombre del archivo donde guardar (se añadira chess si no lo tiene)
     */
    public static void saveGame(Tablero tablero, String filename) {
        // Asegura que el archivo tenga la extensión correcta
        if (!filename.endsWith(".chess")) {
            filename += ".chess";
        }

        // Guarda el estado principal del juego (piezas, turnos, tiempos)
        saveGameData(tablero, filename);

        // Guarda el historial de movimientos en un archivo de texto separado
        saveMovimientosToFile(tablero, filename.replace(".chess", "_movimientos.txt"));
    }

    /**
     * Guarda los datos principales del juego en formato binario serializado
     * Esto incluye: las piezas, el turno actual, información sobre enPassant,
     * si el juego esta terminado, el historial de movimientos y los tiempos restantes
     *
     * @param tablero Tablero de juego a guardar
     * @param filename Nombre del archivo donde guardar
     */
    private static void saveGameData(Tablero tablero, String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {

            // Guarda la lista de piezas (estado actual del tablero)
            out.writeObject(new ArrayList<>(tablero.piezasList));

            // Guarda informacion sobre el estado del juego
            out.writeBoolean(tablero.TurnoBlanco); // De quien es el turno
            out.writeInt(tablero.enPassantTile); // Posicion para captura al paso
            out.writeBoolean(tablero.GameOver); // Si el juego ha terminado

            // Guarda el historial de movimientos si existe
            if (tablero.getHistorial() != null) {
                out.writeObject(tablero.getHistorial().getMovimientos());
            }else {
                // Si no hay historial, guarda una lista vacia
                out.writeObject(new ArrayList<String>());
            }

            // Guarda la información del reloj si existe
            MejorasVisuales mv = tablero.getMejorasVisuales();
            if (mv != null && mv.getReloj() != null) {
                out.writeInt(mv.getReloj().getBlancoTime()); // Tiempo restante blanco
                out.writeInt(mv.getReloj().getNegroTime());// Tiempo restante negro
                out.writeBoolean(tablero.TurnoBlanco);  // Turno actual
            } else {
                // Si no hay reloj, guarda valores por defecto (10 minutos)
                out.writeInt(600); // 10 minutos
                out.writeInt(600);
                out.writeBoolean(true);
            }

        } catch (IOException e) {
            // Muestra un dialogo de error si no se puede guardar
            showErrorDialog("Error al guardar la partida", e.getMessage());
        }
    }

    /**
     * Guarda el historial de movimientos en un archivo de texto
     * Le da formato a los movimientos en pares (blancas y negras) con numeracion
     * Incluye informacion sobre el resultado final de la partida
     * @param tablero Tablero de juego con el historial
     * @param filename Nombre del archivo donde guardar el historial
     */
    private static void saveMovimientosToFile(Tablero tablero, String filename) {
        // Si no hay historial o está vacio, no crea el archivo
        if (tablero.getHistorial() == null || tablero.getHistorial().getMovimientos().isEmpty()) {
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Historial de movimientos - ");
            writer.println("----------------------------------");

            // Escribe los movimientos con formato de notacion de ajedrez
            ArrayList<String> movimientos = tablero.getHistorial().getMovimientos();
            for (int i = 0; i < movimientos.size(); i++) {
                if (i % 2 == 0) {
                    // Movimiento de blancas, inicia con numero de jugada
                    writer.print(((i / 2) + 1) + ". " + movimientos.get(i));
                } else {
                    // Movimiento de negras, completa la línea
                    writer.println(" " + movimientos.get(i));
                }
            }
            // Escribe el resultado final de la partida
            writer.println("\nPartida finalizada: " +
                    (tablero.GameOver ?
                            (tablero.TurnoBlanco ? "Negras ganan" : "Blancas ganan") :
                            "Tablas"));
        } catch (IOException e) {
            showErrorDialog("Error al guardar movimientos", e.getMessage());
        }
    }

    /**
     * Carga una partida completa desde un archivo
     * Restaura el estado del tablero, la posición de las piezas,
     * los tiempos del reloj y todos los demas datos guardados
     *
     * @param tablero Tablero donde cargar la partida, se sobrescribe su estado
     * @param filename Nombre del archivo a cargar
     */
    public static void loadGame(Tablero tablero, String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {

            // Carga la lista de piezas
            ArrayList<Pieza> piezas = (ArrayList<Pieza>) in.readObject();
            tablero.piezasList = piezas;

            // Carga el estado del juego
            tablero.TurnoBlanco = in.readBoolean();
            tablero.enPassantTile = in.readInt();
            tablero.GameOver = in.readBoolean();

            // Carga el historial de movimientos
            if (tablero.getHistorial() != null) {
                ArrayList<String> movimientos = (ArrayList<String>) in.readObject();
                tablero.getHistorial().setMovimientos(movimientos);
            }else {
                in.readObject(); // Leer pero descartar si no hay historial
            }

            // Carga los tiempos del reloj
            int blancoTime = in.readInt();
            int negroTime = in.readInt();
            boolean turnoReloj = in.readBoolean();

            // Actualiza el reloj si existe
            MejorasVisuales mv = tablero.getMejorasVisuales();
            if (mv != null && mv.getReloj() != null) {
                mv.getReloj().setBlancoTime(blancoTime);
                mv.getReloj().setNegroTime(negroTime);
                mv.getReloj().setTurnoBlanco(turnoReloj);
            }


            for (Pieza p : tablero.piezasList) {
                if (p != null) {
                    p.setTablero(tablero);
                }
            }

            // Reconstruye estructuras de datos transient necesarias para el juego
            tablero.rebuildInput(); // Reconstruye componentes de entrada
            tablero.rebuildJaqueScanner();// Reconstruye el escaner de jaque

            // Actualiza la interfaz visual
            if (tablero.getMejorasVisuales() != null) {
                tablero.getMejorasVisuales().updateEstatus();
            }

            // Vuelve a dibujar el tablero
            tablero.repaint();
        } catch (Exception e){
            // Muestra un dialogo de error si no se puede cargar
            showErrorDialog("Error al cargar partida", e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Muestra un diálogo de error con formato HTML para mejor presentacion
     * Usado internamente para notificar problemas al guardar o cargar
     *
     * @param title Título del dialogo
     * @param message  Mensaje de error
     */
    private static void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(null,
                "<html><b>" + title + "</b><br>" + message + "</html>",
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
