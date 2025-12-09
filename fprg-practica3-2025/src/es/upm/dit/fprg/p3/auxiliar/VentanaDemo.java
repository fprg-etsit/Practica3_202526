package es.upm.dit.fprg.p3.auxiliar;

import es.upm.dit.fprg.p3.*;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ventana de escritorio destinada a probar la lógica de los reconocedores
 * (patrones y lineales) con muestras y elementos microscópicos predefinidos.
 */
public class VentanaDemo {

    private final PanelMuestra panelMuestra = new PanelMuestra();
    private final JLabel etiquetaEstado = new JLabel("Seleccione una muestra y un elemento microscópico.");
    private final List<Muestra> muestras;
    private final List<ReconocedorImagen> reconocedores;
    private final JComboBox<String> selectorMuestra;
    private final JComboBox<String> selectorReconocedor;

    public VentanaDemo(Muestra[] muestrasAdicionales, ReconocedorImagen[] reconocedoresAdicionales) {
        this.muestras = combinarListas(
            DatosPredefinidos.getInstance().getMuestras(),
            muestrasAdicionales
        );
        this.reconocedores = combinarListas(
            DatosPredefinidos.getInstance().getReconocedores(),
            reconocedoresAdicionales
        );
        this.selectorMuestra = new JComboBox<>(extraerIds(this.muestras));
        this.selectorReconocedor = new JComboBox<>(extraerNombresElementos(this.reconocedores));
    }

    public VentanaDemo() {
        this(null, null);
    }

    public void play() {
        SwingUtilities.invokeLater(() -> {
            configurarLookAndFeel();
            mostrar();
        });
    }

    private static void configurarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | 
                 javax.swing.UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private void mostrar() {
        JFrame frame = new JFrame("Reconocedor de Elementos Microscópicos - Práctica 3");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(12, 12));
        frame.setLocationByPlatform(true);

        frame.add(crearPanelControles(), BorderLayout.NORTH);
        frame.add(panelMuestra, BorderLayout.CENTER);
        frame.add(crearBarraEstado(), BorderLayout.SOUTH);

        selectorMuestra.addActionListener(e -> actualizarImagenSinDeteccion());
        selectorReconocedor.addActionListener(e -> etiquetaEstado.setText(
            "Pulse \"Detectar\" para buscar el elemento en la muestra."));

        actualizarImagenSinDeteccion();

        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setVisible(true);
    }

    private JPanel crearPanelControles() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        panel.add(new JLabel("Muestra:"));
        panel.add(selectorMuestra);

        panel.add(new JLabel("Elemento:"));
        panel.add(selectorReconocedor);

        JButton botonDetectar = new JButton("Detectar");
        botonDetectar.addActionListener(e -> ejecutarDeteccion());
        panel.add(botonDetectar);

        JButton botonLimpiar = new JButton("Limpiar");
        botonLimpiar.addActionListener(e -> actualizarImagenSinDeteccion());
        panel.add(botonLimpiar);

        return panel;
    }

    private JPanel crearBarraEstado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(4, 12, 8, 12));
        panel.add(etiquetaEstado, BorderLayout.CENTER);
        return panel;
    }

    private void actualizarImagenSinDeteccion() {
        Muestra muestra = obtenerMuestraSeleccionada();
        if (muestra == null) {
            panelMuestra.limpiar();
            etiquetaEstado.setText("Seleccione una muestra y un elemento microscópico.");
        } else {
            panelMuestra.actualizar(muestra, null);
            etiquetaEstado.setText("Pulse \"Detectar\" para buscar el elemento en la muestra.");
        }
    }

    private void ejecutarDeteccion() {
        Muestra muestra = obtenerMuestraSeleccionada();
        ReconocedorImagen reconocedor = obtenerReconocedorSeleccionado();

        if (muestra == null || reconocedor == null) {
            JOptionPane.showMessageDialog(panelMuestra,
                "Debe seleccionar una muestra y un elemento microscópico.",
                "Datos incompletos",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean[][] mascara = reconocedor.detectar(muestra);
            int pixelesPositivos = contarPositivos(mascara);
            int hallazgos = contarRegiones(mascara);

            panelMuestra.actualizar(muestra, mascara);

            String nombreElemento = reconocedor.getElemento().getNombre();
            String tipoReconocedor = reconocedor instanceof ReconocedorPatron ? "patrón" : "lineal";
            
            String mensaje = String.format(
                "Elemento: %s (reconocedor %s) | Píxeles detectados: %d | Regiones: %d",
                nombreElemento, tipoReconocedor, pixelesPositivos, hallazgos
            );
            
            etiquetaEstado.setText(mensaje);
        } catch (Exception ex) {
            String mensaje = ex.getMessage() != null ? ex.getMessage() : 
                "Error inesperado durante la detección.";
            JOptionPane.showMessageDialog(panelMuestra,
                mensaje,
                "Error en la detección",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private Muestra obtenerMuestraSeleccionada() {
        int indice = selectorMuestra.getSelectedIndex();
        if (indice < 0 || indice >= muestras.size()) {
            return null;
        }
        return muestras.get(indice);
    }

    private ReconocedorImagen obtenerReconocedorSeleccionado() {
        int indice = selectorReconocedor.getSelectedIndex();
        if (indice < 0 || indice >= reconocedores.size()) {
            return null;
        }
        return reconocedores.get(indice);
    }

    private int contarPositivos(boolean[][] mascara) {
        if (mascara == null) {
            return 0;
        }
        int contador = 0;
        for (boolean[] fila : mascara) {
            for (boolean valor : fila) {
                if (valor) {
                    contador++;
                }
            }
        }
        return contador;
    }

    private int contarRegiones(boolean[][] mascara) {
        if (mascara == null) {
            return 0;
        }
        int filas = mascara.length;
        int columnas = mascara[0].length;
        boolean[][] visitado = new boolean[filas][columnas];
        int[] stackFilas = new int[filas * columnas];
        int[] stackColumnas = new int[filas * columnas];
        int regiones = 0;

        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                if (!mascara[fila][columna] || visitado[fila][columna]) {
                    continue;
                }
                int top = 0;
                stackFilas[top] = fila;
                stackColumnas[top] = columna;
                top++;
                visitado[fila][columna] = true;

                while (top > 0) {
                    top--;
                    int actualFila = stackFilas[top];
                    int actualColumna = stackColumnas[top];

                    if (actualFila > 0 && mascara[actualFila - 1][actualColumna] && 
                        !visitado[actualFila - 1][actualColumna]) {
                        visitado[actualFila - 1][actualColumna] = true;
                        stackFilas[top] = actualFila - 1;
                        stackColumnas[top] = actualColumna;
                        top++;
                    }
                    if (actualFila + 1 < filas && mascara[actualFila + 1][actualColumna] && 
                        !visitado[actualFila + 1][actualColumna]) {
                        visitado[actualFila + 1][actualColumna] = true;
                        stackFilas[top] = actualFila + 1;
                        stackColumnas[top] = actualColumna;
                        top++;
                    }
                    if (actualColumna > 0 && mascara[actualFila][actualColumna - 1] && 
                        !visitado[actualFila][actualColumna - 1]) {
                        visitado[actualFila][actualColumna - 1] = true;
                        stackFilas[top] = actualFila;
                        stackColumnas[top] = actualColumna - 1;
                        top++;
                    }
                    if (actualColumna + 1 < columnas && mascara[actualFila][actualColumna + 1] && 
                        !visitado[actualFila][actualColumna + 1]) {
                        visitado[actualFila][actualColumna + 1] = true;
                        stackFilas[top] = actualFila;
                        stackColumnas[top] = actualColumna + 1;
                        top++;
                    }
                }
                regiones++;
            }
        }
        return regiones;
    }

    private static <T> List<T> combinarListas(List<T> base, T[] adicionales) {
        List<T> resultado = new ArrayList<>(base);
        if (adicionales != null) {
            resultado.addAll(Arrays.stream(adicionales)
                .filter(Objects::nonNull)
                .toList());
        }
        return List.copyOf(resultado);
    }

    private static String[] extraerIds(List<Muestra> muestras) {
        return muestras.stream()
                .map(Muestra::getId)
                .toArray(String[]::new);
    }

    private static String[] extraerNombresElementos(List<ReconocedorImagen> reconocedores) {
        return reconocedores.stream()
                .map(r -> r.getElemento().getNombre())
                .toArray(String[]::new);
    }
}
