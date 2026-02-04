package it.univaq.disim.lpo.dominiouniversitario.service.methods;

import it.univaq.disim.lpo.dominiouniversitario.core.Studenti;
import it.univaq.disim.lpo.dominiouniversitario.service.DataInitializable;
import it.univaq.disim.lpo.dominiouniversitario.service.IGestioneStudenti;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GestioneStudenti implements IGestioneStudenti, DataInitializable {

    private static final Logger log = LoggerFactory.getLogger(GestioneStudenti.class);
    private Map<String, Studenti> studentiMap;
    private static GestioneStudenti instance = null;
    private static final String FILE_NAME = "studenti.json";
    private final ObjectMapper objectMapper;
    private Studenti profiloStudente;
    private boolean datiInizializzati = false;

    public GestioneStudenti() {
        this.studentiMap = new java.util.HashMap<>();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        // Non serializzare campi null per mantenere il JSON pulito
        this.objectMapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
        log.debug("Istanza GestioneStudenti creata (dati non ancora caricati)");
    }

    @Override
    public void initializeData() throws IOException {
        if (datiInizializzati) {
            log.warn("Dati studenti già inizializzati, operazione ignorata");
            return;
        }
        
        log.info("Inizializzazione dati studenti...");
        List<Studenti> studenti = caricaStudenti();
        inserisciDati(studenti);
        datiInizializzati = true;
        log.info("Dati studenti inizializzati con successo: {} studenti caricati", studentiMap.size());
    }

    public List<Studenti> caricaStudenti() throws IOException {
        log.info("Tentativo di caricamento degli studenti...");
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            log.error("Il file .json degli studenti non è presente nel progetto.");
            throw new IOException("Il file .json non è presente nel progetto.");
        }
        try {
            List<Studenti> studenti = objectMapper.readValue(file, new TypeReference<List<Studenti>>() {});
            if (studenti == null || studenti.isEmpty()) {
                log.warn("Il file {} è vuoto o corrotto.", FILE_NAME);
                throw new IOException("Il file " + FILE_NAME + " è vuoto o corrotto.");
            }
            log.debug("Studenti caricati con successo dal JSON: {}", FILE_NAME);
            return studenti;
        } catch (IOException e) {
            log.error("Impossibile leggere il file {}: {}", FILE_NAME, e.getMessage(), e);
            throw new IOException("Impossibile leggere il file " + FILE_NAME + ". Errore: " + e.getMessage(), e);
        }
    }

    public void salvaStudenti(List<Studenti> datiDaSalvare) {
        File file = new File(FILE_NAME);
        if (file.exists() && file.length() > 0) {
            if (datiDaSalvare == null || datiDaSalvare.isEmpty()) {
                log.error("Errore salvataggio dati: impossibile salvare i dati.");
                return;
            }
        }
        try {
            objectMapper.writeValue(file, datiDaSalvare);
            log.info("Dati salvati nel file JSON!");
        } catch (IOException e) {
            log.error("Errore durante il salvataggio dei dati: {}", e.getMessage(), e);
        }
    }

    @Override
    public void saveData() {
        log.debug("Salvataggio dati studenti tramite DataInitializable");
        salvaStudenti(getStudentiJson());
        log.info("Dati studenti salvati con successo");
    }

    public void salvaStudenti() {
        salvaStudenti(getStudentiJson());
    }

    public Studenti getProfiloStudente() { return profiloStudente; }
    public void setProfiloStudente(Studenti profiloStudente) { 
        log.info("Nuovo profilo studente impostato: {}", profiloStudente.getMatricola());
        this.profiloStudente = profiloStudente; 
    }

    private Studenti studenteDaModificare = null;
    
    public Studenti getStudenteDaModificare() { return studenteDaModificare; }
    public void setStudenteDaModificare(Studenti studente) { 
        log.info("Studente da modificare impostato: {}", studente != null ? studente.getMatricola() : "null");
        this.studenteDaModificare = studente; 
    }
    public void clearStudenteDaModificare() { 
        this.studenteDaModificare = null; 
    }

    public static GestioneStudenti getInstance() {
        if (instance == null) {
            instance = new GestioneStudenti();
        }
        return instance;
    }

    public Studenti verificaLogin(String email, String password) {
        log.debug("Credenziali ricevute: email={}, password={}", email, password);
        for (Studenti s : studentiMap.values()) {
            if (s.getEmail().equals(email) && s.getPassword().equals(password)) {
                log.info("Login studente avvenuto con successo!");
                return s;
            }
        }
        log.warn("Login studente fallito: credenziali non valide.");
        return null;
    }

    public void inserisciDati(List<Studenti> listaStudentiJson) {
        studentiMap.clear();
        for (Studenti s : listaStudentiJson) { 
            studentiMap.put(s.getMatricola(), s); 
        }
        log.info("Dati degli studenti inseriti nella mappa con successo!");
    }

    public List<Studenti> getStudentiJson() { 
        log.info("Recupero lista studenti per serializzazione JSON.");
        return new ArrayList<>(studentiMap.values());
    }

    public Map<String, Studenti> getStudentiMap() { 
        log.info("Recupero mappa studenti.");
        return studentiMap; 
    }

    public void aggiungiStudente(Studenti s) { 
        log.debug("Aggiunta nuovo studente con matricola: {}", s.getMatricola());
        studentiMap.put(s.getMatricola(), s); 
    }

    public Studenti cercaStudente(String matricola) {
        log.debug("Ricerca studente con matricola: {}", matricola);
        return studentiMap.get(matricola); 
    }

    public void rimuoviStudente(String matricola) { 
        log.debug("Rimozione studente con matricola: {}", matricola);
        studentiMap.remove(matricola); 
    }

    public void modificaStudente(String matricola, String nuovoNome, String nuovoCognome, String password) {
        Studenti s = studentiMap.get(matricola);
        if (s != null) {
            log.debug("Modifica studente {}:", matricola);
            s.setNome(nuovoNome);
            s.setCognome(nuovoCognome);
            s.setPassword(password);
            log.info("Dati studente modificati con successo!");
            return;
        }
        log.warn("Studente non trovato.");
    }

    public int getCfu(Studenti s) {
        int totaleCfu = 0;
        for (it.univaq.disim.lpo.dominiouniversitario.core.EsameSostenuto esame : s.getListaEsami()) {
            log.debug("Calcolo CFU conseguiti dallo studente {}.", s.getMatricola());
            // Considera solo gli esami superati (voto > 0)
            if (esame.getVoto() > 0) {
                totaleCfu += esame.getCfu();
            }
        }
        log.info("CFU totali calcolati con successo!");
        return totaleCfu;
    }

    public double getMediaPonderata(Studenti s) {
        log.debug("Calcolo della media ponderata dello studente {}.", s.getMatricola());
        if (s.getListaEsami().isEmpty()) {
            log.info("Nessun esame conseguito, media ponderata nulla.");
            return 0.0;
        }
        double sommaPonderata = 0;
        int totaleCfu = 0;
        for (it.univaq.disim.lpo.dominiouniversitario.core.EsameSostenuto esame : s.getListaEsami()) {
            // Considera solo gli esami superati (voto > 0)
            if (esame.getVoto() > 0) {
                sommaPonderata += esame.getVoto() * esame.getCfu();
                totaleCfu += esame.getCfu();
            }
        }
        if (totaleCfu == 0) return 0.0;
        log.info("Media ponderata calcolata con successo!");
        return sommaPonderata / totaleCfu;
    }

}