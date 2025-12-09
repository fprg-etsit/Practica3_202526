package es.upm.dit.fprg.p3;

import es.upm.dit.fprg.p3.EspecialidadMedica;

public class Medico {
    private String idColegiado;
    private String nombreCompleto;
    private String centroSanitario;
    private EspecialidadMedica especialidad;
    
    public Medico() {
    	
    }
    
    public Medico(String idColegiado, String nombreCompleto, String centroSanitario, EspecialidadMedica especialidad) {
        this.idColegiado = idColegiado;
        this.nombreCompleto = nombreCompleto;
        this.centroSanitario = centroSanitario;
        this.especialidad = especialidad;
    }

    public String getColegiado() {
        return idColegiado;
    }

    public void setColegiado(String colegiado) {
        this.idColegiado = colegiado;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCentroSanitario() {
        return centroSanitario;
    }

    public void setCentroSanitario(String centroSanitario) {
        this.centroSanitario = centroSanitario;
    }

    public EspecialidadMedica getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(EspecialidadMedica especialidad) {
        this.especialidad = especialidad;
    }

	@Override
	public String toString() {
		return "Medico [idColegiado=" + idColegiado + ", nombreCompleto=" + nombreCompleto + ", centroSanitario="
				+ centroSanitario + ", especialidad=" + especialidad + "]";
	}

}
