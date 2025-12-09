package es.upm.dit.fprg.p3;

/**
 * Encapsulación del patrón visual que caracteriza a un patógeno, representado como
 * una matriz rectangular de intensidades en escala de grises (rango 0-15).
 * <p>
 * El patrón actúa como plantilla de búsqueda que se compara contra instancias de
 * {@link Muestra} para identificar coincidencias. A diferencia de una muestra
 * específica, un patrón permite definir tanto restricciones exactas como flexibilidad
 * mediante comodines.
 * </p>
 * <p>
 * <strong>Comodines (valores indefinidos):</strong> El valor especial
 * {@link #INDEFINIDO} (-1) actúa como comodín en el patrón y representa una
 * posición que acepta cualquier intensidad en la muestra. Por ejemplo, si un patrón
 * define un píxel como indefinido, ese píxel coincidirá con cualquier valor (0-15)
 * encontrado en la posición correspondiente de la muestra. Esto proporciona
 * flexibilidad para detectar patrones que toleran variación en ciertas posiciones.
 * </p>
 * <p>
 * Esta clase valida exhaustivamente la integridad estructural del patrón durante
 * su construcción, garantizando que ningún componente del sistema tenga que trabajar
 * con patrones incoherentes o inválidos.
 * </p>
 */
public class Patogeno {

    /** Valor especial que representa un píxel indefinido (comodín) en el patrón. */
    public static final int INDEFINIDO = -1;

    /** Valor constante para el nivel de intensidad mínimo (negro). */
    private static final int NEGRO = 0;

    /** Valor constante para el nivel de intensidad máximo (blanco). */
    private static final int BLANCO = 15;

    /** Nombre descriptivo que identifica el patógeno (ej: "Cruz", "Virus"). */
    private final String nombre;

    /** Matriz rectangular de píxeles que define el patrón visual del patógeno. */
    private final int[][] patron;

    /** La clase no debe contener ningún otro atributo. */


    /**
     * Construye una instancia de Patogeno realizando validación exhaustiva de los
     * datos proporcionados.
     * <p>
     * El proceso de validación comprende cuatro etapas secuenciales que garantizan
     * la integridad del patrón:
     * </p>
     * <ol>
     *   <li><strong>Nombre:</strong> Se verifica que no sea nulo ni cadena vacía,
     *       permitiendo identificar el patógeno de forma unívoca</li>
     *   <li><strong>Dimensionalidad:</strong> Se verifica que la matriz no sea nula
     *       y tenga dimensiones mínimas de 1×1 píxeles</li>
     *   <li><strong>Estructura:</strong> Se verifica que todas las filas posean el
     *       mismo número de columnas, garantizando una matriz rectangular</li>
     *   <li><strong>Valores:</strong> Se verifica que cada píxel sea
     *       {@link #INDEFINIDO} o esté en el rango [0, 15]</li>
     * </ol>
     * <p>
     * Ante cualquier violación de estas validaciones, se lanza una excepción
     * inmediata con un mensaje descriptivo indicando exactamente qué condición
     * ha sido vulnerada. Esto implementa el patrón "fail-fast" previniendo que
     * patrones inválidos se propaguen al resto del sistema.
     * </p>
     *
     * @param nombre etiqueta descriptiva que identificará el patógeno
     *               (ej: "Cruz", "Coronavirus", "Parásito_Malaria")
     * @param patron matriz rectangular cuyos elementos deben ser valores en rango
     *               [0, 15] o {@link #INDEFINIDO}
     * @throws Exception si el nombre es nulo o vacío
     * @throws Exception si la matriz es nula o posee dimensiones 0×0
     * @throws Exception si la matriz no tiene estructura rectangular
     * @throws Exception si algún píxel está fuera del rango permitido
     *                   (válidos: 0-15 o {@link #INDEFINIDO})
     */
    public Patogeno(String nombre, int[][] patron) throws Exception {
        if (nombre == null || nombre.equals("")) {
            throw new Exception("El nombre del patógeno no puede ser nulo ni vacío.");
        }
        
        if (patron == null || patron.length == 0 || patron[0].length == 0) {
            throw new Exception("El patrón no puede ser nulo "
                    + "y debe tener dimensiones mínimas de 1x1 píxeles.");
        }

        int columnas = patron[0].length;
        for (int[] fila : patron) {
            if (fila == null || fila.length != columnas) {
                throw new Exception("La matriz debe ser rectangular: todas las filas "
                        + "deben tener el mismo número de columnas.");
            }
        }

        for (int[] fila : patron) {
            for (int valor : fila) {
                if (valor != INDEFINIDO && (valor < NEGRO || valor > BLANCO)) {
                    throw new Exception("Los valores de píxeles están fuera del rango permitido. "
                            + "Rango válido: [" + NEGRO + ", " + BLANCO + "] o "
                            + INDEFINIDO + " (indefinido). "
                            + "Valor encontrado: " + valor + ".");
                }
            }
        }
        this.nombre = nombre;
        this.patron = patron;
    }

    /**
     * Devuelve el nombre descriptivo del patógeno.
     * <p>
     * El nombre es una etiqueta utilizada para identificar el patógeno en la interfaz
     * de usuario, mensajes de diagnóstico y trazas de ejecución.
     * </p>
     *
     * @return etiqueta descriptiva del patrón (cadena no nula ni vacía)
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve la dimensión vertical (altura) del patrón en píxeles.
     * <p>
     * Esta dimensión, junto con {@link #getAncho()}, define el tamaño total del
     * patrón. Es esencial para determinar las posiciones válidas donde el patrón
     * puede ser colocado dentro de una muestra durante el reconocimiento.
     * </p>
     *
     * @return número de filas del patrón (alto en píxeles, garantizado ≥ 1)
     */
    public int getAlto() {
        return patron.length;
    }

    /**
     * Devuelve la dimensión horizontal (ancho) del patrón en píxeles.
     * <p>
     * Esta dimensión, junto con {@link #getAlto()}, define el tamaño total del
     * patrón. En el contexto del reconocimiento de patrones, limita el número de
     * posiciones válidas donde el patrón puede evaluarse sobre la muestra.
     * </p>
     *
     * @return número de columnas del patrón (ancho en píxeles, garantizado ≥ 1)
     */
    public int getAncho() {
        return patron[0].length;
    }

    /**
     * Devuelve el valor de intensidad en una posición concreta del patrón.
     * <p>
     * <strong>Diferencia fundamental respecto a {@link Muestra#getPixel(int, int)}:</strong>
     * Este método implementa un comportamiento tolerante: si los índices solicitados
     * caen fuera de los límites del patrón, devuelve {@link #INDEFINIDO} en lugar de
     * lanzar una excepción. Esto no indica un error; refleja una decisión semántica
     * importante.
     * </p>
     * <p>
     * La razón de este comportamiento es que todo lo que se encuentra fuera de las
     * dimensiones del patrón se considera indefinido: "puede tener cualquier valor".
     * Esta característica simplifica significativamente la lógica del
     * {@link Reconocedor}, que puede evaluar píxeles sin necesidad de validar
     * explícitamente si están dentro del patrón.
     * </p>
     *
     * @param fila índice de fila (0-based, rango válido: [0, {@link #getAlto()}-1])
     * @param columna índice de columna (0-based, rango válido: [0, {@link #getAncho()}-1])
     * @return intensidad del píxel en esa posición (0-15), o {@link #INDEFINIDO}
     *         si está fuera de los límites o contiene un comodín
     */
    public int getPixel(int fila, int columna) {
        if (fila < 0 || fila >= getAlto() || columna < 0 || columna >= getAncho()) {
            return INDEFINIDO;
        }
        return patron[fila][columna];
    }

    /**
     * Indica si la posición especificada del patrón contiene un comodín
     * (valor indefinido).
     * <p>
     * Un comodín es un píxel con valor {@link #INDEFINIDO} (-1) que actúa durante
     * el reconocimiento como aceptor universal: coincide con cualquier valor
     * encontrado en la posición correspondiente de la muestra. Este método ofrece
     * una forma conveniente de consultar si una posición del patrón impone una
     * restricción específica o es flexible.
     * </p>
     * <p>
     * Nota importante: Las posiciones fuera del rango del patrón también devuelven
     * {@code true} porque se consideran indefinidas. Esto es coherente con el
     * comportamiento de {@link #getPixel(int, int)}.
     * </p>
     *
     * @param fila índice de fila (0-based)
     * @param columna índice de columna (0-based)
     * @return {@code true} si la posición es un comodín (valor
     *         {@link #INDEFINIDO}) o está fuera del rango del patrón;
     *         {@code false} si contiene un valor específico (0-15) que debe
     *         coincidir exactamente con la muestra
     */
    public boolean esIndefinido(int fila, int columna) {
        return getPixel(fila, columna) == INDEFINIDO;
    }

    /**
     * Devuelve una representación textual del patrón, útil para depuración visual
     * y documentación.
     * <p>
     * <strong>Formato de salida:</strong> La primera línea contiene el nombre del
     * patrón rodeado de signos de exclamación (ej: {@code !Mini!}). Las líneas
     * siguientes contienen las filas del patrón, donde cada píxel se representa
     * como: hexadecimal (0-F) para píxeles con valores definidos, o asterisco (*)
     * para píxeles indefinidos (comodines).
     * </p>
     * <p>
     * <strong>Ejemplo:</strong>
     * </p>
     * <pre>{@code
     * Patogeno mini = new Patogeno("Mini", new int[][]{
     *     {Patogeno.INDEFINIDO, 15},
     *     {10, 0}
     * });
     * System.out.println(mini.toString());
     * 
     * // Salida:
     * // !Mini!
     * // *F
     * // A0
     * }</pre>
     *
     * @return representación textual del patrón con valores hexadecimales y
     *         asteriscos delimitando el nombre
     */
    @Override
    public String toString() {
        String resultado = "!" + getNombre() + "!\n";
        for (int[] fila : patron) {
            for (int valor : fila) {
                if (valor == INDEFINIDO) {
                    resultado += "*";
                } else {
                    resultado += Integer.toHexString(valor).toUpperCase();
                }
            }
            resultado += "\n";
        }
        return resultado;
    }


}
