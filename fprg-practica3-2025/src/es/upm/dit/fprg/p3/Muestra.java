package es.upm.dit.fprg.p3;

/**
 * Encapsulación de datos para una imagen médica simplificada, representada como
 * una matriz rectangular de intensidades en escala de grises (rango 0-15).
 * <p>
 * Esta clase implementa un modelo de datos con validación exhaustiva de integridad
 * estructural. Su responsabilidad principal es garantizar que los datos de imagen
 * sean coherentes y válidos antes de ser procesados por componentes posteriores,
 * especialmente por {@link Reconocedor}.
 * </p>
 * <p>
 * La escala de intensidades (0-15) es una simplificación deliberada que facilita
 * la representación hexadecimal y permite trabajar con datos sintéticos sin
 * dependencias externas. En sistemas reales, esta representación se expandiría
 * típicamente a 8 bits (0-255) o superior, según la modalidad de imagen.
 * </p>
 */
public class Muestra {

    /** Umbral inferior de intensidad: valor mínimo admitido (negro absoluto). */
    private static final int NEGRO = 0;

    /** Umbral superior de intensidad: valor máximo admitido (blanco absoluto). */
    private static final int BLANCO = 15;


    /** Identificador único que permite referenciar la muestra en sistemas de gestión. */
    private String id;

    /** Matriz rectangular que almacena los datos de píxeles de la imagen. */
    private final int[][] pixeles;

    /** La clase no debe contener ningún otro atributo. */


    /**
     * Construye una instancia de Muestra realizando validación exhaustiva de los
     * datos proporcionados.
     * <p>
     * El proceso de validación comprende cuatro etapas secuenciales que verifican
     * la integridad estructural y semántica:
     * </p>
     * <ol>
     *   <li><strong>Identificador:</strong> Se verifica que no sea nulo ni cadena
     *       vacía, garantizando que la muestra pueda ser identificada univocamente</li>
     *   <li><strong>Dimensionalidad:</strong> Se verifica que la matriz no sea nula
     *       y posea dimensiones mínimas de 1×1 píxeles</li>
     *   <li><strong>Estructura:</strong> Se verifica que todas las filas tengan el
     *       mismo número de columnas, garantizando una matriz rectangular</li>
     *   <li><strong>Valores:</strong> Se verifica que cada elemento se encuentre
     *       en el rango cerrado [0, 15]</li>
     * </ol>
     * <p>
     * El constructor implementa el patrón "fail-fast": ante cualquier violación de
     * estas precondiciones se lanza una excepción inmediata, previniendo que datos
     * inconsistentes se propaguen a través del sistema.
     * </p>
     *
     * @param id identificador único de la muestra (ej: "XRay_Chest_20250101",
     *           "MRI_Brain_001")
     * @param pixeles matriz rectangular cuyos elementos deben estar en rango [0, 15]
     * @throws Exception si el identificador es nulo o vacío
     * @throws Exception si la matriz es nula o posee dimensiones 0×0
     * @throws Exception si la matriz carece de estructura rectangular
     * @throws IllegalArgumentException si algún píxel excede el rango permitido
     */
    public Muestra(String id, int[][] pixeles) throws Exception {
        if (id == null || id.equals("")) {
            throw new Exception("El identificador de la muestra no puede ser nulo ni vacío.");
        }
    
        if (pixeles == null || pixeles.length == 0 || pixeles[0].length == 0) {
            throw new Exception("La matriz de píxeles no puede ser nula "
                    + "y debe tener dimensiones mínimas de 1x1.");
        }

        int columnas = pixeles[0].length;
        for (int[] fila : pixeles) {
            if (fila == null || fila.length != columnas) {
                throw new Exception("La matriz debe ser rectangular: todas las filas "
                        + "deben tener el mismo número de columnas.");
            }
        }

        for (int[] fila : pixeles) {
            for (int valor : fila) {
                if (valor < NEGRO || valor > BLANCO) {
                    throw new IllegalArgumentException("Los valores de píxeles están fuera del rango permitido. "
                            + "Rango válido: [" + NEGRO + ", " + BLANCO + "]. "
                            + "Valor encontrado: " + valor + ".");
                }
            }
        }

        this.id = id;
        this.pixeles = pixeles;
    }

    /**
     * Devuelve el identificador único asociado a esta muestra.
     *
     * @return identificador de la muestra (cadena no nula ni vacía)
     */
    public String getId() {
        return this.id;
    }

    /**
     * Devuelve la dimensión vertical (altura) de la imagen en píxeles.
     * <p>
     * Este valor, junto con {@link #getAncho()}, define las dimensiones totales de
     * la matriz de píxeles y es requerido para operaciones de validación y
     * recorrido matricial, especialmente en el contexto del reconocimiento de
     * patrones.
     * </p>
     *
     * @return número de filas (altura en píxeles, garantizado ≥ 1)
     */
    public int getAlto() {
        return this.pixeles.length;
    }

    /**
     * Devuelve la dimensión horizontal (ancho) de la imagen en píxeles.
     * <p>
     * Este valor, junto con {@link #getAlto()}, define las dimensiones totales de
     * la matriz. En el contexto de reconocimiento de patrones, determina cuántas
     * posiciones son válidas para la colocación y evaluación de patrones.
     * </p>
     *
     * @return número de columnas (ancho en píxeles, garantizado ≥ 1)
     */
    public int getAncho() {
        return this.pixeles[0].length;
    }

    /**
     * Devuelve el valor de intensidad del píxel en la posición especificada.
     * <p>
     * El método detecta y reporta explícitamente cualquier intento de:
     * </p>
     * <ul>
     *   <li>Acceder a fila con índice negativo</li>
     *   <li>Acceder a fila fuera del rango válido</li>
     *   <li>Acceder a columna con índice negativo</li>
     *   <li>Acceder a columna fuera del rango válido</li>
     * </ul>
     *
     * @param fila índice de fila (0-based, rango válido: [0, {@link #getAlto()}-1])
     * @param columna índice de columna (0-based, rango válido: [0, {@link #getAncho()}-1])
     * @return intensidad del píxel en rango [0, 15]
     * @throws Exception si fila o columna están fuera del rango válido, indicando
     *                   los límites permitidos y el acceso solicitado
     */
    public int getPixel(int fila, int columna) throws Exception {
        if (fila < 0 || fila >= getAlto() || columna < 0 || columna >= getAncho()) {
            throw new Exception("Acceso a índices fuera de los límites válidos. "
                    + "Rango permitido: fila [0, " + (getAlto() - 1) + "], "
                    + "columna [0, " + (getAncho() - 1) + "]. "
                    + "Acceso solicitado: fila " + fila + ", columna " + columna + ".");
        }
        return pixeles[fila][columna];
    }

    /**
     * Método auxiliar proporcionado que extrae los píxeles de una imagen y los
     * convierte del rango 0-255 al rango 0-15.
     * <p>
     * Este método utiliza el raster de la imagen para obtener todos los píxeles
     * en escala de grises y aplica división entera (v15 = v255 / 16) para
     * convertirlos al rango simplificado.
     * </p>
     *
     * @param img imagen de la cual extraer los píxeles
     * @return matriz de píxeles en el rango 0-15 con orden fila→columna
     */
    private int[][] extraerPixeles(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        
        // Extraer todos los píxeles de una vez
        int[] raw = img.getRaster().getPixels(0, 0, w, h, new int[w * h]);
        
        // Construir la matriz de píxeles en rango 0-15
        int[][] pixeles = new int[h][w];
        for (int fila = 0; fila < h; fila++) {
            for (int col = 0; col < w; col++) {
                int index = fila * w + col;
                int valor255 = raw[index];
                // Convertir de 0-255 a 0-15 mediante división entera
                pixeles[fila][col] = valor255 / 16;
            }
        }
        
        return pixeles;
    }

    /**
     * Devuelve una representación textual de la muestra en formato hexadecimal,
     * conveniente para verificación visual y pruebas automatizadas.
     * <p>
     * <strong>Formato:</strong> La primera línea contiene el identificador entre
     * caracteres de delimitación (ej: {@code <XRay_001>}). Las líneas siguientes
     * contienen las filas de la imagen, con cada píxel codificado en hexadecimal
     * (0-F): 0 representa negro, F representa blanco, y los valores intermedios
     * representan tonos de gris.
     * </p>
     * <p>
     * <strong>Ejemplo:</strong>
     * </p>
     * <pre>{@code
     * Muestra m = new Muestra("Sample", new int[][]{
     *     {0, 10},
     *     {15, 5}
     * });
     * System.out.println(m.toString());
     * 
     * // Salida:
     * // <Sample>
     * // 0A
     * // F5
     * }</pre>
     *
     * @return representación textual de la matriz en formato hexadecimal con el
     *         identificador en la primera línea
     */
    @Override
    public String toString() {
        String resultado = "<" + getId() + ">\n";
        for (int[] fila : pixeles) {
            for (int valor : fila) {
                resultado += Integer.toHexString(valor).toUpperCase();
            }
            resultado += "\n";
        }
        return resultado;
    }


}
