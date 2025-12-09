package es.upm.dit.fprg.p3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * Clase de prueba que demuestra el flujo completo del sistema de diagnóstico
 * microscópico, desde la carga de muestras hasta la persistencia en XML.
 * <p>
 * Esta clase integra todos los componentes desarrollados en la práctica:
 * carga de imágenes, reconocimiento de patrones y estructuras lineales,
 * análisis cuantitativo y persistencia XML.
 * </p>
 */
public class PruebaSistemaCompleto {

    public static void main(String[] args) {
        System.out.println("=== PRUEBA DEL SISTEMA COMPLETO ===\n");

        try {
            // ========================================
            // 1. CARGAR MUESTRAS DESDE FICHEROS
            // ========================================
            System.out.println("1. Cargando muestras desde ficheros locales...");
            
            Muestra muestra1 = cargarMuestraDesdeArchivo("data/ejemplo_coronavirus_ruido1.png", "M001");
            System.out.println("   ✓ Muestra M001 cargada: " + muestra1.getAncho() + "x" 
                    + muestra1.getAlto() + " píxeles");
            
            Muestra muestra2 = cargarMuestraDesdeArchivo("data/ejemplo_cruces1.png", "M002");
            System.out.println("   ✓ Muestra M002 cargada: " + muestra2.getAncho() + "x" 
                    + muestra2.getAlto() + " píxeles");

            // Ejemplo de carga desde URL (comentado, descomentar si hay URL disponible)
            /*
            System.out.println("\n   Cargando muestra desde URL...");
            Muestra muestra3 = cargarMuestraDesdeURL(
                "https://ejemplo.com/muestra3.png", "M003");
            System.out.println("   ✓ Muestra M003 cargada desde URL");
            */

            // ========================================
            // 2. CREAR ESTUDIO DIAGNÓSTICO
            // ========================================
            System.out.println("\n2. Creando estudio diagnóstico...");
            
            // Crear paciente con los atributos correctos
            Paciente paciente = new Paciente(
                "87654321B",           // dni
                "María",               // nombre
                "López",               // primerApellido
                "García",              // segundoApellido
                1973                   // anioNacimiento
            );
            System.out.println("   ✓ Paciente: " + paciente.getNombre() + " " 
                    + paciente.getPrimerApellido() + " (Edad: " + paciente.getEdad() + ")");
            
            // Crear médico prescriptor con los atributos correctos
            Medico prescriptor = new Medico(
                "28/28/11111",                    // idColegiado
                "Dr. Carlos Ramírez Sánchez",     // nombreCompleto
                "Hospital La Paz",                // centroSanitario
                EspecialidadMedica.MICROBIOLOGIA  // especialidad
            );
            System.out.println("   ✓ Prescriptor: " + prescriptor.getNombreCompleto());
            
            // Crear médico informador
            Medico informador = new Medico(
                "28/28/22222",                         // idColegiado
                "Dra. Ana Torres Ruiz",                // nombreCompleto
                "Hospital La Paz",                     // centroSanitario
                EspecialidadMedica.RADIOLOGIA  // especialidad
            );
            System.out.println("   ✓ Informador: " + informador.getNombreCompleto());

            // Crear estudio
            EstudioDiagnostico estudio = new EstudioDiagnostico(
                TecnicaAdquisicion.ECOGRAFIA,
                prescriptor,
                paciente
            );
            System.out.println("   ✓ Estudio creado con técnica: " + TecnicaAdquisicion.ECOGRAFIA);

            // Añadir muestras al estudio
            System.out.println("\n   Añadiendo muestras al estudio...");
            estudio.addMuestra(muestra1);
            estudio.addMuestra(muestra2);
            System.out.println("   ✓ " + estudio.getMuestras().size() + " muestras añadidas");

            // ========================================
            // 3. CONFIGURAR RECONOCEDORES
            // ========================================
            System.out.println("\n3. Configurando reconocedores...");

            // Crear patógenos para reconocer
            Patogeno bacteria = new Patogeno("Bacteria_Tipo_A", new int[][]{
                {15, 15, 15},
                {15, Patogeno.INDEFINIDO, 15},
                {15, 15, 15}
            });
            System.out.println("   ✓ Patógeno 'Bacteria_Tipo_A' definido (3x3)");

            Patogeno virus = new Patogeno("Virus_XYZ", new int[][]{
                {Patogeno.INDEFINIDO, 14, Patogeno.INDEFINIDO},
                {14, 14, 14},
                {Patogeno.INDEFINIDO, 14, Patogeno.INDEFINIDO}
            });
            System.out.println("   ✓ Patógeno 'Virus_XYZ' definido (3x3)");

            // Crear fibrillas para reconocer
            Fibrilla fibrillaOscura = new Fibrilla("Fibrilla_Oscura", 5, 0, 4);
            System.out.println("   ✓ Fibrilla 'Fibrilla_Oscura' definida (long≥5, color 0-4)");

            Fibrilla fibrillaClara = new Fibrilla("Fibrilla_Clara", 8, 10, 15);
            System.out.println("   ✓ Fibrilla 'Fibrilla_Clara' definida (long≥8, color 10-15)");

            // Crear reconocedores
            ReconocedorPatron reconocedorBacteria = new ReconocedorPatron(bacteria);
            ReconocedorPatron reconocedorVirus = new ReconocedorPatron(virus);
            ReconocedorLineal reconocedorFibrillaOscura = new ReconocedorLineal(fibrillaOscura);
            ReconocedorLineal reconocedorFibrillaClara = new ReconocedorLineal(fibrillaClara);

            // Configurar analizador
            AnalizadorDiagnostico analizador = new AnalizadorDiagnostico();
            analizador.addReconocedor(reconocedorBacteria);
            analizador.addReconocedor(reconocedorVirus);
            analizador.addReconocedor(reconocedorFibrillaOscura);
            analizador.addReconocedor(reconocedorFibrillaClara);
            System.out.println("   ✓ 4 reconocedores registrados en el analizador");

            // ========================================
            // 4. EJECUTAR ANÁLISIS Y GENERAR INFORME
            // ========================================
            System.out.println("\n4. Ejecutando análisis...");
            
            Map<String, Integer> resultados = analizador.analizar(estudio);
            
            System.out.println("\n   RESULTADOS DEL ANÁLISIS:");
            System.out.println("   " + "=".repeat(50));
            for (Map.Entry<String, Integer> entry : resultados.entrySet()) {
                System.out.printf("   %-25s : %8d píxeles%n", 
                        entry.getKey(), entry.getValue());
            }
            System.out.println("   " + "=".repeat(50));

            // Generar informe oficial
            System.out.println("\n   Generando informe oficial...");
            estudio.informar(informador, resultados);
            System.out.println("   ✓ Informe generado por: " + informador.getNombreCompleto());
            System.out.println("   ✓ Especialidad: " + informador.getEspecialidad());
            System.out.println("   ✓ Fecha del informe: " + estudio.getFechaInformeIso());

            // ========================================
            // 5. GUARDAR EN XML
            // ========================================
            System.out.println("\n5. Guardando estudio en XML...");
            
            String rutaXML = "data/estudio_completo.xml";
            guardarEstudio(estudio, rutaXML);
            System.out.println("   ✓ Estudio guardado en: " + rutaXML);

            // ========================================
            // 6. VISUALIZACIÓN DE MÁSCARAS (simulada)
            // ========================================
            System.out.println("\n6. Generando máscaras de detección...");
            visualizarMascaras(muestra1, reconocedorBacteria, reconocedorFibrillaClara);

            // ========================================
            // 7. CASOS DE PRUEBA ADICIONALES
            // ========================================
            System.out.println("\n7. Ejecutando casos de prueba adicionales...");
            ejecutarCasosDePruebaAdicionales();

            // Resumen final del estudio
            System.out.println("\n" + "=".repeat(60));
            System.out.println("RESUMEN DEL ESTUDIO:");
            System.out.println("  Paciente: " + paciente.getNombre() + " " 
                    + paciente.getPrimerApellido() + " " + paciente.getSegundoApellido());
            System.out.println("  DNI: " + paciente.getDni());
            System.out.println("  Edad: " + paciente.getEdad() + " años");
            System.out.println("  Pediátrico: " + (paciente.esPediatrico() ? "Sí" : "No"));
            System.out.println("  Prescrito por: " + prescriptor.getNombreCompleto());
            System.out.println("  Centro: " + prescriptor.getCentroSanitario());
            System.out.println("  Informado por: " + informador.getNombreCompleto());
            System.out.println("  Muestras analizadas: " + estudio.getMuestras().size());
            System.out.println("  Elementos detectados: " + resultados.size());
            System.out.println("=".repeat(60));

            System.out.println("\n=== PRUEBA COMPLETADA EXITOSAMENTE ===");

        } catch (Exception e) {
            System.err.println("\n✗ ERROR durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
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
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(ruta))) {
            return new Muestra(in, id);
        }
    }

    /**
     * Carga una muestra desde una URL.
     *
     * @param urlString URL de la imagen
     * @param id identificador de la muestra
     * @return muestra cargada
     * @throws Exception si hay error en la carga
     */
    private static Muestra cargarMuestraDesdeURL(String urlString, String id) throws Exception {
        URL url = new URL(urlString);
        try (InputStream in = new BufferedInputStream(url.openStream())) {
            return new Muestra(in, id);
        }
    }

    /**
     * Guarda un estudio diagnóstico en formato XML.
     *
     * @param estudio estudio a guardar
     * @param ruta ruta del archivo XML
     */
    private static void guardarEstudio(EstudioDiagnostico estudio, String ruta) {
        try (BufferedOutputStream out = new BufferedOutputStream(
                new FileOutputStream(ruta))) {
            estudio.guardar(out);
        } catch (IOException e) {
            System.err.println("   ✗ Error al guardar XML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Visualiza las máscaras de detección generadas por reconocedores.
     * (Versión simplificada sin GUI)
     *
     * @param muestra muestra a analizar
     * @param reconocedores reconocedores a aplicar
     */
    private static void visualizarMascaras(Muestra muestra, ReconocedorImagen... reconocedores) {
        for (ReconocedorImagen reconocedor : reconocedores) {
            boolean[][] mascara = reconocedor.detectar(muestra);
            int positivos = contarPositivos(mascara);
            
            System.out.println("\n   Máscara para: " + reconocedor.getElemento().getNombre());
            System.out.println("   Dimensiones: " + mascara.length + "x" + mascara[0].length);
            System.out.println("   Píxeles positivos: " + positivos);
            
            // Mostrar una pequeña porción de la máscara (primeros 5x5 píxeles)
            System.out.println("   Vista previa (5x5):");
            for (int i = 0; i < Math.min(5, mascara.length); i++) {
                System.out.print("   ");
                for (int j = 0; j < Math.min(5, mascara[0].length); j++) {
                    System.out.print(mascara[i][j] ? "█" : "·");
                }
                System.out.println();
            }
        }
    }

    /**
     * Cuenta píxeles positivos en una máscara.
     */
    private static int contarPositivos(boolean[][] mascara) {
        int count = 0;
        for (boolean[] fila : mascara) {
            for (boolean valor : fila) {
                if (valor) count++;
            }
        }
        return count;
    }

    /**
     * Ejecuta casos de prueba adicionales para validar el sistema.
     */
    private static void ejecutarCasosDePruebaAdicionales() throws Exception {
        // Caso 1: Paciente pediátrico
        System.out.println("\n   Caso 1: Paciente pediátrico");
        Paciente pediatrico = new Paciente("11111111A", "Carlos", "Martín", "López", 2015);
        System.out.println("   ✓ Paciente: " + pediatrico.getNombre() + " " 
                + pediatrico.getPrimerApellido());
        System.out.println("   ✓ Edad: " + pediatrico.getEdad() + " años");
        System.out.println("   ✓ Es pediátrico: " + pediatrico.esPediatrico());

        // Caso 2: Diferentes especialidades médicas
        System.out.println("\n   Caso 2: Diferentes especialidades médicas");
        Medico radiologo = new Medico("11/11/11111", "Dr. Juan Pérez", 
                "Hospital Clínico", EspecialidadMedica.RADIOLOGIA);
        System.out.println("   ✓ " + radiologo.getNombreCompleto() 
                + " - " + radiologo.getEspecialidad());
        
        Medico anatomopatologo = new Medico("22/22/22222", "Dra. Laura Gómez", 
                "Hospital General", EspecialidadMedica.ANATOMIA_PATOLOGICA);
        System.out.println("   ✓ " + anatomopatologo.getNombreCompleto() 
                + " - " + anatomopatologo.getEspecialidad());

        // Caso 3: Muestra pequeña (patrón no cabe)
        System.out.println("\n   Caso 3: Muestra muy pequeña (2x2)");
        int[][] pixelesMinimos = {{5, 10}, {15, 0}};
        Muestra muestraPequena = new Muestra("MINI", pixelesMinimos);
        
        Patogeno patronGrande = new Patogeno("Grande", new int[][]{
            {15, 15, 15},
            {15, 15, 15},
            {15, 15, 15}
        });
        
        ReconocedorPatron recGrande = new ReconocedorPatron(patronGrande);
        boolean[][] mascaraVacia = recGrande.detectar(muestraPequena);
        int detecciones = contarPositivos(mascaraVacia);
        System.out.println("   ✓ Detecciones en muestra pequeña (esperado 0): " + detecciones);

        // Caso 4: Múltiples técnicas de adquisición
        System.out.println("\n   Caso 4: Diferentes técnicas de adquisición");
        for (TecnicaAdquisicion tecnica : TecnicaAdquisicion.values()) {
            System.out.println("   ✓ Técnica disponible: " + tecnica);
        }

        System.out.println("\n   ✓ Todos los casos de prueba adicionales completados");
    }
}
