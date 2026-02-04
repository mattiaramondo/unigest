package it.univaq.disim.lpo.dominiouniversitario.service;

import it.univaq.disim.lpo.dominiouniversitario.core.Appello;
import it.univaq.disim.lpo.dominiouniversitario.core.Docenti;
import it.univaq.disim.lpo.dominiouniversitario.core.Studenti;
import java.util.List;

public interface IGestioneAppelli {
    
    List<Appello> getTuttiGliAppelli();
    
    void aggiungiAppello(Docenti docente, Appello appello);
    
    void rimuoviAppello(Docenti docente, Appello appello);
    
    boolean prenotaStudente(Studenti studente, Appello appello);

    boolean annullaPrenotazione(Studenti studente, Appello appello);
    
}
