package es.upm.dit.fprg.p3;

import es.upm.dit.fprg.p3.auxiliar.VentanaDemo;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Punto de entrada para la prueba interactiva.
 * Permite ajustar las muestras y reconocedores que se entregan a la aplicación
 * sin tocar la lógica Swing de {@link VentanaDemo}.
 */
public final class PruebaInteractiva {

    private PruebaInteractiva() {
        // Evitar instanciación
    }

    public static void main(String[] args) throws Exception {

        // ========== MUESTRAS ADICIONALES ==========
        List<Muestra> listaMuestras = new ArrayList<>();
        
        // Muestras creadas manualmente con matrices de píxeles
        int[][] pixelesEjemplo3x3 = {
                { 0, 1, 2 },
                { 2, 3, 4 },
                { 4, 5, 6 }
        };
        Muestra ejemploMuestra3x3 = new Muestra("Ejemplo 3x3", pixelesEjemplo3x3);
        listaMuestras.add(ejemploMuestra3x3);

        int[][] pixelesLineas = {
                { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
                { 2, 2, 2,15,15,15,15,15, 2, 2 },
                { 2, 2, 2, 2, 2, 2, 2,15, 2, 2 },
                { 2, 2, 2, 2, 2, 2, 2,15, 2, 2 },
                { 0, 2, 2, 2, 2, 2, 2,15, 2, 2 },
                { 0, 2, 2, 2, 2, 2, 2,15, 2, 2 },
                { 0, 2, 2, 2, 2, 2, 2,15, 2, 2 },
                { 2, 2, 2, 2, 2, 2, 2,15, 2, 2 },
                { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
                { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }
        };
        Muestra muestraConLineas = new Muestra("Líneas 10x10", pixelesLineas);
        listaMuestras.add(muestraConLineas);

        // Muestras cargadas desde ficheros
        try {
            Muestra muestraFichero1 = cargarMuestraDesdeArchivo(
                "data/ejemplo_coronavirus_ruido1.png", "Coronavirus con ruido");
            listaMuestras.add(muestraFichero1);
            System.out.println("✓ Muestra cargada desde fichero: " + muestraFichero1.getId());
        } catch (Exception e) {
            System.out.println("⚠ No se pudo cargar ejemplo_coronavirus_ruido1.png: " + e.getMessage());
        }

        try {
            Muestra muestraFichero2 = cargarMuestraDesdeArchivo(
                "data/ejemplo_cruces1.png", "Cruces dispersas");
            listaMuestras.add(muestraFichero2);
            System.out.println("✓ Muestra cargada desde fichero: " + muestraFichero2.getId());
        } catch (Exception e) {
            System.out.println("⚠ No se pudo cargar ejemplo_cruces1.png: " + e.getMessage());
        }

        // Convertir la lista a array
        Muestra[] muestras = new Muestra[listaMuestras.size()];
        for (int i = 0; i < listaMuestras.size(); i++) {
            muestras[i] = listaMuestras.get(i);
        }

        // ========== RECONOCEDORES ADICIONALES ==========
        // Patógenos
        int I = Patogeno.INDEFINIDO;
        int[][] patronEjemplo1x3 = {{ 0, I, 0 }};
        Patogeno ejemploPatogeno1x3 = new Patogeno("Patrón 1x3", patronEjemplo1x3);
        ReconocedorPatron recPatron1x3 = new ReconocedorPatron(ejemploPatogeno1x3);

        // Fibrillas
        Fibrilla fibrillaCorta = new Fibrilla("Fibrilla corta oscura (>=3)", 3, 0, 1);
        ReconocedorLineal recFibrillaCorta = new ReconocedorLineal(fibrillaCorta);

        Fibrilla fibrillaLarga = new Fibrilla("Fibrilla larga clara (>=7)", 7, 13, 15);
        ReconocedorLineal recFibrillaLarga = new ReconocedorLineal(fibrillaLarga);

        ReconocedorImagen[] reconocedores = { 
            recPatron1x3, 
            recFibrillaCorta, 
            recFibrillaLarga 
        };

        // Lanzar ventana demo
        System.out.println();
        System.out.println("Iniciando ventana de prueba interactiva...");
        new VentanaDemo(muestras, reconocedores).play();
    }

    /**
     * Carga una muestra desde un archivo local.
     *
     * @param ruta ruta del archivo
     * @param id identificador de la muestra
     * @return muestra cargada
     * @throws Exception si hay error en la carga
     */
    private static Muestra cargarMuestraDesdeArchivo(String ruta, String id) throws Exception {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(ruta));
        try {
            return new Muestra(in, id);
        } finally {
            in.close();
        }
    }
}
