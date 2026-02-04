package it.univaq.disim.lpo.dominiouniversitario.service.methods;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.univaq.disim.lpo.dominiouniversitario.core.Appello;
import it.univaq.disim.lpo.dominiouniversitario.core.Docenti;
import it.univaq.disim.lpo.dominiouniversitario.core.Iscrizione;
import it.univaq.disim.lpo.dominiouniversitario.core.Studenti;
import it.univaq.disim.lpo.dominiouniversitario.service.IGestioneAppelli;

import java.util.ArrayList;
import java.util.List;

public class GestioneAppelli implements IGestioneAppelli{

    private static GestioneAppelli instance;
    private static final Logger log = LoggerFactory.getLogger(GestioneAppelli.class);

    private GestioneAppelli() {}

    public static synchronized GestioneAppelli getInstance() {
        if (instance == null) {
            instance = new GestioneAppelli();
        }
        return instance;
    }

    public List<Appello> getTuttiGliAppelli() {
        log.debug("Recupero di tutti gli appelli di tutti i docenti");
        List<Appello> tuttiGliAppelli = new ArrayList<>();
        List<Docenti> docenti = GestioneDocenti.getInstance().getDocenti();
        if (docenti != null) {
            log.debug("Calcolo di tutti gli appelli disponibili");
            for (Docenti docente : docenti) {
                if (docente.getAppelli() != null) {
                    tuttiGliAppelli.addAll(docente.getAppelli());
                }
            }
        }
        log.info("Recupero di tutti gli appelli completato");
        return tuttiGliAppelli;
    }

    public void aggiungiAppello(Docenti docente, Appello appello) {
        log.debug("Aggiunta di un appello per il docente {}", docente.getNome());
        if (docente != null && appello != null) {
            docente.getAppelli().add(appello);
            log.info("Aggiunta completata, salvataggio dei dati");
            GestioneDocenti.getInstance().salvaDocenti();
        }
        else {
            log.warn("Docente o appello null, impossibile aggiungere l'appello");
        }
    }

    public void rimuoviAppello(Docenti docente, Appello appello) {
        log.debug("Rimozione di un appello per il docente {}", docente.getNome());
        if (docente != null && appello != null) {
            docente.getAppelli().remove(appello);
            log.info("Rimozione completata, salvataggio dei dati");
            GestioneDocenti.getInstance().salvaDocenti();
        } else {
            log.warn("Docente o appello null, impossibile rimuovere l'appello");
        }
    }

    public boolean prenotaStudente(Studenti studente, Appello appello) {
        log.debug("Prenotazione dello studente {} all'appello di {}", studente.getMatricola(), appello.getInsegnamento().getNome());
        if (studente == null || appello == null) {
            log.warn("Studente o appello null, impossibile effettuare la prenotazione");
            return false;
        }
        
        Studenti sGlobal = GestioneStudenti.getInstance().cercaStudente(studente.getMatricola());
        if (sGlobal != null) {
            studente = sGlobal;
        }

        if (studente.getListaEsami() != null) {
            for (it.univaq.disim.lpo.dominiouniversitario.core.EsameSostenuto esame : studente.getListaEsami()) {
                if (esame.getInsegnamento().getCodiceInsegnamento().equals(appello.getInsegnamento().getCodiceInsegnamento())) {
                    if (esame.getVoto() >= 18) {
                        log.warn("Lo studente {} ha già superato l'esame di {} con voto {}, non può iscriversi", 
                                 studente.getMatricola(), appello.getInsegnamento().getNome(), esame.getVoto());
                        return false;
                    }
                }
            }
        }
        
        for (Iscrizione iscrizione : appello.getIscrizioni()) {
            if (iscrizione.getStudente().getMatricola().equals(studente.getMatricola())) {
                log.warn("Lo studente {} è già iscritto all'appello ", studente.getMatricola());
                return false; 
            }
        }
        
        Iscrizione nuovaIscrizione = new Iscrizione(studente, appello);
        log.info("Iscrizione completata per studente {}", studente.getMatricola());
        appello.getIscrizioni().add(nuovaIscrizione);
        log.debug("Salvataggio dei dati");
        GestioneDocenti.getInstance().salvaDocenti();
        return true;
    }

    public boolean annullaPrenotazione(Studenti studente, Appello appello) {
        if (studente == null || appello == null) {
            return false;
        }
        Iscrizione iscrizioneDaRimuovere = null;
        for (Iscrizione iscrizione : appello.getIscrizioni()) {
            if (iscrizione.getStudente().getMatricola().equals(studente.getMatricola())) {
                iscrizioneDaRimuovere = iscrizione;
                break;
            }
        }
        if (iscrizioneDaRimuovere != null) {
            appello.getIscrizioni().remove(iscrizioneDaRimuovere);
            GestioneDocenti.getInstance().salvaDocenti();
            return true;
        }
        return false;
    }
    
}
