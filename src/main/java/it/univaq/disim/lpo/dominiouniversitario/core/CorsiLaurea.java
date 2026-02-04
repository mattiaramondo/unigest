package it.univaq.disim.lpo.dominiouniversitario.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"insegnamenti", "studentiIscritti"})
public class CorsiLaurea implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome;
    private String codice;

    private List<Insegnamenti> insegnamenti;
    private List<Studenti> studentiIscritti;

    public CorsiLaurea() {
        this.insegnamenti = new ArrayList<>();
        this.studentiIscritti = new ArrayList<>();
    }

    public CorsiLaurea(String nome, String codice) {
        this.nome = nome;
        this.codice = codice;
        this.insegnamenti = new ArrayList<>();
        this.studentiIscritti = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public List<Insegnamenti> getInsegnamenti() {
        return insegnamenti;
    }

    public void setInsegnamenti(List<Insegnamenti> insegnamenti) {
        this.insegnamenti = insegnamenti;
    }

    public List<Studenti> getStudentiIscritti() {
        return studentiIscritti;
    }

    public void setStudentiIscritti(List<Studenti> studentiIscritti) {
        this.studentiIscritti = studentiIscritti;
    }

    @Override
    public String toString() {
        return nome + " (" + codice + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CorsiLaurea that = (CorsiLaurea) o;
        return codice != null ? codice.equals(that.codice) : that.codice == null;
    }

    @Override
    public int hashCode() {
        return codice != null ? codice.hashCode() : 0;
    }

    public void aggiungiInsegnamento(Insegnamenti insegnamento) {
        throw new UnsupportedOperationException("Unimplemented method 'aggiungiInsegnamento'");
    }

}

