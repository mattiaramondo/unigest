package it.univaq.disim.lpo.dominiouniversitario.service;

import it.univaq.disim.lpo.dominiouniversitario.core.AuleEdifici;

import java.io.IOException;
import java.util.List;

public interface IGestioneAule {

    List<AuleEdifici> caricaAule() throws IOException;

    void inserisciDati(List<AuleEdifici> listaAuleJson);

    void salvaAule();
    
    List<AuleEdifici> getAule();
    
    AuleEdifici getAulaByNome(String nomeAula);
    
    void aggiungiAula(AuleEdifici aula);
    
    void rimuoviAula(String nomeAula);
    
    void modificaAula(String nomeAula, int nuovaCapienza);
    
    void modificaNomeAula(String vecchioNome, String nuovoNome);
    
    List<AuleEdifici> cercaAulePerCapienza(int capienzaMinima);
    
    boolean aulaEsiste(String nomeAula);
    
}
