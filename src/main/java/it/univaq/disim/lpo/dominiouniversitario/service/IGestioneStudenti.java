package it.univaq.disim.lpo.dominiouniversitario.service;

import it.univaq.disim.lpo.dominiouniversitario.core.Studenti;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IGestioneStudenti {

    List<Studenti> caricaStudenti() throws IOException;

    void salvaStudenti(List<Studenti> datiDaSalvare);

    void salvaStudenti();
    
    Studenti getProfiloStudente();
    
    void setProfiloStudente(Studenti profiloStudente);
    void clearStudenteDaModificare();

    Studenti verificaLogin(String email, String password);
    
    void inserisciDati(List<Studenti> listaStudentiJson);
    
    List<Studenti> getStudentiJson();
    
    Map<String, Studenti> getStudentiMap();
    
    void aggiungiStudente(Studenti s);
    
    Studenti cercaStudente(String matricola);
    
    void rimuoviStudente(String matricola);
    
    void modificaStudente(String matricola, String nuovoNome, String nuovoCognome, String password);
    
    int getCfu(Studenti s);
    
    double getMediaPonderata(Studenti s);
    
}
