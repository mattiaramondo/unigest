package it.univaq.disim.lpo.dominiouniversitario.core;

// Importazione dell'interfaccia Serializable per la serializzazione degli oggetti.
import java.io.Serializable;
import java.time.LocalDate;

public class EsameSostenuto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Insegnamenti insegnamento;
    private int voto;
    private boolean lode;
    private int cfu;
    private LocalDate data;

    public EsameSostenuto() {
    }

    public EsameSostenuto(Insegnamenti insegnamento, int voto, boolean lode, int cfu, LocalDate data) {
        this.insegnamento = insegnamento;
        this.voto = voto;
        this.lode = lode;
        this.cfu = cfu;
        this.data = data;
    }

    public Insegnamenti getInsegnamento() { 
        return insegnamento; 
    }
    public void setInsegnamento(Insegnamenti insegnamento) { 
        this.insegnamento = insegnamento; 
    }

    public int getVoto() { 
        return voto; 
    }
    public void setVoto(int voto) { 
        this.voto = voto;
    }

    public boolean isLode() { 
        return lode; 
    }
    public void setLode(boolean lode) { 
        this.lode = lode; 
    }

    public int getCfu() { 
        return cfu; 
    }
    public void setCfu(int cfu) { 
        this.cfu = cfu; 
    }
    public LocalDate getData() { 
        return data; 
    }
    public void setData(LocalDate data) { 
        this.data = data; 
    }
    
}

