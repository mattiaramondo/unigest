package it.univaq.disim.lpo.dominiouniversitario.core;

import java.io.Serializable;

public class AuleEdifici implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nomeAula;
    private int capienza;

    public AuleEdifici() {
    }

    public AuleEdifici(String nomeAula, int capienza) {
        this.nomeAula = nomeAula;
        this.capienza = capienza;
    }

    public String getNomeAula() {
        return nomeAula;
    }
    public void setNomeAula(String nomeAula) {
        this.nomeAula = nomeAula;
    }
    public int getCapienza() {
        return capienza;
    }
    public void setCapienza(int capienza) {
        this.capienza = capienza;
    }
}


