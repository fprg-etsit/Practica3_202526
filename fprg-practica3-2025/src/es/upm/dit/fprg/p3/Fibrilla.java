package es.upm.dit.fprg.p3;


/**
 * Representa una fibrilla (estructura lineal) en una muestra médica.
 */
public final class Fibrilla implements ElementoMicroscopico {
    private String nombre;
    private int longitudMin;
    private int colorMin;
    private int colorMax;
    
    public Fibrilla() {
        // Constructor sin argumentos para JavaBeans
    }
    
    public Fibrilla(String nombre, int longitudMin, int colorMin, int colorMax) throws Exception {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new Exception("El nombre no puede ser nulo ni vacío");
        }
        if (longitudMin < 2) {
            throw new Exception("La longitud mínima debe ser >= 2");
        }
        if (colorMin < 0 || colorMin > 15) {
            throw new Exception("colorMin debe estar entre 0 y 15");
        }
        if (colorMax < 0 || colorMax > 15) {
            throw new Exception("colorMax debe estar entre 0 y 15");
        }
        if (colorMin > colorMax) {
            throw new Exception("colorMin no puede ser mayor que colorMax");
        }
        
        this.nombre = nombre;
        this.longitudMin = longitudMin;
        this.colorMin = colorMin;
        this.colorMax = colorMax;
    }
    
    @Override
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public int getLongitudMin() { return longitudMin; }
    public void setLongitudMin(int longitudMin) { this.longitudMin = longitudMin; }
    
    public int getColorMin() { return colorMin; }
    public void setColorMin(int colorMin) { this.colorMin = colorMin; }
    
    public int getColorMax() { return colorMax; }
    public void setColorMax(int colorMax) { this.colorMax = colorMax; }
    
    @Override
    public String toString() {
        return "Fibrilla[" + nombre + ", long>=" + longitudMin + 
               ", color=" + colorMin + "-" + colorMax + "]";
    }
}
