package it.univaq.disim.lpo.dominiouniversitario.service;

import it.univaq.disim.lpo.dominiouniversitario.core.CorsiLaurea;
import it.univaq.disim.lpo.dominiouniversitario.core.Insegnamenti;
import it.univaq.disim.lpo.dominiouniversitario.core.Studenti;
import java.util.List;

public interface IGestioneCorsiLaurea {
    
    void aggiungiCorso(CorsiLaurea corso);

    void rimuoviCorso(String codiceCorso);
    
    List<String> getNomiCorsiDisponibili();
    
    CorsiLaurea getCorsoByNome(String nome);
    
    void aggiungiInsegnamento(String codiceCorso, Insegnamenti insegnamento);
    
    void rimuoviInsegnamento(String codiceCorso, Insegnamenti insegnamento);
    
    void iscriviStudente(String codiceCorso, Studenti studente);
    
    void rimuoviStudente(String codiceCorso, Studenti studente);
    
    int calcolaCfuTotali(String codiceCorso);
    
    void stampaNomiCorsi();
    
}
