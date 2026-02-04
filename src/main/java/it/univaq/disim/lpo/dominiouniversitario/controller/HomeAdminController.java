package it.univaq.disim.lpo.dominiouniversitario.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeAdminController {

    private static final Logger log = LoggerFactory.getLogger(HomeAdminController.class);

    @FXML
    private Button profiloAdminButton;
    @FXML
    private Button modificaStudenteButton;
    @FXML
    private Button modificaDocenteButton;
    @FXML
    private Button altroButton;

    @FXML
    public void initialize() {
        log.debug("HomeAdminController inizializzato");
    }

    @FXML
    public void onProfiloAdmin(ActionEvent event) {
        log.info("Click Profilo Admin");
        mostraAlert("Funzionalità non implementata", "Profilo admin non è ancora implementato.");
    }

    @FXML
    public void onModificaStudente(ActionEvent event) {
        log.info("Click Modifica Studente");
        try {
            cambiaScena(event, "RicercaStudente.fxml");
        } catch (IOException e) {
            log.error("Errore Tecnico", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile aprire la schermata di modifica studente: " + e.getMessage());
        }
    }

    @FXML
    public void onModificaDocente(ActionEvent event) {
        log.info("Click Modifica Docente");
        try {
            cambiaScena(event, "RicercaDocente.fxml");
        } catch (IOException e) {
            log.error("Errore Tecnico", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile aprire la schermata di modifica docente: " + e.getMessage());
        }
    }

    @FXML
    public void onRicercaStudente(ActionEvent event) {
        log.info("Click Ricerca Studente");
        try {
            cambiaScena(event, "RicercaStudente.fxml");
        } catch (IOException e) {
            log.error("Errore Tecnico", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile aprire la schermata di ricerca studente: " + e.getMessage());
        }
    }

    @FXML
    public void onRicercaDocente(ActionEvent event) {
        log.info("Click Ricerca Docente");
        try {
            cambiaScena(event, "RicercaDocente.fxml");
        } catch (IOException e) {
            log.error("Errore Tecnico", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile aprire la schermata di ricerca docente: " + e.getMessage());
        }
    }

    @FXML
    public void onRicercaAule(ActionEvent event) {
        log.info("Click Ricerca Aule");
        try {
            cambiaScena(event, "RicercaAule.fxml");
        } catch (IOException e) {
            log.error("Errore Tecnico", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile aprire la schermata di ricerca aule: " + e.getMessage());
        }
    }

    @FXML
    public void onLogout(ActionEvent event) {
        log.info("Click Logout");
        try {
            cambiaScena(event, "NewLoginPage.fxml");
        } catch (IOException e) {
            log.error("Errore logout", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile tornare al login: " + e.getMessage());
        }
    }

    @FXML
    public void onAltro(ActionEvent event) {
        log.info("Click Altro");
        mostraAlert("Funzionalità non implementata", "Questa sezione non è ancora disponibile.");
    }

    private void cambiaScena(ActionEvent event, String fxmlFileName) throws IOException {
        log.debug("Cambio scena verso {}", fxmlFileName);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFileName));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
        log.info("Scena {} caricata correttamente", fxmlFileName);
    }

    private void mostraAlert(String title, String content) {
        log.warn("Visualizzazione alert: {} - {}", title, content);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
