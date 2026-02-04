package it.univaq.disim.lpo.dominiouniversitario.service.methods;

import it.univaq.disim.lpo.dominiouniversitario.core.AuleEdifici;
import it.univaq.disim.lpo.dominiouniversitario.service.DataInitializable;
import it.univaq.disim.lpo.dominiouniversitario.service.IGestioneAule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GestioneAule implements IGestioneAule, DataInitializable {

    private static final Logger log = LoggerFactory.getLogger(GestioneAule.class);
    private static GestioneAule instance = null;
    private Map<String, AuleEdifici> auleMap = new LinkedHashMap<>();
    private static final String FILE_NAME = "aule.json";
    private final ObjectMapper objectMapper;
    private boolean datiInizializzati = false;

    private GestioneAule() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        // Non serializzare campi null per mantenere il JSON pulito
        this.objectMapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
        log.debug("Istanza GestioneAule creata (dati non ancora caricati)");
    }

    public static GestioneAule getInstance() {
        if (instance == null) {
            instance = new GestioneAule();
        }
        return instance;
    }

    @Override
    public void initializeData() throws IOException {
        if (datiInizializzati) {
            log.warn("Dati aule già inizializzati, operazione ignorata");
            return;
        }
        
        log.info("Inizializzazione dati aule...");
        List<AuleEdifici> aule = caricaAule();
        inserisciDati(aule);
        datiInizializzati = true;
        log.info("Dati aule inizializzati con successo: {} aule caricate", auleMap.size());
    }

    @Override
    public void saveData() {
        log.debug("Salvataggio dati aule tramite DataInitializable");
        salvaAule();
        log.info("Dati aule salvati con successo tramite DataInitializable");
    }

    public List<AuleEdifici> caricaAule() throws IOException {
        log.debug("Tentativo di caricamento delle aule dal file: {}", FILE_NAME);
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            log.error("Il file .json delle aule non è presente nel progetto");
            throw new IOException("ERRORE CRITICO: File " + FILE_NAME + " non trovato");
        }
        try {
            List<AuleEdifici> aule = objectMapper.readValue(file, new TypeReference<List<AuleEdifici>>() {});
            if (aule == null || aule.isEmpty()) {
                log.error("Il file {} è vuoto o corrotto", FILE_NAME);
                throw new IOException("Il file " + FILE_NAME + " è vuoto o corrotto");
            }
            log.info("Aule caricate con successo dal JSON: {}", FILE_NAME);
            return aule;
        } catch (IOException e) {
            log.error("Impossibile leggere il file {}: {}", FILE_NAME, e.getMessage(), e);
            throw new IOException("Impossibile leggere il file " + FILE_NAME + ". Errore: " + e.getMessage(), e);
        }
    }

    public void inserisciDati(List<AuleEdifici> listaAuleJson) {
        log.debug("Inserimento dei dati JSON delle aule nella mappa");
        auleMap.clear();
        for (AuleEdifici aula : listaAuleJson) {
            auleMap.put(aula.getNomeAula(), aula);
        }
        log.info("Dati delle aule inseriti nella mappa con successo");
    }

    public void salvaAule() {
        log.debug("Tentativo di salvataggio dei dati aule al file JSON");
        File file = new File(FILE_NAME);
        List<AuleEdifici> aule = getAule();
        
        if (file.exists() && file.length() > 0) {
            if (aule == null || aule.isEmpty()) {
                log.error("ATTENZIONE: Tentativo di salvare lista vuota quando il file contiene dati. Operazione annullata per sicurezza.");
                return;
            }
        }
        
        try {
            objectMapper.writeValue(file, aule);
            log.info("Aule salvate correttamente in {}: {} aule", FILE_NAME, aule.size());
        } catch (IOException e) {
            log.error("Errore durante il salvataggio di {}: {}", FILE_NAME, e.getMessage(), e);
        }
    }

    @Override
    public List<AuleEdifici> getAule() {
        log.debug("Restituzione della lista di tutte le aule");
        return new ArrayList<>(auleMap.values());
    }

    @Override
    public AuleEdifici getAulaByNome(String nomeAula) {
        log.debug("Ricerca aula con nome: {}", nomeAula);
        return auleMap.get(nomeAula);
    }

    @Override
    public void aggiungiAula(AuleEdifici aula) {
        if (aula == null) {
            log.warn("Tentativo di aggiungere un'aula null");
            return;
        }
        if (auleMap.containsKey(aula.getNomeAula())) {
            log.warn("Aula {} già esistente, impossibile aggiungere duplicato", aula.getNomeAula());
            return;
        }
        log.debug("Aggiunta della nuova aula {}", aula.getNomeAula());
        auleMap.put(aula.getNomeAula(), aula);
        log.info("Aula {} aggiunta con successo", aula.getNomeAula());
    }

    @Override
    public void rimuoviAula(String nomeAula) {
        if (!auleMap.containsKey(nomeAula)) {
            log.warn("Aula {} non trovata, impossibile rimuovere", nomeAula);
            return;
        }
        log.debug("Rimozione dell'aula {}", nomeAula);
        auleMap.remove(nomeAula);
        log.info("Aula {} rimossa con successo", nomeAula);
    }

    @Override
    public void modificaAula(String nomeAula, int nuovaCapienza) {
        AuleEdifici aula = auleMap.get(nomeAula);
        if (aula != null) {
            log.debug("Modifica aula {}: nuova capienza {}", nomeAula, nuovaCapienza);
            aula.setCapienza(nuovaCapienza);
            log.info("Aula {} modificata con successo", nomeAula);
        } else {
            log.warn("Aula {} non trovata, impossibile modificare", nomeAula);
        }
    }

    @Override
    public void modificaNomeAula(String vecchioNome, String nuovoNome) {
        AuleEdifici aula = auleMap.get(vecchioNome);
        if (aula != null) {
            if (auleMap.containsKey(nuovoNome)) {
                log.warn("Impossibile rinominare: aula {} già esistente", nuovoNome);
                return;
            }
            log.debug("Modifica nome aula da {} a {}", vecchioNome, nuovoNome);
            auleMap.remove(vecchioNome);
            aula.setNomeAula(nuovoNome);
            auleMap.put(nuovoNome, aula);
            log.info("Nome aula modificato con successo da {} a {}", vecchioNome, nuovoNome);
        } else {
            log.warn("Aula {} non trovata, impossibile modificare il nome", vecchioNome);
        }
    }

    @Override
    public List<AuleEdifici> cercaAulePerCapienza(int capienzaMinima) {
        log.debug("Ricerca aule con capienza minima: {}", capienzaMinima);
        List<AuleEdifici> risultato = new ArrayList<>();
        for (AuleEdifici aula : auleMap.values()) {
            if (aula.getCapienza() >= capienzaMinima) {
                risultato.add(aula);
            }
        }
        log.info("Trovate {} aule con capienza >= {}", risultato.size(), capienzaMinima);
        return risultato;
    }

    @Override
    public boolean aulaEsiste(String nomeAula) {
        boolean esiste = auleMap.containsKey(nomeAula);
        log.debug("Verifica esistenza aula {}: {}", nomeAula, esiste);
        return esiste;
    }
}
