package it.univaq.disim.lpo.dominiouniversitario.service;

import it.univaq.disim.lpo.dominiouniversitario.core.Appello;
import it.univaq.disim.lpo.dominiouniversitario.core.CorsiLaurea;
import it.univaq.disim.lpo.dominiouniversitario.core.Docenti;
import it.univaq.disim.lpo.dominiouniversitario.core.Insegnamenti;
import it.univaq.disim.lpo.dominiouniversitario.core.Studenti;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface IGestioneDocenti {

    List<Docenti> caricaDocenti() throws IOException;

    void inserisciDati(List<Docenti> listaDocentiJson);
    
    Docenti getProfiloDocente();
    
    void setProfiloDocente(Docenti profiloDocente);
    
    Docenti getDocenteDaModificare();
    void setDocenteDaModificare(Docenti docente);
    void clearDocenteDaModificare();

    CorsiLaurea getCorsoSelezionato();
    void setCorsoSelezionato(CorsiLaurea corsoSelezionato);
    
    Docenti verificaLogin(String email, String password);

    void aggiungiDocente(Docenti docente);
    
    void rimuoviDocente(String idDocente);
    
    Docenti getDocenteById(String idDocente);
    
    List<Docenti> getDocenti();
    
    void assegnaInsegnamento(String idDocente, Insegnamenti insegnamento);
    
    void rimuoviInsegnamento(String idDocente, Insegnamenti insegnamento);
    
    Appello creaAppello(String idDocente, Insegnamenti insegnamento, LocalDate dataChiusuraIscrizione);
    
    List<Studenti> visualizzaIscritti(String idDocente, Appello appello);

    void salvaDocenti();
}
