package it.univaq.disim.lpo.dominiouniversitario.core;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Verbali implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Insegnamenti insegnamento;
    private List<EsameSostenuto> esami;
    private LocalDate data;

    public Verbali() {
        this.esami = new ArrayList<>();
    }

    public Verbali(Insegnamenti insegnamento, List<EsameSostenuto> esami, LocalDate data) {
        this.insegnamento = insegnamento;
        this.esami = esami != null ? esami : new ArrayList<>();
        this.data = data;
    }

    public Insegnamenti getInsegnamento() {
        return insegnamento;
    }

    public void setInsegnamento(Insegnamenti insegnamento) {
        this.insegnamento = insegnamento;
    }

    public List<EsameSostenuto> getEsami() {
        return esami;
    }

    public void setEsami(List<EsameSostenuto> esami) {
        this.esami = esami;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

}
