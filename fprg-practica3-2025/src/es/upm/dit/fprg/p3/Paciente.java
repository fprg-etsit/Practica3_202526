package es.upm.dit.fprg.p3;

public class Paciente {
    private String dni;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private int anioNacimiento;
    
    public Paciente() {
    	
    }
    
    public Paciente(String dni, String nombre, String primerApellido, String segundoApellido, int anioNacimiento) {
        this.dni = dni;
        this.nombre = nombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.anioNacimiento = anioNacimiento;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public int getAnioNacimiento() {
        return anioNacimiento;
    }

    public void setAnioNacimiento(int anioNacimiento) {
        this.anioNacimiento = anioNacimiento;
    }
    
    public int getEdad() {
        return 2025 - this.anioNacimiento;
    }

    public boolean esPediatrico() {
        return (2025 - this.anioNacimiento) < 18;
    }


	@Override
	public String toString() {
		return "Paciente [dni=" + dni + ", nombre=" + nombre + ", primerApellido=" + primerApellido
				+ ", segundoApellido=" + segundoApellido + ", anioNacimiento=" + anioNacimiento + "]";
	}
    


}