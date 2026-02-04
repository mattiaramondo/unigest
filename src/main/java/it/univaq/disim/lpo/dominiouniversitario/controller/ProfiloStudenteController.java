package it.univaq.disim.lpo.dominiouniversitario.controller;

import java.net.URL;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfiloStudenteController extends HomeStudenteController {

    private static final Logger log = LoggerFactory.getLogger(ProfiloStudenteController.class);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        
        log.debug("Inizializzazione ProfiloStudenteController");
    }
}