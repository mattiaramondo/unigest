package it.univaq.disim.lpo.dominiouniversitario.service;

import it.univaq.disim.lpo.dominiouniversitario.core.Insegnamenti;
import java.util.List;

public interface IGestioneInsegnamenti {
    
    void aggiungiInsegnamento(Insegnamenti insegnamento);
    
    void rimuoviInsegnamento(String codiceInsegnamento);
    
    Insegnamenti getInsegnamentoByCodice(String codiceInsegnamento);
    
    List<Insegnamenti> getTuttiGliInsegnamenti();
    
    List<Insegnamenti> cercaInsegnamentiPerNome(String nome);
    
    List<Insegnamenti> cercaInsegnamentiPerCfu(int cfu);
    
}
