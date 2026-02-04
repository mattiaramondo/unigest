package it.univaq.disim.lpo.dominiouniversitario.service.methods;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import it.univaq.disim.lpo.dominiouniversitario.core.CorsiLaurea;
import it.univaq.disim.lpo.dominiouniversitario.core.Insegnamenti;
import it.univaq.disim.lpo.dominiouniversitario.core.Studenti;
import it.univaq.disim.lpo.dominiouniversitario.service.IGestioneCorsiLaurea;

public class GestioneCorsiLaurea implements IGestioneCorsiLaurea{

    private Map<String, CorsiLaurea> corsiMap = new LinkedHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(GestioneCorsiLaurea.class);

    public void aggiungiCorso(CorsiLaurea corso) {
        log.debug("Aggiunta del corso di laurea {}", corso.getNome());
        corsiMap.put(corso.getCodice(), corso);
    }

    public void rimuoviCorso(String codiceCorso) {
        log.debug("Rimozione del corso di laurea {}", corsiMap.get(codiceCorso).getNome());
        corsiMap.remove(codiceCorso);
    }
    
    public List<String> getNomiCorsiDisponibili() {
        log.debug("Restituzione dei corsi disponibili");
        List<String> nomi = new ArrayList<>();
        for (CorsiLaurea corso : corsiMap.values()) {
            nomi.add(corso.getNome());
        }
        return nomi;
    }
    
    public CorsiLaurea getCorsoByNome(String nome) {
        log.debug("Ricerca del corso di laurea per nome: {}", nome);
        for (CorsiLaurea corso : corsiMap.values()) {
            if (corso.getNome().equals(nome)) {
                return corso;
            }
        }
        return null;
    }

    public void aggiungiInsegnamento(String codiceCorso, Insegnamenti insegnamento) {
        log.debug("Aggiunta dell'insegnamento {} al corso di laurea {}", insegnamento.getNome(), codiceCorso);
        CorsiLaurea corso = corsiMap.get(codiceCorso);
        if (corso != null) {
            log.info("Aggiunta in corso");
            corso.getInsegnamenti().add(insegnamento);
            log.info("Insegnamento {} aggiunto con successo al corso {}", insegnamento.getNome(), corso.getNome());
        }
        else {
            log.warn("Corso di laurea non trovato");
        }
    }

    public void rimuoviInsegnamento(String codiceCorso, Insegnamenti insegnamento) {
        log.debug("Rimozione dell'insegnamento {} dal corso di laurea {}", insegnamento.getNome(), codiceCorso);
        CorsiLaurea corso = corsiMap.get(codiceCorso);
        if (corso != null) {
            log.info("Rimozione in corso");
            corso.getInsegnamenti().remove(insegnamento);
            log.info("Insegnamento {} rimosso con successo dal corso {}", insegnamento.getNome(), corso.getNome());
        }
        else {
            log.warn("Corso di laurea non trovato");
        }
    }

    public void iscriviStudente(String codiceCorso, Studenti studente) {
        log.debug("Iscrizione dello studente {} al corso di laurea {}", studente.getMatricola(), codiceCorso);
        CorsiLaurea corso = corsiMap.get(codiceCorso);
        if (corso != null && !corso.getStudentiIscritti().contains(studente)) {
            log.info("Iscrizione in corso");
            corso.getStudentiIscritti().add(studente);
        }
        else {
            log.warn("Corso di laurea non trovato o studente già iscritto");
        }
    }

    public void rimuoviStudente(String codiceCorso, Studenti studente) {
        log.debug("Rimozione dello studente {} dal corso di laurea {}", studente.getMatricola(), codiceCorso);
        CorsiLaurea corso = corsiMap.get(codiceCorso);
        if (corso != null) {
            log.info("Rimozione in corso");
            corso.getStudentiIscritti().remove(studente);
        }
        else {
            log.warn("Corso di laurea non trovato");
        }
    }

    public int calcolaCfuTotali(String codiceCorso) {
        log.debug("Calcolo dei CFU totali per il corso di laurea {}", codiceCorso);
        CorsiLaurea corso = corsiMap.get(codiceCorso);
        if (corso != null) {
            int cfuTotali = 0;
            for (Insegnamenti insegnamento : corso.getInsegnamenti()) {
                cfuTotali += insegnamento.getCfu();
            }
            log.info("Calcolo completato: {} CFU", cfuTotali);
            return cfuTotali;
        }
        log.warn("Corso di laurea non trovato");
        return 0;
    }
    
    public void stampaNomiCorsi() {
        log.debug("Stampa dei nomi dei corsi di laurea disponibili");
        for (CorsiLaurea corso : corsiMap.values()) {
            System.out.println("- " + corso.getNome() + " (" + corso.getCodice() + ")");
        }
    }
}
