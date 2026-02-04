package it.univaq.disim.lpo.dominiouniversitario.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;

@JsonIgnoreProperties("appello")
public class Iscrizione implements Serializable {

    private static final long serialVersionUID = 1L;

    private Studenti studente;
    private Appello appello;
    private LocalDate dataIscrizione;

    public Iscrizione() {
    }

    public Iscrizione(Studenti studente, Appello appello) {
        this.studente = studente;
        this.appello = appello;
        this.dataIscrizione = LocalDate.now();
    }

    public Studenti getStudente() {
        return studente;
    }

    public void setStudente(Studenti studente) {
        this.studente = studente;
    }

    public Appello getAppello() {
        return appello;
    }

    public void setAppello(Appello appello) {
        this.appello = appello;
    }

    public LocalDate getDataIscrizione() {
        return dataIscrizione;
    }

    public void setDataIscrizione(LocalDate dataIscrizione) {
        this.dataIscrizione = dataIscrizione;
    }
}
