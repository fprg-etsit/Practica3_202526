package es.upm.dit.fprg.p3;

/**
 * Componente responsable de implementar el reconocimiento automático de patrones
 * sobre instancias de {@link Muestra}.
 * <p>
 * Su funcionalidad principal es localizar todas las posiciones en una imagen donde
 * aparece un {@link Patogeno} específico y devolver una máscara booleana que indica
 * dónde se han encontrado coincidencias.
 * </p>
 * <p>
 * <strong>Enfoque algorítmico:</strong> La implementación utiliza el algoritmo de
 * ventana deslizante (sliding window). El patrón se posiciona secuencialmente en
 * cada ubicación válida de la muestra, se evalúa si existe coincidencia completa
 * respetando los comodines ({@link Patogeno#INDEFINIDO}), y se registran los
 * resultados en una máscara de salida.
 * </p>
 */
public class ReconocedorPatron {

    private final Patogeno patogeno;

    /**
     * Construye un reconocedor configurado para buscar un patrón específico.
     * <p>
     * El reconocedor mantiene una referencia al patógeno que utilizará en todas
     * las búsquedas posteriores. La validación del patrón en esta etapa garantiza
     * que el reconocedor esté en estado válido desde su creación.
     * </p>
     *
     * @param patogeno patrón que se buscará en las muestras analizadas
     * @throws Exception si el patrón proporcionado es nulo
     */
    public ReconocedorPatron(Patogeno patogeno) throws Exception {
        if (patogeno == null) {
            throw new Exception("El patrón no puede ser nulo.");
        }
        this.patogeno = patogeno;
    }

    /**
     * Analiza una muestra e identifica las posiciones donde aparece el patrón
     * almacenado en el reconocedor.
     * <p>
     * <strong>Estrategia de búsqueda (ventana deslizante):</strong> El algoritmo
     * sigue estos pasos:
     * </p>
     * <ol>
     *   <li>Posiciona el patrón en cada ubicación válida de la muestra, comenzando
     *       por la esquina superior-izquierda</li>
     *   <li>Verifica si existe coincidencia completa en esa ubicación llamando a
     *       {@link #coincideEn(Muestra, int, int)}</li>
     *   <li>Si se encuentra coincidencia, registra todos los píxeles definidos en
     *       la máscara de resultados llamando a
     *       {@link #marcarCoincidencia(boolean[][], int, int)}</li>
     *   <li>Continúa iterando hasta haber evaluado todas las posiciones posibles</li>
     * </ol>
     * <p>
     * La máscara resultante tiene idénticas dimensiones que la muestra de entrada.
     * Cada posición marcada con {@code true} indica que un píxel definido del
     * patrón fue encontrado en esa ubicación. Los píxeles indefinidos (comodines)
     * del patrón no generan marcas en la máscara, solo los píxeles con restricción
     * específica.
     * </p>
     *
     * @param muestra imagen sobre la que realizar la búsqueda del patrón
     * @return máscara booleana con las mismas dimensiones que la muestra,
     *         {@code true} en posiciones donde se encontraron píxeles definidos
     *         del patrón
     * @throws Exception si la muestra es nula
     * @throws Exception si el patrón es más grande que la muestra en cualquiera
     *                   de las dimensiones
     */
    public boolean[][] detectar(Muestra muestra) throws Exception {
        if (muestra == null) {
            throw new Exception("La muestra no puede ser nula.");
        }
        if (patogeno.getAlto() > muestra.getAlto() || patogeno.getAncho() > muestra.getAncho()) {
            throw new Exception("El patrón no puede ser más grande que la muestra.");
        }

        boolean[][] mascara = new boolean[muestra.getAlto()][muestra.getAncho()];

        int maxFila = muestra.getAlto() - patogeno.getAlto();
        int maxColumna = muestra.getAncho() - patogeno.getAncho();

        for (int fila = 0; fila <= maxFila; fila++) {
            for (int columna = 0; columna <= maxColumna; columna++) {
                if (coincideEn(muestra, fila, columna)) {
                    marcarCoincidencia(mascara, fila, columna);
                }
            }
        }

        return mascara;
    }

    /**
     * Evalúa si el patrón coincide completamente en una posición específica de
     * la muestra.
     * <p>
     * El método realiza una comparación píxel a píxel entre el patrón y la región
     * correspondiente de la muestra. La evaluación respeta la semántica de los
     * comodines:
     * </p>
     * <ul>
     *   <li>Los píxeles <strong>indefinidos</strong> (comodines) del patrón siempre
     *       se consideran coincidentes; no imponen restricción alguna</li>
     *   <li>Los píxeles <strong>definidos</strong> del patrón deben coincidir
     *       exactamente con los valores encontrados en la muestra</li>
     *   <li>Si algún píxel definido no coincide, el método devuelve {@code false}
     *       inmediatamente, implementando la optimización de cortocircuito
     *       (short-circuit evaluation)</li>
     * </ul>
     * <p>
     * <strong>Manejo de excepciones:</strong> Aunque en este punto del algoritmo
     * los índices ya han sido validados en {@link #detectar(Muestra)}, este método
     * captura excepciones como defensa en profundidad. Si ocurre una excepción,
     * generalmente indica un error de programación en lugar de datos inválidos.
     * </p>
     *
     * @param muestra imagen donde se busca el patrón
     * @param filaInicio fila de la muestra donde se posiciona la esquina
     *                   superior-izquierda del patrón (índice 0-based)
     * @param columnaInicio columna de la muestra donde se posiciona la esquina
     *                      superior-izquierda del patrón (índice 0-based)
     * @return {@code true} si todos los píxeles definidos del patrón coinciden
     *         exactamente con los valores correspondientes en la muestra;
     *         {@code false} si existe alguna discrepancia
     */
    private boolean coincideEn(Muestra muestra, int filaInicio, int columnaInicio) {
        for (int filaPatron = 0; filaPatron < patogeno.getAlto(); filaPatron++) {
            for (int colPatron = 0; colPatron < patogeno.getAncho(); colPatron++) {
                try {
                    if (!patogeno.esIndefinido(filaPatron, colPatron)) {
                        int valorMuestra = muestra.getPixel(filaInicio + filaPatron, columnaInicio + colPatron);
                        int valorPatron = patogeno.getPixel(filaPatron, colPatron);
                        if (valorMuestra != valorPatron) {
                            return false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Registra en la máscara de resultados los píxeles donde se localizó una
     * coincidencia del patrón.
     * <p>
     * Cuando se identifica una coincidencia del patrón en una posición determinada,
     * este método actualiza la máscara para reflejar dicho hallazgo. Únicamente se
     * marcan como {@code true} los píxeles que corresponden a posiciones definidas
     * del patrón; los comodines (valores {@link Patogeno#INDEFINIDO}) no se registran
     * en la máscara.
     * </p>
     * <p>
     * <strong>Comportamiento con coincidencias solapadas:</strong> Si múltiples
     * coincidencias del patrón se solapan espacialmente, la máscara acumulará todas
     * ellas (una misma posición puede ser marcada múltiples veces sin efecto adicional).
     * Este comportamiento es correcto y refleja que la máscara registra dónde
     * aparecen píxeles definidos del patrón, no necesariamente dónde comienzan
     * coincidencias completas.
     * </p>
     *
     * @param mascara matriz booleana donde se registran los resultados (modificada
     *                in situ)
     * @param filaInicio fila donde se posiciona la esquina superior-izquierda
     *                   del patrón (índice 0-based)
     * @param columnaInicio columna donde se posiciona la esquina superior-izquierda
     *                      del patrón (índice 0-based)
     */
    private void marcarCoincidencia(boolean[][] mascara, int filaInicio, int columnaInicio) {
        for (int fila = 0; fila < patogeno.getAlto(); fila++) {
            for (int columna = 0; columna < patogeno.getAncho(); columna++) {
                try {
                    if (!patogeno.esIndefinido(fila, columna)) {
                        mascara[filaInicio + fila][columnaInicio + columna] = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
