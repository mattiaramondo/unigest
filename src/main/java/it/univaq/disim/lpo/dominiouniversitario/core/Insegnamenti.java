package it.univaq.disim.lpo.dominiouniversitario.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"docenti", "appelli"})
public class Insegnamenti implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome;
    private String codiceInsegnamento;
    private int cfu;
    private List<Docenti> docenti;
    private CorsiLaurea corsoDiLaurea;
    private List<Appello> appelli;

    public Insegnamenti() {
    }

    public Insegnamenti(String nome, String codiceInsegnamento, int cfu, CorsiLaurea corsoDiLaurea) {
        this.nome = nome;
        this.codiceInsegnamento = codiceInsegnamento;
        this.cfu = cfu;
        this.corsoDiLaurea = corsoDiLaurea;
        this.docenti = new ArrayList<>();
        this.appelli = new ArrayList<>(); 
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodiceInsegnamento() {
        return codiceInsegnamento;
    }

    public void setCodiceInsegnamento(String codiceInsegnamento) {
        this.codiceInsegnamento = codiceInsegnamento;
    }

    public int getCfu() {
        return cfu;
    }

    public void setCfu(int cfu) {
        this.cfu = cfu;
    }

    public List<Docenti> getDocenti() {
        return docenti;
    }

    public void setDocenti(List<Docenti> docenti) {
        this.docenti = docenti;
    }

    public CorsiLaurea getCorsoDiLaurea() {
        return corsoDiLaurea;
    }

    public void setCorsoDiLaurea(CorsiLaurea corsoDiLaurea) {
        this.corsoDiLaurea = corsoDiLaurea;
    }

    public List<Appello> getAppelli() {
        return appelli;
    }

    public void setAppelli(List<Appello> appelli) {
        this.appelli = appelli;
    }

    public void aggiungiDocente(Docenti docente) {
        if (!this.docenti.contains(docente)) {
            this.docenti.add(docente);
        }
    }

    public void rimuoviDocente(Docenti docente) {
        this.docenti.remove(docente);
    }

    public void aggiungiAppello(Appello appello) {
        if (!this.appelli.contains(appello)) {
            this.appelli.add(appello);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Insegnamenti that = (Insegnamenti) o;
        return codiceInsegnamento != null && codiceInsegnamento.equals(that.codiceInsegnamento);
    }

    @Override
    public int hashCode() {
        return codiceInsegnamento != null ? codiceInsegnamento.hashCode() : 0;
    }
}
