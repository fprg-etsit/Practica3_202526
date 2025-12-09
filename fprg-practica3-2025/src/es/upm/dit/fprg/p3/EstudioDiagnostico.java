package es.upm.dit.fprg.p3;

public class EstudioDiagnostico {
    private TecnicaAdquisicion tecnica;
    private Medico prescriptor;
    private Medico informador; 
    private Paciente paciente;
    private int timestampPrescripcion; 

    public EstudioDiagnostico(TecnicaAdquisicion tecnica, Medico prescriptor, Paciente paciente) {
        this.tecnica = tecnica;
        this.prescriptor = prescriptor;
        this.paciente = paciente;
        long epochSeconds = System.currentTimeMillis() / 1000;
        this.timestampPrescripcion = (int) epochSeconds; 
        this.informador = null;
    }

    public TecnicaAdquisicion getTecnica() {
        return tecnica;
    }

    public void setTecnica(TecnicaAdquisicion tecnica) {
        this.tecnica = tecnica;
    }

    public Medico getPrescriptor() {
        return prescriptor;
    }

    public void setPrescriptor(Medico prescriptor) {
        this.prescriptor = prescriptor;
    }

    public Medico getInformador() {
        return informador;
    }

    public void setInformador(Medico informador) {
        this.informador = informador;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public int getTimestampPrescripcion() {
        return timestampPrescripcion;
    }

    public void setTimestampPrescripcion(int ts) {
        this.timestampPrescripcion = ts;
    }

	@Override
	public String toString() {
		return "EstudioDiagnostico [tecnica=" + tecnica + ", prescriptor=" + prescriptor + ", informador=" + String.valueOf(informador)
				+ ", paciente=" + paciente + ", timestampPrescripcion=" + timestampPrescripcion + "]";
	}

}