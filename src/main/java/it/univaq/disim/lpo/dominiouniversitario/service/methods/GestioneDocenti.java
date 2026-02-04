package it.univaq.disim.lpo.dominiouniversitario.service.methods;

import it.univaq.disim.lpo.dominiouniversitario.core.Appello;
import it.univaq.disim.lpo.dominiouniversitario.core.CorsiLaurea;
import it.univaq.disim.lpo.dominiouniversitario.core.Docenti;
import it.univaq.disim.lpo.dominiouniversitario.core.EsameSostenuto;
import it.univaq.disim.lpo.dominiouniversitario.core.Insegnamenti;
import it.univaq.disim.lpo.dominiouniversitario.core.Iscrizione;
import it.univaq.disim.lpo.dominiouniversitario.core.Studenti;
import it.univaq.disim.lpo.dominiouniversitario.service.DataInitializable;
import it.univaq.disim.lpo.dominiouniversitario.service.IGestioneDocenti;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GestioneDocenti implements IGestioneDocenti, DataInitializable {

    private static final Logger log = LoggerFactory.getLogger(GestioneDocenti.class);
    private static GestioneDocenti instance = null;
    private Map<String, Docenti> docentiMap = new LinkedHashMap<>();
    private Docenti profiloDocente;
    private static final String FILE_NAME = "docenti.json";
    private final ObjectMapper objectMapper;
    private CorsiLaurea corsoSelezionato;
    private boolean datiInizializzati = false;
    private Docenti docenteDaModificare = null;

    private GestioneDocenti() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
        log.debug("Istanza GestioneDocenti creata (dati non ancora caricati)");
    }

    public static GestioneDocenti getInstance() {
        if (instance == null) {
            instance = new GestioneDocenti();
        }
        return instance;
    }

    @Override
    public void initializeData() throws IOException {
        if (datiInizializzati) {
            log.warn("Dati docenti già inizializzati, operazione ignorata");
            return;
        }
        
        log.info("Inizializzazione dati docenti...");
        List<Docenti> docenti = caricaDocenti();
        inserisciDati(docenti);
        datiInizializzati = true;
        log.info("Dati docenti inizializzati con successo: {} docenti caricati", docentiMap.size());
    }

    public List<Docenti> caricaDocenti() throws IOException {
        log.debug("Tentativo di caricamento dei docenti...");
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            log.error("Il file .json dei docenti non è presente nel progetto");
            throw new IOException("ERRORE CRITICO: File " + FILE_NAME + " non trovato");
        }
        try {
            List<Docenti> docenti = objectMapper.readValue(file, new TypeReference<List<Docenti>>() {});
            if (docenti == null || docenti.isEmpty()) {
                log.error("Il file {} è vuoto o corrotto", FILE_NAME);
                throw new IOException("Il file " + FILE_NAME + " è vuoto o corrotto");
            }
            log.info("Docenti caricati con successo dal JSON: {}", FILE_NAME);
            return docenti;
        } catch (IOException e) {
            log.error("Impossibile leggere il file {}: {}", FILE_NAME, e.getMessage(), e);
            throw new IOException("Impossibile leggere il file " + FILE_NAME + ". Errore: " + e.getMessage(), e);
        }
    }

    public void inserisciDati(List<Docenti> listaDocentiJson) {
        log.debug("Inserimento dei dati JSON dei docenti nella mappa dei docenti");
        docentiMap.clear();
        for (Docenti d : listaDocentiJson) { 
            docentiMap.put(d.getIdDocente(), d); 
        }
        log.info("Dati dei docenti inseriti nella mappa con successo");
    }

    public Docenti getProfiloDocente() {
        log.debug("Restituzione profilo docente {}", profiloDocente.getIdDocente());
        return profiloDocente;
    }

    public void setProfiloDocente(Docenti profiloDocente) {
        log.debug("Profilo docente attualmente loggato: {}", profiloDocente.getIdDocente());
        this.profiloDocente = profiloDocente;
        this.corsoSelezionato = null;
    }
    
    public Docenti getDocenteDaModificare() { return docenteDaModificare; }
    public void setDocenteDaModificare(Docenti docente) { 
        log.info("Docente da modificare impostato: {}", docente != null ? docente.getIdDocente() : "null");
        this.docenteDaModificare = docente; 
    }
    public void clearDocenteDaModificare() { 
        this.docenteDaModificare = null; 
    }

    public CorsiLaurea getCorsoSelezionato() {
        return corsoSelezionato;
    }
    public void setCorsoSelezionato(CorsiLaurea corsoSelezionato) {
        this.corsoSelezionato = corsoSelezionato;
    }

    public Docenti verificaLogin(String email, String password) {
        log.debug("Verifica delle credenziali per email: {}", email);
        for (Docenti d : docentiMap.values()) {
            if (d.getEmail().equals(email) && d.getPassword().equals(password)) {
                log.info("Login docente riuscito");
                return d;
            }
        }
        log.warn("Login docente fallito");
        return null;
    }

    public void aggiungiDocente(Docenti docente) {
        log.debug("Aggiunta del nuovo docente {}", docente.getIdDocente());
        docentiMap.put(docente.getIdDocente(), docente);
    }

    public void rimuoviDocente(String idDocente) {
        log.debug("Rimozione del docente {}", idDocente);
        docentiMap.remove(idDocente);
    }

    public Docenti getDocenteById(String idDocente) {
        log.debug("Restituzione del docente {}", idDocente);
        return docentiMap.get(idDocente);
    }

    public List<Docenti> getDocenti() {
        log.debug("Restituzione della lista di tutti i docenti");
        return new ArrayList<>(docentiMap.values());
    }

    public void assegnaInsegnamento(String idDocente, Insegnamenti insegnamento) {
        log.debug("Tentativo di assegnamento dell'insegnamento {} al docente {}", insegnamento, idDocente);
        Docenti docente = getDocenteById(idDocente);
        if (docente != null) {
            log.info("Assegnazione dell'insegnamento {} al docente {}", insegnamento.getCodiceInsegnamento(), idDocente);
            docente.aggiungiInsegnamento(insegnamento);
        }
        else {
            log.warn("Docente {} non trovato", idDocente);
        }
    }

    public void rimuoviInsegnamento(String idDocente, Insegnamenti insegnamento) {
        log.debug("Tentativo di rimozione dell'insegnamento {} dal docente {}", insegnamento, idDocente);
        Docenti docente = getDocenteById(idDocente);
        if (docente != null) {
            log.info("Rimozione dell'insegnamento {} dal docente {}", insegnamento.getCodiceInsegnamento(), idDocente);
            docente.rimuoviInsegnamento(insegnamento);
        }
        else {
            log.warn("Docente {} non trovato", idDocente);
        }
    }

    public Appello creaAppello(String idDocente, Insegnamenti insegnamento, LocalDate dataChiusuraIscrizione) {
        log.debug("Tentativo di creazione di un nuovo appello per l'insegnamento {} da parte del docente {}", insegnamento, idDocente);
        Docenti docente = getDocenteById(idDocente);
        if (docente != null && docente.getInsegnamenti().contains(insegnamento)) {
            Appello nuovoAppello = new Appello(insegnamento, dataChiusuraIscrizione.plusDays(7), LocalDate.now(), dataChiusuraIscrizione, 50, "Aula da definire");
            docente.getAppelli().add(nuovoAppello);
            log.info("Nuovo appello creato con successo");
            return nuovoAppello;
        }
        log.warn("Creazione appello fallita: docente non trovato o insegnamento non assegnato");
        return null;
    }

    public List<Studenti> visualizzaIscritti(String idDocente, Appello appello) {
        log.debug("Tentativo di visualizzazione degli studenti iscritti all'appello {} da parte del docente {}", appello, idDocente);
        Docenti docente = getDocenteById(idDocente);
        if (docente != null && docente.getAppelli().contains(appello)) {
            List<Studenti> studenti = new ArrayList<>();
            for (Iscrizione iscrizione : appello.getIscrizioni()) {
                Studenti studente = iscrizione.getStudente();
                boolean haSuperato = false;
                boolean giaVerbalizzatoQuestoAppello = false;
                
                for (EsameSostenuto es : studente.getListaEsami()) {
                    boolean stessoCorso = es.getInsegnamento().getCodiceInsegnamento().equals(appello.getInsegnamento().getCodiceInsegnamento());
                    
                    if (stessoCorso) {
                        if (es.getVoto() >= 18) {
                            haSuperato = true;
                            break;
                        }
                        if (es.getData().equals(appello.getDataEsame())) {
                            giaVerbalizzatoQuestoAppello = true;
                        }
                    }
                }
                
                if (!haSuperato && !giaVerbalizzatoQuestoAppello) {
                    studenti.add(studente);
                }
            }
            log.info("Visualizzazione degli studenti iscritti all'appello completata con successo: {} studenti", studenti.size());
            return studenti;
        }
        log.warn("Errore nella visualizzazione degli iscritti all'appello, verrà restituita una lista vuota");
        return new ArrayList<>();
    }

    @Override
    public void saveData() {
        log.debug("Salvataggio dati docenti tramite DataInitializable");
        salvaDocenti();
        log.info("Dati docenti salvati con successo tramite DataInitializable");
    }

    public void salvaDocenti() {
        log.debug("Tentativo di salvataggio dei dati docenti al file JSON");
        File file = new File(FILE_NAME);
        List<Docenti> docenti = getDocenti();
        
        if (file.exists() && file.length() > 0) {
            if (docenti == null || docenti.isEmpty()) {
                log.error("Errore salvataggio dati: impossibile salvare i dati.");
                return;
            }
        }
        try {
            objectMapper.writeValue(file, docenti);
            log.info("Docenti salvati correttamente in {}", FILE_NAME);
        } catch (IOException e) {
            log.error("Errore durante il salvataggio di {}: {}", FILE_NAME, e.getMessage());
        }
    }
}
