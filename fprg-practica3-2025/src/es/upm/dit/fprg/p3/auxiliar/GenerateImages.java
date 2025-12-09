package es.upm.dit.fprg.p3.auxiliar;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;

public class GenerateImages {
    private static final int WIDTH = 96;
    private static final int HEIGHT = 64;
    private static final int INDEFINIDO = -1;
    private static final int GRAY_STEP = 17; // 255/15: 0x0 to 0xF mapped to 0 to 255

    public static void main(String[] args) throws IOException {
        createNoiseCoronavirus();
        createCrosses();
    }

    private static void createNoiseCoronavirus() throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = image.getRaster();
        Random random = new Random(42);

        // Fondo de ruido en bloques blancos o negros para darle grano visible.
        int block = 2;
        for (int y = 0; y < HEIGHT; y += block) {
            for (int x = 0; x < WIDTH; x += block) {
                int value = random.nextBoolean() ? 0 : 255;
                for (int dy = 0; dy < block && y + dy < HEIGHT; dy++) {
                    for (int dx = 0; dx < block && x + dx < WIDTH; dx++) {
                        raster.setSample(x + dx, y + dy, 0, value);
                    }
                }
            }
        }

        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawRandomFibrils(g, raster, random);
        g.dispose();

        int[][] corona = crearPatronCoronaConEspinas();
        int size = corona.length;
        int cx = (int) (WIDTH * 0.7); // Desplazado para que no quede centrado
        int cy = (int) (HEIGHT * 0.35);
        int offsetX = cx - size / 2;
        int offsetY = cy - size / 2;

        for (int fila = 0; fila < size; fila++) {
            int y = offsetY + fila;
            if (y < 0 || y >= HEIGHT) {
                continue;
            }
            for (int columna = 0; columna < size; columna++) {
                int x = offsetX + columna;
                if (x < 0 || x >= WIDTH) {
                    continue;
                }
                int valor = corona[fila][columna];
                if (valor == INDEFINIDO) {
                    continue;
                }
                raster.setSample(x, y, 0, valor * GRAY_STEP);
            }
        }

        ImageIO.write(image, "png", new File("ejemplo_coronavirus_ruido1.png"));
    }

    private static void createCrosses() throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = image.createGraphics();

        // Fondo gris oscuro.
        g.setColor(new Color(30, 30, 30));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Cruces blancas.
        g.setColor(Color.WHITE);
        int armLength = 7;
        int thickness = 3;
        int[][] centers = {
                {18, 16},
                {48, 16},
                {78, 16},
                {28, 44},
                {58, 46}
        };

        for (int[] c : centers) {
            drawCross(g, c[0], c[1], armLength, thickness);
        }

        // Rejilla de fibrillas donde estaba la última cruz.
        drawGridFibrils(g, 86, 40);

        g.dispose();
        ImageIO.write(image, "png", new File("ejemplo_cruces1.png"));
    }

    private static void drawCross(Graphics2D g, int cx, int cy, int arm, int thickness) {
        int halfThickness = thickness / 2;

        // Segmentos principales vertical y horizontal.
        g.fillRect(cx - halfThickness, cy - arm, thickness, arm * 2 + 1);
        g.fillRect(cx - arm, cy - halfThickness, arm * 2 + 1, thickness);

        // Puntillas en los extremos (un píxel extra centrado en cada lado).
        g.fillRect(cx, cy - arm - 1, 1, 1);
        g.fillRect(cx, cy + arm + 1, 1, 1);
        g.fillRect(cx - arm - 1, cy, 1, 1);
        g.fillRect(cx + arm + 1, cy, 1, 1);
    }

    private static void drawGridFibrils(Graphics2D g, int cx, int cy) {
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1f));

        int spacing = 4;
        int length = 20;
        int halfLen = length / 2;

        for (int i = -2; i <= 2; i++) {
            int x = cx + i * spacing;
            int y1 = Math.max(0, cy - halfLen);
            int y2 = Math.min(HEIGHT - 1, cy + halfLen);
            g.drawLine(x, y1, x, y2);
        }

        for (int i = -2; i <= 2; i++) {
            int y = cy + i * spacing;
            int x1 = Math.max(0, cx - halfLen);
            int x2 = Math.min(WIDTH - 1, cx + halfLen);
            g.drawLine(x1, y, x2, y);
        }
    }

    private static void drawRandomFibrils(Graphics2D g, WritableRaster raster, Random random) {
        int fibrilGray = 0x40;
        g.setColor(new Color(fibrilGray, fibrilGray, fibrilGray));
        g.setStroke(new BasicStroke(1f));

        int fibrilCount = 14;
        int minLen = 16;
        int maxLen = 48;

        for (int i = 0; i < fibrilCount; i++) {
            int len = minLen + random.nextInt(maxLen - minLen + 1);
            int dir = random.nextInt(4); // 0: vertical, 1: horizontal, 2: diag down, 3: diag up
            int x0, y0, x1, y1;
            switch (dir) {
                case 0: // vertical
                    x0 = random.nextInt(WIDTH);
                    y0 = random.nextInt(HEIGHT - len + 1);
                    x1 = x0;
                    y1 = y0 + len - 1;
                    break;
                case 1: // horizontal
                    x0 = random.nextInt(WIDTH - len + 1);
                    y0 = random.nextInt(HEIGHT);
                    x1 = x0 + len - 1;
                    y1 = y0;
                    break;
                case 2: // diagonal descendente
                    x0 = random.nextInt(WIDTH - len + 1);
                    y0 = random.nextInt(HEIGHT - len + 1);
                    x1 = x0 + len - 1;
                    y1 = y0 + len - 1;
                    break;
                default: // diagonal ascendente
                    x0 = random.nextInt(WIDTH - len + 1);
                    y0 = len - 1 + random.nextInt(HEIGHT - len + 1);
                    x1 = x0 + len - 1;
                    y1 = y0 - (len - 1);
                    break;
            }
            g.drawLine(x0, y0, x1, y1);
            drawLineOnRaster(raster, x0, y0, x1, y1, fibrilGray);
        }
    }

    private static void drawLineOnRaster(WritableRaster raster, int x0, int y0, int x1, int y1, int gray) {
        int dx = Math.abs(x1 - x0);
        int dy = -Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx + dy;
        int x = x0;
        int y = y0;

        while (true) {
            if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
                raster.setSample(x, y, 0, gray);
            }
            if (x == x1 && y == y1) {
                break;
            }
            int e2 = 2 * err;
            if (e2 >= dy) {
                err += dy;
                x += sx;
            }
            if (e2 <= dx) {
                err += dx;
                y += sy;
            }
        }
    }

    private static int[][] crearPatronCoronaConEspinas() {
        int size = 32;
        int[][] matriz = new int[size][size];
        double centro = (size - 1) / 2.0;
        double radioNucleo = 7.5;
        double radioCoronaInterior = 10.5;
        double radioCoronaExterior = 13.0;
        double longitudEspina = 4.0;
        double diagonal = Math.sqrt(0.5);

        for (int fila = 0; fila < size; fila++) {
            Arrays.fill(matriz[fila], INDEFINIDO);
            for (int columna = 0; columna < size; columna++) {
                double dx = columna - centro;
                double dy = fila - centro;
                double distancia = Math.hypot(dx, dy);

                if (distancia <= radioNucleo) {
                    matriz[fila][columna] = 0xF;
                } else if (distancia <= radioCoronaInterior) {
                    matriz[fila][columna] = 0xE;
                } else if (distancia <= radioCoronaExterior) {
                    matriz[fila][columna] = distancia < radioCoronaExterior - 0.5 ? 0xD : 0xC;
                }
            }
        }

        double[][] direcciones = {
            {1.0, 0.0},
            {-1.0, 0.0},
            {0.0, 1.0},
            {0.0, -1.0},
            {diagonal, diagonal},
            {diagonal, -diagonal},
            {-diagonal, diagonal},
            {-diagonal, -diagonal}
        };

        for (double[] direccion : direcciones) {
            for (double paso = -0.8; paso <= longitudEspina; paso += 0.5) {
                double distancia = radioCoronaExterior + paso;
                int fila = (int) Math.round(centro + direccion[1] * distancia);
                int columna = (int) Math.round(centro + direccion[0] * distancia);

                if (fila < 0 || fila >= size || columna < 0 || columna >= size) {
                    continue;
                }

                int intensidad;
                if (paso < 0) {
                    intensidad = 0xE;
                } else if (paso < 1.5) {
                    intensidad = 0xD;
                } else {
                    intensidad = 0xB;
                }
                matriz[fila][columna] = Math.max(matriz[fila][columna], intensidad);
            }
        }

        return matriz;
    }
}
