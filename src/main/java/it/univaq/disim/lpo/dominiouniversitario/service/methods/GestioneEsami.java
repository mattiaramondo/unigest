package it.univaq.disim.lpo.dominiouniversitario.service.methods;

import java.time.LocalDate;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.univaq.disim.lpo.dominiouniversitario.core.Appello;
import it.univaq.disim.lpo.dominiouniversitario.core.Docenti;
import it.univaq.disim.lpo.dominiouniversitario.core.EsameSostenuto;
import it.univaq.disim.lpo.dominiouniversitario.core.Insegnamenti;
import it.univaq.disim.lpo.dominiouniversitario.core.Iscrizione;
import it.univaq.disim.lpo.dominiouniversitario.core.Studenti;
import it.univaq.disim.lpo.dominiouniversitario.core.Verbali;
import it.univaq.disim.lpo.dominiouniversitario.service.IGestioneEsami;

public class GestioneEsami implements IGestioneEsami{

    private static final Logger log = LoggerFactory.getLogger(GestioneEsami.class);
    private static GestioneEsami instance = null;
    
    public void creaEsameSostenuto(Studenti s,
                                   Insegnamenti insegnamento, 
                                   int voto, 
                                   boolean lode, 
                                   int cfu, 
                                   LocalDate data) {
        EsameSostenuto esame = new EsameSostenuto(insegnamento, voto, lode, cfu, data);
        s.getListaEsami().add(esame);
    }

    public void aggiungiEsame(String idDocente, EsameSostenuto esame) {
        log.debug("Aggiunta dell'esame sostenuto {} del docente {}", esame, idDocente);
        Docenti docente = GestioneDocenti.getInstance().getDocenteById(idDocente);
        if (docente != null && docente.getEsamiRegistrati() != null) {
            log.info("Esame sostenuto aggiunto con successo");
            docente.getEsamiRegistrati().add(esame);
        }
        else {
            log.warn("Docente {} non trovato o lista esami registrati non inizializzata", idDocente);
        }
    }

    public static GestioneEsami getInstance() {
        if (instance == null) {
            instance = new GestioneEsami();
        }
        return instance;
    }

    public EsameSostenuto registraVoto(String idDocente, Studenti studente, Appello appello, int voto, boolean lode) {
        log.debug("Tentativo di registrazione del voto {} per lo studente {} all'appello {}", voto, studente, appello);
        Docenti docente = GestioneDocenti.getInstance().getDocenteById(idDocente);
        Appello appelloReale = null;
        if (docente != null) {
            for (Appello a : docente.getAppelli()) {
                if (a.equals(appello)) {
                    appelloReale = a;
                    break;
                }
            }
        }
        boolean isIscritto = false;
        if (appelloReale != null) {
            for (Iscrizione i : appelloReale.getIscrizioni()) {
                if (i.getStudente().getMatricola().equals(studente.getMatricola())) {
                    isIscritto = true;
                    break;
                }
            }
        }
        if (docente != null && appelloReale != null && isIscritto) {
            
            Studenti sGlobal = GestioneStudenti.getInstance().cercaStudente(studente.getMatricola());
            if (sGlobal != null && sGlobal.getListaEsami() != null) {
                for (EsameSostenuto esame : sGlobal.getListaEsami()) {
                    if (esame.getInsegnamento().getCodiceInsegnamento().equals(appello.getInsegnamento().getCodiceInsegnamento()) && esame.getVoto() >= 18) {
                         log.warn("Lo studente (da record globale) ha già sostenuto questo esame con voto {}", esame.getVoto());
                         return null;
                    }
                }
            }

            if (studente.getListaEsami() != null) {
                for (EsameSostenuto esame : studente.getListaEsami()) {
                    if (esame.getInsegnamento().getCodiceInsegnamento().equals(appello.getInsegnamento().getCodiceInsegnamento()) && esame.getVoto() >= 18) {
                        log.warn("Lo studente ha già sostenuto questo esame con voto {}", esame.getVoto());
                        return null;
                    }
                }
            }
            log.debug("verifica duplicati del verbale per l'appello");
            Verbali verbale = null;
            if (docente.getVerbali() == null) {
                docente.setVerbali(new ArrayList<>());
            }
            for (Verbali v : docente.getVerbali()) {
                if (v.getInsegnamento().equals(appelloReale.getInsegnamento()) && v.getData().equals(appelloReale.getDataEsame())) {
                    verbale = v;
                    break;
                }
            }
            log.debug("Creazione di un nuovo verbale");
            if (verbale == null) {
                verbale = new Verbali(appelloReale.getInsegnamento(), new ArrayList<>(), appelloReale.getDataEsame());
                docente.getVerbali().add(verbale);
            }
            log.debug("Creazione e aggiunta dell'esame sostenuto alla lista degli esami sostenuti dallo studente");
            EsameSostenuto nuovoEsame = new EsameSostenuto(
                appelloReale.getInsegnamento(), 
                voto, 
                lode, 
                appelloReale.getInsegnamento().getCfu(), 
                appelloReale.getDataEsame()
            );
            if (voto > 0) {
                verbale.getEsami().add(nuovoEsame);
                GestioneEsami.getInstance().aggiungiEsame(idDocente, nuovoEsame);
                log.info("Aggiunta esame completato");
            } else {
                log.info("Esito 'Non Superato': non aggiunto al verbale ufficiale, ma notificato allo studente");
            }
            if (studente.getListaEsami() == null) {
                studente.setListaEsami(new ArrayList<>());
            }
            studente.getListaEsami().add(nuovoEsame);

            Studenti studenteInGestione = GestioneStudenti.getInstance().cercaStudente(studente.getMatricola());
            log.debug("Aggiornamento istanza dello studente");
            if (studenteInGestione != null) {
                if (studenteInGestione.getListaEsami() == null) {
                    studenteInGestione.setListaEsami(new ArrayList<>());
                }
                studenteInGestione.getListaEsami().add(nuovoEsame);
            }
            log.info("Salvatagggio delle modifiche");
            GestioneDocenti.getInstance().salvaDocenti();
            GestioneStudenti.getInstance().salvaStudenti();
            log.info("Voto registrato con successo per lo studente {}", studente.getMatricola());
            return nuovoEsame;
        } else {
            log.error("Errore nella registrazione del voto: controlli falliti.");
            if (docente == null) log.error("Docente non trovato (id: {})", idDocente);
            else if (!docente.getAppelli().contains(appello)) {
                log.error("Appello non trovato nella lista del docente.");
                log.error("  Appello passato: {} del {}", appello.getInsegnamento().getNome(), appello.getDataEsame());
                log.error("  Appelli del docente:");
                for (Appello a : docente.getAppelli()) {
                    System.out.println("    * " + a.getInsegnamento().getNome() + " del " + a.getDataEsame() + (a.equals(appello) ? " [MATCH]" : " [NO MATCH]"));
                }
            }
            else if (!isIscritto) {
                log.error("Studente non iscritto all'appello.");
                log.error("  Studente: {}", studente.getMatricola());
                log.error("  Iscritti:");
                if (appello.getIscrizioni() != null) {
                    for (Iscrizione i : appello.getIscrizioni()) {
                        log.error("    * {}", i.getStudente().getMatricola());
                    }
                } else {
                    log.error("    * Nessun iscritto (lista null)");
                }
            }
        }
        log.warn("Lo studente non è presente nella lista degli iscritti all'appello");
        return null;
    }
        
}
