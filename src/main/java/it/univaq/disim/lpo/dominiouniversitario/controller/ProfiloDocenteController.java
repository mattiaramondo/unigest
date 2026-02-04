package it.univaq.disim.lpo.dominiouniversitario.controller;

import it.univaq.disim.lpo.dominiouniversitario.core.Docenti;
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneDocenti;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfiloDocenteController extends HomeInsegnanteController {

    private static final Logger log = LoggerFactory.getLogger(ProfiloDocenteController.class);

    @FXML
    private Label labelNome;
    @FXML
    private Label labelCognome;
    @FXML
    private Label labelEmail;
    @FXML
    private Label labelIdDocente;
    @FXML
    private Label labelRuolo;
    @FXML
    private Label labelBenvenuto;

    @Override
    public void initialize() {
        super.initialize();
        
        log.debug("Inizializzazione ProfiloDocenteController");
        
        Docenti docente = GestioneDocenti.getInstance().getProfiloDocente();
        if (docente != null) {
            popolaDatiDocente(docente);
        } else {
            log.error("Nessun docente loggato trovato!");
        }
    }

    private void popolaDatiDocente(Docenti d) {
        if (labelNome != null) labelNome.setText(d.getNome());
        if (labelCognome != null) labelCognome.setText(d.getCognome());
        if (labelEmail != null) labelEmail.setText(d.getEmail());
        if (labelIdDocente != null) labelIdDocente.setText(d.getIdDocente());
        if (labelRuolo != null) labelRuolo.setText(d.getRuolo().toString());
        if (labelBenvenuto != null) labelBenvenuto.setText("Benvenuto, " + d.getNome() + " " + d.getCognome());
    }
}
