package es.upm.dit.fprg.p3;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Orquestador que coordina múltiples reconocedores de imagen para generar
 * informes diagnósticos cuantitativos.
 * <p>
 * Esta clase permite registrar diferentes tipos de reconocedores (patrones,
 * estructuras lineales, etc.) y aplicarlos de forma sistemática sobre todas
 * las muestras de un estudio diagnóstico, agregando los resultados en un
 * informe consolidado.
 * </p>
 * <p>
 * El informe generado cuantifica el número total de píxeles positivos detectados
 * para cada elemento microscópico, permitiendo una evaluación objetiva de la
 * presencia de diferentes patógenos o estructuras en el estudio completo.
 * </p>
 */
public class AnalizadorDiagnostico {

    /**
     * Conjunto de reconocedores registrados que se aplicarán sobre las muestras.
     * Se utiliza un Set para evitar duplicados.
     */
    private final Set<ReconocedorImagen> reconocedores;

    /**
     * Constructor que inicializa el analizador sin reconocedores.
     */
    public AnalizadorDiagnostico() {
        this.reconocedores = new HashSet<>();
    }


    /**
     * Registra un nuevo reconocedor que será aplicado en los análisis posteriores.
     * <p>
     * Los reconocedores pueden ser de cualquier tipo que implemente la interfaz
     * {@link ReconocedorImagen}, permitiendo combinar diferentes estrategias de
     * detección (patrones específicos, estructuras lineales, etc.).
     * </p>
     *
     * @param reconocedor reconocedor a registrar
     * @throws Exception si el reconocedor es nulo
     */
    public void addReconocedor(ReconocedorImagen reconocedor) throws Exception {
    	if(reconocedor == null) throw new Exception("Reconocedor nulo");
        reconocedores.add(reconocedor);
    }
    
    /**
     * Cuenta el número total de píxeles marcados como positivos en una máscara
     * de detección.
     * <p>
     * Este método auxiliar recorre toda la matriz booleana y cuenta cuántos
     * elementos tienen el valor {@code true}.
     * </p>
     *
     * @param mascara matriz booleana de detecciones
     * @return número total de elementos {@code true} en la matriz
     */
    private int contarPositivos(boolean[][] mascara) {
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
}
