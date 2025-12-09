package es.upm.dit.fprg.p3;

/**
 * Reconocedor especializado en detectar estructuras lineales (fibrillas) en muestras
 * microscópicas.
 * <p>
 * A diferencia de {@link ReconocedorPatron} que busca formas específicas predefinidas,
 * este reconocedor identifica cualquier segmento lineal (horizontal, vertical o diagonal)
 * que cumpla criterios de longitud y rango de color, sin requerir una forma exacta.
 * </p>
 * <p>
 * El algoritmo explora la imagen en tres direcciones principales (horizontal, vertical
 * y diagonal) buscando segmentos continuos de píxeles cuyos valores estén dentro del
 * rango de color especificado y que alcancen la longitud mínima requerida.
 * </p>
 */
public class ReconocedorLineal {

    private final Fibrilla fibrilla;

    /**
     * Construye un reconocedor configurado para detectar fibrillas con características
     * específicas.
     *
     * @param fibrilla especificación de la fibrilla a detectar (longitud mínima y
     *                 rango de color)
     * @throws Exception si la fibrilla es nula
     */
    public ReconocedorLineal(Fibrilla fibrilla) throws Exception {
        if (fibrilla == null) {
            throw new Exception("La fibrilla no puede ser nula.");
        }
        this.fibrilla = fibrilla;
    }


    /**
     * Implementación del algoritmo de detección de segmentos lineales.
     * <p>
     * <strong>Estrategia de búsqueda:</strong> El algoritmo explora la imagen en
     * tres direcciones:
     * </p>
     * <ul>
     *   <li><strong>Horizontal:</strong> segmentos de izquierda a derecha</li>
     *   <li><strong>Vertical:</strong> segmentos de arriba a abajo</li>
     *   <li><strong>Diagonal:</strong> segmentos en dirección sureste (↘)</li>
     * </ul>
     * <p>
     * Para cada dirección, el algoritmo:
     * </p>
     * <ol>
     *   <li>Recorre cada posición válida de inicio</li>
     *   <li>Cuenta píxeles consecutivos que cumplen el criterio de color</li>
     *   <li>Si la longitud alcanza o supera {@code longitudMin}, marca todos los
     *       píxeles del segmento en la máscara</li>
     * </ol>
     * <p>
     * <strong>Criterio de color:</strong> Un píxel pertenece a un segmento si su
     * valor está en el rango [{@code colorMin}, {@code colorMax}].
     * </p>
     *
     * @param m muestra a analizar
     * @return máscara con los segmentos detectados
     */
    private boolean[][] segmentosDetectados(Muestra m) {
        boolean[][] mascara = new boolean[m.getAlto()][m.getAncho()];
        
        int longitudMin = fibrilla.getLongitudMin();
        int colorMin = fibrilla.getColorMin();
        int colorMax = fibrilla.getColorMax();

        // Búsqueda horizontal
        for (int fila = 0; fila < m.getAlto(); fila++) {
            for (int col = 0; col < m.getAncho(); col++) {
                int longitud = 0;
                int colInicio = col;
                
                // Contar píxeles consecutivos en rango
                while (col < m.getAncho()) {
                    try {
                        int valor = m.getPixel(fila, col);
                        if (valor >= colorMin && valor <= colorMax) {
                            longitud++;
                            col++;
                        } else {
                            break;
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
                
                // Si cumple longitud mínima, marcar segmento
                if (longitud >= longitudMin) {
                    for (int c = colInicio; c < colInicio + longitud; c++) {
                        mascara[fila][c] = true;
                    }
                }
                
                // Ajustar col para no revisar píxeles ya procesados
                if (longitud > 0) {
                    col--;
                }
            }
        }

        // Búsqueda vertical
        for (int col = 0; col < m.getAncho(); col++) {
            for (int fila = 0; fila < m.getAlto(); fila++) {
                int longitud = 0;
                int filaInicio = fila;
                
                // Contar píxeles consecutivos en rango
                while (fila < m.getAlto()) {
                    try {
                        int valor = m.getPixel(fila, col);
                        if (valor >= colorMin && valor <= colorMax) {
                            longitud++;
                            fila++;
                        } else {
                            break;
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
                
                // Si cumple longitud mínima, marcar segmento
                if (longitud >= longitudMin) {
                    for (int f = filaInicio; f < filaInicio + longitud; f++) {
                        mascara[f][col] = true;
                    }
                }
                
                // Ajustar fila para no revisar píxeles ya procesados
                if (longitud > 0) {
                    fila--;
                }
            }
        }

        // Búsqueda diagonal (dirección sureste ↘)
        // Diagonales que comienzan en la primera fila
        for (int colInicio = 0; colInicio < m.getAncho(); colInicio++) {
            detectarDiagonal(m, 0, colInicio, mascara, longitudMin, colorMin, colorMax);
        }
        
        // Diagonales que comienzan en la primera columna (excepto esquina ya procesada)
        for (int filaInicio = 1; filaInicio < m.getAlto(); filaInicio++) {
            detectarDiagonal(m, filaInicio, 0, mascara, longitudMin, colorMin, colorMax);
        }

        return mascara;
    }

    /**
     * Detecta segmentos lineales en una diagonal específica.
     *
     * @param m muestra a analizar
     * @param filaInicio fila de inicio de la diagonal
     * @param colInicio columna de inicio de la diagonal
     * @param mascara máscara donde marcar los píxeles detectados
     * @param longitudMin longitud mínima del segmento
     * @param colorMin color mínimo del rango
     * @param colorMax color máximo del rango
     */
    private void detectarDiagonal(Muestra m, int filaInicio, int colInicio, 
                                   boolean[][] mascara, int longitudMin, 
                                   int colorMin, int colorMax) {
        int fila = filaInicio;
        int col = colInicio;
        
        while (fila < m.getAlto() && col < m.getAncho()) {
            int longitud = 0;
            int filaSegmento = fila;
            int colSegmento = col;
            
            // Contar píxeles consecutivos en rango en la diagonal
            while (fila < m.getAlto() && col < m.getAncho()) {
                try {
                    int valor = m.getPixel(fila, col);
                    if (valor >= colorMin && valor <= colorMax) {
                        longitud++;
                        fila++;
                        col++;
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    break;
                }
            }
            
            // Si cumple longitud mínima, marcar segmento
            if (longitud >= longitudMin) {
                for (int i = 0; i < longitud; i++) {
                    mascara[filaSegmento + i][colSegmento + i] = true;
                }
            }
            
            // Continuar si no se procesaron píxeles o avanzar
            if (longitud == 0) {
                fila++;
                col++;
            }
        }
    }
}
