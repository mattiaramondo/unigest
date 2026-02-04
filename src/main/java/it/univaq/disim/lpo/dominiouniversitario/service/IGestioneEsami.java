package it.univaq.disim.lpo.dominiouniversitario.service;

import it.univaq.disim.lpo.dominiouniversitario.core.Appello;
import it.univaq.disim.lpo.dominiouniversitario.core.EsameSostenuto;
import it.univaq.disim.lpo.dominiouniversitario.core.Insegnamenti;
import it.univaq.disim.lpo.dominiouniversitario.core.Studenti;
import java.time.LocalDate;

public interface IGestioneEsami {
    
    void creaEsameSostenuto(Studenti s, 
                           Insegnamenti insegnamento, 
                           int voto, 
                           boolean lode, 
                           int cfu, 
                           LocalDate data);
        
    void aggiungiEsame(String idDocente, EsameSostenuto esame);

    EsameSostenuto registraVoto(String idDocente, Studenti studente, Appello appello, int voto, boolean lode);
    
}
