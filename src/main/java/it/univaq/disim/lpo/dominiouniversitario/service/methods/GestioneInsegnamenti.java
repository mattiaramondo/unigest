package it.univaq.disim.lpo.dominiouniversitario.service.methods;

import it.univaq.disim.lpo.dominiouniversitario.core.Insegnamenti;
import it.univaq.disim.lpo.dominiouniversitario.service.IGestioneInsegnamenti;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GestioneInsegnamenti implements IGestioneInsegnamenti{

    private static final Logger log = LoggerFactory.getLogger(GestioneInsegnamenti.class);
    private Map<String, Insegnamenti> insegnamentiMap = new LinkedHashMap<>();

    public void aggiungiInsegnamento(Insegnamenti insegnamento) {
        log.debug("Aggiunta dell'insegnamento {}", insegnamento.getNome());
        if (!insegnamentiMap.containsKey(insegnamento.getCodiceInsegnamento())) {
            insegnamentiMap.put(insegnamento.getCodiceInsegnamento(), insegnamento);
            log.info("Insegnamento aggiunto!");
        } else {
            log.warn("Insegnamento già presente.");
        }
    }

    public void rimuoviInsegnamento(String codiceInsegnamento) {
        log.debug("Rimozione dell'insegnamento {}", insegnamentiMap.get(codiceInsegnamento).getNome());
        insegnamentiMap.remove(codiceInsegnamento);
    }

    public Insegnamenti getInsegnamentoByCodice(String codiceInsegnamento) {
        log.debug("Recupero dell'insegnamento {}", insegnamentiMap.get(codiceInsegnamento).getNome());
        return insegnamentiMap.get(codiceInsegnamento);
    }

    public List<Insegnamenti> getTuttiGliInsegnamenti() {
        log.info("Recupero della lista di tutti gli insegnamenti!");
        return new ArrayList<>(insegnamentiMap.values());
    }

    public List<Insegnamenti> cercaInsegnamentiPerNome(String nome) {
        log.debug("Ricerca insegnamenti per nome: {}", nome);
        return insegnamentiMap.values().stream()
                .filter(i -> i.getNome().toLowerCase().contains(nome.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Insegnamenti> cercaInsegnamentiPerCfu(int cfu) {
        log.debug("Ricerca insegnamenti per CFU: {}", cfu);
        return insegnamentiMap.values().stream()
                .filter(i -> i.getCfu() == cfu)
                .collect(Collectors.toList());
    }
}
