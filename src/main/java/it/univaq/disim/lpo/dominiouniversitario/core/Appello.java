package it.univaq.disim.lpo.dominiouniversitario.core;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Appello implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum StatoAppello {
        APERTO,
        CHIUSO,
        SOSTENUTO,
        ANNULLATO,
    }

    private Insegnamenti insegnamento;
    private LocalDate dataEsame;
    private LocalDate dataChiusuraIscrizione;
    private LocalDate dataApertura;
    private int numeroMassimoStudenti;
    private String luogo;
    private StatoAppello stato;
    private List<Iscrizione> iscrizioni;

    public Appello() {
    }

    public Appello(
            Insegnamenti insegnamento,
            LocalDate dataEsame,
            LocalDate dataApertura,
            LocalDate dataChiusuraIscrizione,
            int numeroMassimoStudenti,
            String luogo
    ) {
        this.insegnamento = insegnamento;
        this.dataEsame = dataEsame;
        this.dataApertura = dataApertura;
        this.dataChiusuraIscrizione = dataChiusuraIscrizione;
        this.numeroMassimoStudenti = numeroMassimoStudenti;
        this.luogo = luogo;
        this.stato = StatoAppello.APERTO;
        this.iscrizioni = new ArrayList<>();
    }

    public Insegnamenti getInsegnamento() { 
        return insegnamento; 
    }
    public void setInsegnamento(Insegnamenti insegnamento) { 
        this.insegnamento = insegnamento; 
    }
    public LocalDate getDataEsame() { 
        return dataEsame; 
    }
    public void setDataEsame(LocalDate dataEsame) { 
        this.dataEsame = dataEsame; 
    }
    public LocalDate getDataChiusuraIscrizione() { 
        return dataChiusuraIscrizione; 
    }
    public void setDataChiusuraIscrizione(LocalDate dataChiusuraIscrizione) { 
        this.dataChiusuraIscrizione = dataChiusuraIscrizione; 
    }
    public LocalDate getDataApertura() { 
        return dataApertura; 
    }
    public void setDataApertura(LocalDate dataApertura) { 
        this.dataApertura = dataApertura; 
    }
    public int getNumeroMassimoStudenti() { 
        return numeroMassimoStudenti; 
    }
    public void setNumeroMassimoStudenti(int numeroMassimoStudenti) { 
        this.numeroMassimoStudenti = numeroMassimoStudenti; 
    }
    public String getLuogo() { 
        return luogo; 
    }
    public void setLuogo(String luogo) { 
        this.luogo = luogo; 
    }
    public StatoAppello getStato() { 
        return stato; 
    }
    public void setStato(StatoAppello stato) { 
        this.stato = stato; 
    }
    public List<Iscrizione> getIscrizioni() { 
        return iscrizioni; 
    }
    public void setIscrizioni(List<Iscrizione> iscrizioni) { 
        this.iscrizioni = iscrizioni; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appello appello = (Appello) o;
        if (insegnamento != null ? !insegnamento.equals(appello.insegnamento) : appello.insegnamento != null) return false;
        return dataEsame != null ? dataEsame.equals(appello.dataEsame) : appello.dataEsame == null;
    }

    @Override
    public int hashCode() {
        int result = insegnamento != null ? insegnamento.hashCode() : 0;
        result = 31 * result + (dataEsame != null ? dataEsame.hashCode() : 0);
        return result;
    }

    @JsonIgnore
    public boolean isScaduto() {
        return LocalDate.now().isAfter(this.dataEsame);
    }

    @JsonIgnore
    public boolean isIscrizioneAperta() {
        LocalDate oggi = LocalDate.now();
        if (dataApertura == null || dataChiusuraIscrizione == null) return false;
        return (oggi.isEqual(dataApertura) || oggi.isAfter(dataApertura)) &&
               (oggi.isEqual(dataChiusuraIscrizione) || oggi.isBefore(dataChiusuraIscrizione));
    }
    
    @JsonIgnore
    public int getPostiDisponibili() {
        return this.numeroMassimoStudenti - (this.iscrizioni != null ? this.iscrizioni.size() : 0);
    }
}