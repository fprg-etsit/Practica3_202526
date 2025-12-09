package es.upm.dit.fprg.p3;

import es.upm.dit.fprg.p3.auxiliar.VentanaDemo;

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
        int[][] pixelesEjemplo3x3 = {
                { 0, 1, 2 },
                { 2, 3, 4 },
                { 4, 5, 6 }
        };
        Muestra ejemploMuestra3x3 = new Muestra("Ejemplo 3x3", pixelesEjemplo3x3);

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

        Muestra[] muestras = { ejemploMuestra3x3, muestraConLineas };

        // ========== RECONOCEDORES ADICIONALES ==========
        // Patógenos
        int I = Patogeno.INDEFINIDO;
        int[][] patronEjemplo1x3 = {{ 0, I, 0 }};
        Patogeno ejemploPatogeno1x3 = new Patogeno("Patrón 1x3", patronEjemplo1x3);
        ReconocedorPatron recPatron1x3 = new ReconocedorPatron(ejemploPatogeno1x3);


        // Fibrillas
        Fibrilla fibrillaCorta = new Fibrilla("Fibrilla corta oscura (≥3)", 3, 0, 1);
        ReconocedorLineal recFibrillaCorta = new ReconocedorLineal(fibrillaCorta);

        Fibrilla fibrillaLarga = new Fibrilla("Fibrilla larga clara (≥7)", 7, 13, 15);
        ReconocedorLineal recFibrillaLarga = new ReconocedorLineal(fibrillaLarga);

        ReconocedorImagen[] reconocedores = { 
            recPatron1x3, 
            recFibrillaCorta, 
            recFibrillaLarga 
        };

        // Lanzar ventana demo
        new VentanaDemo(muestras, reconocedores).play();
    }
}
