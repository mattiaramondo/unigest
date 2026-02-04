package it.univaq.disim.lpo.dominiouniversitario.core;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Docenti extends Persona {

    private static final long serialVersionUID = 1L;

    public enum TipoDocente {
        DI_RUOLO,
        A_CONTRATTO
    }

    private static int assegnaID = 1;
    private String idDocente;
    private TipoDocente ruolo;

    private List<Insegnamenti> insegnamenti;
    private List<Appello> appelli;
    private List<EsameSostenuto> esamiRegistrati;
    private List<Verbali> verbali;

    public Docenti() {
        super();
        this.insegnamenti = new ArrayList<>();
        this.appelli = new ArrayList<>();
        this.esamiRegistrati = new ArrayList<>();
        this.verbali = new ArrayList<>();
    }

    public Docenti(String nome, String cognome, String idDocente, String email, String password, TipoDocente ruolo) {
        super(nome, cognome, email, password);
        if (idDocente == null || idDocente.trim().isEmpty()) {
            this.idDocente = String.format("D%04d", assegnaID++);
        } else {
            this.idDocente = idDocente;
        }
        this.ruolo = ruolo;
        this.insegnamenti = new ArrayList<>();
        this.appelli = new ArrayList<>();
        this.esamiRegistrati = new ArrayList<>();
        this.verbali = new ArrayList<>();
    }

    public void assegnaIDUnico(
            it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneDocenti gestioneDocenti) {
        String idTemp = this.idDocente;
        while (gestioneDocenti.getDocenteById(idTemp) != null) {
            idTemp = String.format("D%04d", assegnaID++);
        }
        this.idDocente = idTemp;
    }

    public String getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(String idDocente) {
        this.idDocente = idDocente;
    }

    public TipoDocente getRuolo() {
        return ruolo;
    }

    public void setRuolo(TipoDocente ruolo) {
        this.ruolo = ruolo;
    }

    public List<Insegnamenti> getInsegnamenti() {
        return insegnamenti;
    }

    public void setInsegnamenti(List<Insegnamenti> insegnamenti) {
        this.insegnamenti = insegnamenti;
    }

    public List<Appello> getAppelli() {
        return appelli;
    }

    public void setAppelli(List<Appello> appelli) {
        this.appelli = appelli;
    }

    public List<EsameSostenuto> getEsamiRegistrati() {
        return esamiRegistrati;
    }

    public void setEsamiRegistrati(List<EsameSostenuto> esamiRegistrati) {
        this.esamiRegistrati = esamiRegistrati;
    }

    public List<Verbali> getVerbali() {
        return verbali;
    }

    public void setVerbali(List<Verbali> verbali) {
        this.verbali = verbali;
    }

    public void aggiungiInsegnamento(Insegnamenti insegnamento) {
        if (!this.insegnamenti.contains(insegnamento)) {
            this.insegnamenti.add(insegnamento);
        }
    }

    public void rimuoviInsegnamento(Insegnamenti insegnamento) {
        this.insegnamenti.remove(insegnamento);
    }
}
