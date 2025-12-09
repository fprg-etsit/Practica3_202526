package es.upm.dit.fprg.p3.auxiliar;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

import es.upm.dit.fprg.p3.Muestra;

/**
 * Panel encargado de representar la muestra en pantalla, ampliando cada píxel
 * para que sea legible. También superpone las detecciones del reconocedor.
 */
public class PanelMuestra extends JPanel {

    private static final Color TRAZO_CELDA = new Color(40, 40, 40, 60);
    private static final Color COLOR_DETECCION = new Color(255, 0, 0, 80);
    private static final Color TRAZO_DETECCION = new Color(200, 0, 0);
    private static final Color COLOR_ETIQUETAS = new Color(70, 70, 70);
    private static final int MARGEN_SUPERIOR = 28;
    private static final int MARGEN_INFERIOR = 12;
    private static final int MARGEN_IZQUIERDO = 36;
    private static final int MARGEN_DERECHO = 12;

    private Muestra muestra;
    private boolean[][] mascara;
    private int pixelSize = 12;

    public void actualizar(Muestra muestra, boolean[][] mascara) {
        this.muestra = muestra;
        this.mascara = mascara;
        recalcularEscala();
        revalidate();
        repaint();
    }

    public void limpiar() {
        this.muestra = null;
        this.mascara = null;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        if (muestra == null) {
            return new Dimension(400, 400);
        }
        int ancho = muestra.getAncho() * pixelSize + MARGEN_IZQUIERDO + MARGEN_DERECHO;
        int alto = muestra.getAlto() * pixelSize + MARGEN_SUPERIOR + MARGEN_INFERIOR;
        return new Dimension(ancho + 1, alto + 1);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (muestra == null) {
            return;
        }

        recalcularEscala();

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int origenX = MARGEN_IZQUIERDO;
        int origenY = MARGEN_SUPERIOR;

        dibujarEtiquetas(g2d, origenX, origenY);
        dibujarImagen(g2d, origenX, origenY);
        dibujarMascara(g2d, origenX, origenY);

        g2d.dispose();
    }

    private void dibujarImagen(Graphics2D g2d, int origenX, int origenY) {
        for (int fila = 0; fila < muestra.getAlto(); fila++) {
            for (int columna = 0; columna < muestra.getAncho(); columna++) {
                int valor;
                try {
                    valor = muestra.getPixel(fila, columna);
                } catch (Exception e) {
                    valor = 0;
                }
                int nivel = (int) Math.round(valor * (255.0 / 15.0));
                nivel = Math.max(0, Math.min(255, nivel));
                g2d.setColor(new Color(nivel, nivel, nivel));
                int x = origenX + columna * pixelSize;
                int y = origenY + fila * pixelSize;
                g2d.fillRect(x, y, pixelSize, pixelSize);
            }
        }
        dibujarReticula(g2d, origenX, origenY);
    }

    private void dibujarMascara(Graphics2D g2d, int origenX, int origenY) {
        if (mascara == null) {
            return;
        }
        for (int fila = 0; fila < mascara.length; fila++) {
            for (int columna = 0; columna < mascara[0].length; columna++) {
                if (!mascara[fila][columna]) {
                    continue;
                }
                int x = origenX + columna * pixelSize;
                int y = origenY + fila * pixelSize;

                g2d.setColor(COLOR_DETECCION);
                g2d.fillRect(x, y, pixelSize, pixelSize);
                g2d.setColor(TRAZO_DETECCION);
                g2d.drawRect(x, y, pixelSize, pixelSize);
            }
        }

        Graphics2D gBordes = (Graphics2D) g2d.create();
        gBordes.setColor(TRAZO_DETECCION);
        gBordes.setStroke(new java.awt.BasicStroke(Math.max(2f, pixelSize / 2f)));
        for (int fila = 0; fila < mascara.length; fila++) {
            for (int columna = 0; columna < mascara[0].length; columna++) {
                if (!mascara[fila][columna]) {
                    continue;
                }
                int x = origenX + columna * pixelSize;
                int y = origenY + fila * pixelSize;

                if (fila == 0 || !mascara[fila - 1][columna]) {
                    gBordes.drawLine(x, y, x + pixelSize, y);
                }
                if (fila == mascara.length - 1 || !mascara[fila + 1][columna]) {
                    gBordes.drawLine(x, y + pixelSize, x + pixelSize, y + pixelSize);
                }
                if (columna == 0 || !mascara[fila][columna - 1]) {
                    gBordes.drawLine(x, y, x, y + pixelSize);
                }
                if (columna == mascara[0].length - 1 || !mascara[fila][columna + 1]) {
                    gBordes.drawLine(x + pixelSize, y, x + pixelSize, y + pixelSize);
                }
            }
        }
        gBordes.dispose();
    }

    private void recalcularEscala() {
        if (muestra == null) {
            pixelSize = 12;
            return;
        }
        int anchoMuestra = muestra.getAncho();
        int altoMuestra = muestra.getAlto();

        int margenHorizontal = MARGEN_IZQUIERDO + MARGEN_DERECHO;
        int margenVertical = MARGEN_SUPERIOR + MARGEN_INFERIOR;

        int anchoDisponible = getWidth() - margenHorizontal;
        int altoDisponible = getHeight() - margenVertical;

        Container contenedor = getParent();
        if (contenedor != null) {
            if (anchoDisponible <= 0) {
                anchoDisponible = contenedor.getWidth() - margenHorizontal;
            }
            if (altoDisponible <= 0) {
                altoDisponible = contenedor.getHeight() - margenVertical;
            }
        }

        int nuevoPixelSize = 0;
        if (anchoDisponible > 0 && altoDisponible > 0) {
            int maximoHorizontal = Math.max(1, anchoDisponible / Math.max(1, anchoMuestra));
            int maximoVertical = Math.max(1, altoDisponible / Math.max(1, altoMuestra));
            nuevoPixelSize = Math.max(1, Math.min(maximoHorizontal, maximoVertical));
        }

        if (nuevoPixelSize == 0) {
            int mayor = Math.max(anchoMuestra, altoMuestra);
            int espacioBase = 480 - Math.max(margenHorizontal, margenVertical);
            nuevoPixelSize = Math.max(1, espacioBase / Math.max(1, mayor));
        }

        pixelSize = nuevoPixelSize;
    }

    private void dibujarReticula(Graphics2D g2d, int origenX, int origenY) {
        g2d.setColor(TRAZO_CELDA);
        int anchoPx = muestra.getAncho() * pixelSize;
        int altoPx = muestra.getAlto() * pixelSize;

        for (int fila = 0; fila <= muestra.getAlto(); fila++) {
            int y = origenY + fila * pixelSize;
            g2d.drawLine(origenX, y, origenX + anchoPx, y);
        }
        for (int columna = 0; columna <= muestra.getAncho(); columna++) {
            int x = origenX + columna * pixelSize;
            g2d.drawLine(x, origenY, x, origenY + altoPx);
        }
    }

    private void dibujarEtiquetas(Graphics2D g2d, int origenX, int origenY) {
        g2d.setColor(COLOR_ETIQUETAS);
        Graphics2D gEtiquetas = (Graphics2D) g2d.create();
        gEtiquetas.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Font fuenteOriginal = gEtiquetas.getFont();
        Font fuenteHorizontal = ajustarFuenteHorizontal(fuenteOriginal, muestra.getAncho());
        gEtiquetas.setFont(fuenteHorizontal);

        FontMetrics metricasHorizontales = gEtiquetas.getFontMetrics();
        int descensoHorizontal = metricasHorizontales.getDescent();

        for (int columna = 0; columna < muestra.getAncho(); columna++) {
            String texto = Integer.toString(columna);
            int anchoTexto = metricasHorizontales.stringWidth(texto);
            int x = origenX + columna * pixelSize + (pixelSize - anchoTexto) / 2;
            int y = origenY - descensoHorizontal - 4;
            gEtiquetas.drawString(texto, x, y);
        }

        gEtiquetas.setFont(fuenteOriginal);
        FontMetrics metricasVerticales = gEtiquetas.getFontMetrics();
        int ascent = metricasVerticales.getAscent();

        for (int fila = 0; fila < muestra.getAlto(); fila++) {
            String texto = Integer.toString(fila);
            int altoCelda = pixelSize;
            int y = origenY + fila * altoCelda + (altoCelda + ascent) / 2 - 1;
            int x = origenX - metricasVerticales.stringWidth(texto) - 6;
            gEtiquetas.drawString(texto, x, y);
        }

        gEtiquetas.dispose();
    }

    private Font ajustarFuenteHorizontal(Font fuenteBase, int columnas) {
        if (fuenteBase == null) {
            return fuenteBase;
        }
        float tamano = fuenteBase.getSize2D();

        if (columnas > 80 || pixelSize <= 5) {
            tamano -= 4f;
        } else if (columnas > 48 || pixelSize <= 7) {
            tamano -= 2f;
        } else if (columnas > 32) {
            tamano -= 1.5f;
        }

        tamano = Math.max(8f, tamano);
        if (Math.abs(tamano - fuenteBase.getSize2D()) < 0.1f) {
            return fuenteBase;
        }
        return fuenteBase.deriveFont(tamano);
    }

}
