package es.upm.dit.fprg.p3.auxiliar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.upm.dit.fprg.p3.*;

/**
 * Reúne un conjunto pequeño de muestras, patógenos y fibrillas artificiales para la práctica.
 * El objetivo es proporcionar datos constantes y controlados con los que probar
 * la lógica de los reconocedores y la interfaz de usuario.
 */
@SuppressWarnings("CallToPrintStackTrace")
public final class DatosPredefinidos {

    private static final int I = Patogeno.INDEFINIDO;
    private static final int[][] matrizPatronCruz = new int[][]{
            { I,  I, 15,  I,  I},
            { I,  I, 15,  I,  I},
            {15, 15, 15, 15, 15},
            { I,  I, 15,  I,  I},
            { I,  I, 15,  I,  I}
        };

    private static final int[][] matrizPatronCorona = crearPatronCoronaPequena();
    private static final int[][] matrizPatronCoronaConEspinas = crearPatronCoronaConEspinas();

    private final List<Muestra> muestras;
    private final List<Patogeno> patogenos;
    private final List<Fibrilla> fibrillas;
    private final List<ReconocedorImagen> reconocedores;

    // Singleton lazy-initialization holder class idiom
    private static class LazyHolder {
        static final DatosPredefinidos INSTANCE = new DatosPredefinidos();
    }
    
    public static DatosPredefinidos getInstance() {
        return LazyHolder.INSTANCE;
    }

    private DatosPredefinidos() {
        // Crear patógenos
        List<Patogeno> patogenosTemp = new ArrayList<>();
        try {
            Patogeno cruz = new Patogeno("Cruz 5x5", matrizPatronCruz);
            patogenosTemp.add(cruz);
        } catch (Exception ex) {
            ex.printStackTrace();        
        }

        try {
            Patogeno corona = new Patogeno("Corona 9x9", matrizPatronCorona);
            patogenosTemp.add(corona);
        } catch (Exception ex) {
            ex.printStackTrace();        
        }
        
        try {
            Patogeno coronaEspinas = new Patogeno("Corona 32x32 con espinas", 
                matrizPatronCoronaConEspinas);
            patogenosTemp.add(coronaEspinas);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Crear fibrillas
        List<Fibrilla> fibrillasTemp = new ArrayList<>();
        try {
            Fibrilla fibrillaOscura = new Fibrilla("Fibrilla oscura (long≥5)", 5, 0, 4);
            fibrillasTemp.add(fibrillaOscura);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        try {
            Fibrilla fibrillaClara = new Fibrilla("Fibrilla clara (long≥8)", 8, 11, 15);
            fibrillasTemp.add(fibrillaClara);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        try {
            Fibrilla fibrillaModerada = new Fibrilla("Fibrilla moderada (long≥6)", 6, 5, 10);
            fibrillasTemp.add(fibrillaModerada);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Crear muestras
        List<Muestra> muestrasTemp = new ArrayList<>();
        Muestra crucesDispersas = crearMuestraCruces();
        if (crucesDispersas != null) {
            muestrasTemp.add(crucesDispersas);
        }
        Muestra mixta = crearMuestraMixta();
        if (mixta != null) {
            muestrasTemp.add(mixta);
        }
        Muestra coronaEspinas = crearMuestraCoronaEspinas();
        if (coronaEspinas != null) {
            muestrasTemp.add(coronaEspinas);
        }
        Muestra conLineas = crearMuestraConLineas();
        if (conLineas != null) {
            muestrasTemp.add(conLineas);
        }

        // Crear reconocedores
        List<ReconocedorImagen> reconocedoresTemp = new ArrayList<>();
        for (Patogeno p : patogenosTemp) {
            try {
                reconocedoresTemp.add(new ReconocedorPatron(p));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        for (Fibrilla f : fibrillasTemp) {
            try {
                reconocedoresTemp.add(new ReconocedorLineal(f));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        patogenos = Collections.unmodifiableList(patogenosTemp);
        fibrillas = Collections.unmodifiableList(fibrillasTemp);
        muestras = Collections.unmodifiableList(muestrasTemp);
        reconocedores = Collections.unmodifiableList(reconocedoresTemp);
    }

    private Muestra crearMuestraCruces() {
        int[][] imagen = new int[32][32];
        rellenar(imagen, 3);

        colocarPatogeno(imagen, 4, 4, matrizPatronCruz);
        colocarPatogeno(imagen, 20, 6, matrizPatronCruz);
        colocarPatogeno(imagen, 10, 20, matrizPatronCruz);

        try {
            return new Muestra("Cruces dispersas", imagen);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private Muestra crearMuestraMixta() {
        int[][] imagen = new int[32][32];
        rellenar(imagen, 1);

        for (int fila = 0; fila < imagen.length; fila++) {
            for (int columna = 0; columna < imagen[0].length; columna++) {
                if ((fila + columna) % 9 == 0) {
                    imagen[fila][columna] = Math.min(15, imagen[fila][columna] + 2);
                }
            }
        }

        colocarPatogeno(imagen, 5, 8, matrizPatronCorona);
        colocarPatogeno(imagen, 16, 18, matrizPatronCorona);
        colocarPatogeno(imagen, 22, 4, matrizPatronCruz);
        colocarPatogeno(imagen, 8, 22, matrizPatronCruz);

        try {
            return new Muestra("Muestra mixta", imagen);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private Muestra crearMuestraCoronaEspinas() {
        int lado = 48;
        int[][] imagen = new int[lado][lado];
        rellenar(imagen, 2);

        for (int fila = 0; fila < lado; fila++) {
            for (int columna = 0; columna < lado; columna++) {
                if ((fila + columna) % 7 == 0) {
                    imagen[fila][columna] = Math.min(15, imagen[fila][columna] + 1);
                }
            }
        }

        colocarPatogeno(imagen, 8, 8, matrizPatronCoronaConEspinas);

        try {
            return new Muestra("Corona con espinas", imagen);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private Muestra crearMuestraConLineas() {
        int lado = 32;
        int[][] imagen = new int[lado][lado];
        rellenar(imagen, 2);

        // Línea horizontal clara
        for (int col = 5; col < 25; col++) {
            imagen[8][col] = 14;
        }

        // Línea vertical oscura
        for (int fila = 10; fila < 28; fila++) {
            imagen[fila][15] = 1;
        }

        // Línea diagonal moderada
        for (int i = 0; i < 15; i++) {
            if (18 + i < lado && 5 + i < lado) {
                imagen[18 + i][5 + i] = 7;
            }
        }

        // Algunas líneas cortas que no deberían detectarse
        for (int col = 28; col < 31; col++) {
            imagen[4][col] = 13;
        }

        try {
            return new Muestra("Muestra con líneas", imagen);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static void colocarPatogeno(int[][] matriz, int filaInicio, int columnaInicio, 
                                        int[][] patron) {
        int comodin = Patogeno.INDEFINIDO;
        for (int fila = 0; fila < patron.length; fila++) {
            for (int columna = 0; columna < patron[0].length; columna++) {
                int valor = patron[fila][columna];
                if (valor != comodin) {
                    matriz[filaInicio + fila][columnaInicio + columna] = valor;
                }
            }
        }
    }

    private static void rellenar(int[][] matriz, int valor) {
        for (int[] fila : matriz) {
            Arrays.fill(fila, valor);
        }
    }

    private static int[][] crearPatronCoronaPequena() {
        int size = 9;
        int[][] matriz = new int[size][size];
        double centro = (size - 1) / 2.0;
        double radioNucleo = 1.6;
        double radioCoronaInterior = 2.6;
        double radioCoronaExterior = 3.6;
        double halo = 4.2;

        for (int fila = 0; fila < size; fila++) {
            Arrays.fill(matriz[fila], I);
            for (int columna = 0; columna < size; columna++) {
                double dx = columna - centro;
                double dy = fila - centro;
                double distancia = Math.hypot(dx, dy);

                if (distancia <= radioNucleo) {
                    matriz[fila][columna] = 0xF;
                } else if (distancia <= radioCoronaInterior) {
                    matriz[fila][columna] = 0xE;
                } else if (distancia <= radioCoronaExterior) {
                    matriz[fila][columna] = 0xC;
                } else if (distancia <= halo) {
                    matriz[fila][columna] = 0xB;
                }
            }
        }
        return matriz;
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
            Arrays.fill(matriz[fila], I);
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
            {1.0, 0.0}, {-1.0, 0.0}, {0.0, 1.0}, {0.0, -1.0},
            {diagonal, diagonal}, {diagonal, -diagonal}, 
            {-diagonal, diagonal}, {-diagonal, -diagonal}
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

    public List<Muestra> getMuestras() {
        return muestras;
    }

    public List<Patogeno> getPatogenos() {
        return patogenos;
    }
    
    public List<Fibrilla> getFibrillas() {
        return fibrillas;
    }
    
    public List<ReconocedorImagen> getReconocedores() {
        return reconocedores;
    }
    
    public Muestra getMuestra(String nombre) {
        if (nombre == null) {
            return null;
        }
        for (Muestra muestra : muestras) {
            if (nombre.equals(muestra.getId())) {
                return muestra;
            }
        }
        return null;
    }

    public Patogeno getPatogeno(String nombre) {
        if (nombre == null) {
            return null;
        }
        for (Patogeno patogeno : patogenos) {
            if (nombre.equals(patogeno.getNombre())) {
                return patogeno;
            }
        }
        return null;
    }
    
    public Fibrilla getFibrilla(String nombre) {
        if (nombre == null) {
            return null;
        }
        for (Fibrilla fibrilla : fibrillas) {
            if (nombre.equals(fibrilla.getNombre())) {
                return fibrilla;
            }
        }
        return null;
    }
}
