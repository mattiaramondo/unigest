package it.univaq.disim.lpo.dominiouniversitario.core;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Studenti extends Persona {

    private static final long serialVersionUID = 1L;

    public enum AnnoAccademico {
        PRIMO_ANNO("Primo anno accademico"),
        SECONDO_ANNO("Secondo anno accademico"),
        TERZO_ANNO("Terzo anno accademico"),
        FUORI_CORSO("Fuori corso");

        private final String descrizione;

        AnnoAccademico(String d) {
            this.descrizione = d;
        }

        @Override
        public String toString() {
            return descrizione;
        }
    }

    public enum TipoLaurea {
        LAUREA_TRIENNALE("Corso di laurea triennale"),
        LAUREA_MAGISTRALE("Corso di laurea magistrale");

        private final String descrizione;

        TipoLaurea(String d) {
            this.descrizione = d;
        }

        @Override
        public String toString() {
            return descrizione;
        }
    }

    private static int assegnaMatricola = 100000;
    private String matricola;
    private AnnoAccademico annoDiCorso;
    private TipoLaurea tipologiaLaurea;
    private List<EsameSostenuto> listaEsami;
    private CorsiLaurea corsoDiLaurea;

    public Studenti() {
        super();
        this.listaEsami = new ArrayList<>();
    }

    public Studenti(String nome,
            String cognome,
            String password,
            String matricola,
            AnnoAccademico annoDiCorso,
            TipoLaurea tipologiaLaurea,
            CorsiLaurea corsoDiLaurea) {
        super(nome, cognome, nome.toLowerCase() + "." + cognome.toLowerCase() + "@student.univaq.it", password);
        this.matricola = String.valueOf(assegnaMatricola++);
        this.annoDiCorso = annoDiCorso;
        this.tipologiaLaurea = tipologiaLaurea;
        this.listaEsami = new ArrayList<>();
        this.corsoDiLaurea = corsoDiLaurea;
    }

    public void assegnaMatricolaUnica(
            it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneStudenti gestioneStudenti) {
        String matricolaTemp = this.matricola;
        while (gestioneStudenti.cercaStudente(matricolaTemp) != null) {
            assegnaMatricola++;
            matricolaTemp = String.valueOf(assegnaMatricola++);
        }
        this.matricola = matricolaTemp;
    }

    public String getMatricola() {
        return matricola;
    }

    public void setMatricola(String nuovaMatricola) {
        this.matricola = nuovaMatricola;
    }

    public AnnoAccademico getAnnoDiCorso() {
        return annoDiCorso;
    }

    public void setAnnoDiCorso(AnnoAccademico annoDiCorso) {
        this.annoDiCorso = annoDiCorso;
    }

    public TipoLaurea getTipologiaLaurea() {
        return tipologiaLaurea;
    }

    public void setTipologiaLaurea(TipoLaurea tipologiaLaurea) {
        this.tipologiaLaurea = tipologiaLaurea;
    }

    public CorsiLaurea getCorsoDiLaurea() {
        return corsoDiLaurea;
    }

    public void setCorsoDiLaurea(CorsiLaurea corsoDiLaurea) {
        this.corsoDiLaurea = corsoDiLaurea;
    }

    public List<EsameSostenuto> getListaEsami() {
        if (listaEsami == null) {
            listaEsami = new ArrayList<>();
        }
        return listaEsami;
    }

    public void setListaEsami(List<EsameSostenuto> listaEsami) {
        this.listaEsami = listaEsami;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Studenti studenti = (Studenti) o;
        return matricola != null && matricola.equals(studenti.matricola);
    }

    @Override
    public int hashCode() {
        return matricola != null ? matricola.hashCode() : 0;
    }

    @JsonIgnore
    public boolean haEsamiSostenuti() {
        return this.listaEsami != null && !this.listaEsami.isEmpty();
    }
}
