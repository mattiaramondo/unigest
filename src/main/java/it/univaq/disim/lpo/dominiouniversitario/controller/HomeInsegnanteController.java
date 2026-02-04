package it.univaq.disim.lpo.dominiouniversitario.controller;

import it.univaq.disim.lpo.dominiouniversitario.core.Docenti;
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneDocenti;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeInsegnanteController {

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
    @FXML
    private javafx.scene.control.ComboBox<it.univaq.disim.lpo.dominiouniversitario.core.CorsiLaurea> corsoFilterComboBox;

    @FXML
    private javafx.scene.control.Button profileButton;

    private static final Logger log = LoggerFactory.getLogger(HomeInsegnanteController.class);

    @FXML
    public void initialize() {
        log.debug("Inizializzazione HomeInsegnanteController");
        if (profileButton != null) {
            profileButton.setDisable(true);
            profileButton.getStyleClass().add("vbox-button-active");
        }

        Docenti docenteLoggato = GestioneDocenti.getInstance().getProfiloDocente();
        if (docenteLoggato != null) {
            log.debug("Docente loggato: {} {}", docenteLoggato.getNome(), docenteLoggato.getCognome());
            labelNome.setText(docenteLoggato.getNome());
            labelCognome.setText(docenteLoggato.getCognome());
            labelEmail.setText(docenteLoggato.getEmail());
            labelIdDocente.setText(docenteLoggato.getIdDocente());
            labelRuolo.setText(docenteLoggato.getRuolo().toString());
            labelBenvenuto.setText("Benvenuto, " + docenteLoggato.getNome() + " " + docenteLoggato.getCognome());

            java.util.List<it.univaq.disim.lpo.dominiouniversitario.core.CorsiLaurea> corsi = docenteLoggato.getInsegnamenti().stream()
                .map(it.univaq.disim.lpo.dominiouniversitario.core.Insegnamenti::getCorsoDiLaurea)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
            
            corsoFilterComboBox.setItems(javafx.collections.FXCollections.observableArrayList(corsi));
            
            it.univaq.disim.lpo.dominiouniversitario.core.CorsiLaurea currentSelection = GestioneDocenti.getInstance().getCorsoSelezionato();
            if (currentSelection != null && corsi.contains(currentSelection)) {
                corsoFilterComboBox.setValue(currentSelection);
            }

            corsoFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                GestioneDocenti.getInstance().setCorsoSelezionato(newVal);
                log.info("Filtro corso impostato a: {}", newVal);
            });

        } else {
            log.error("ERRORE: Profilo docente non trovato in GestioneDocenti!");
            labelBenvenuto.setText("Errore: Sessione non trovata");
        }
    }

    @FXML
    private void handleGestioneAppelli(ActionEvent event) {
        log.debug("Navigazione a GestioneAppelli");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GestioneAppelli.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            log.info("Scena GestioneAppelli caricata correttamente");
        } catch (IOException e) {
            log.error("Errore durante la navigazione a GestioneAppelli", e);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegistrazioneVoti(ActionEvent event) {
        log.debug("Navigazione a RegistrazioneVoti");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RegistrazioneVoti.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            log.info("Scena RegistrazioneVoti caricata correttamente");
        } catch (IOException e) {
            log.error("Errore durante la navigazione a RegistrazioneVoti", e);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleInsegnamentiAttivi(ActionEvent event) {
        log.debug("Navigazione a InsegnamentiAttivi");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InsegnamentiAttivi.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            log.info("Scena InsegnamentiAttivi caricata correttamente");
        } catch (IOException e) {
            log.error("Errore durante la navigazione a InsegnamentiAttivi", e);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        log.debug("Logout e navigazione a NewLoginPage");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NewLoginPage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            log.info("Scena NewLoginPage caricata correttamente");
        } catch (IOException e) {
            log.error("Errore durante la navigazione a NewLoginPage", e);
            e.printStackTrace();
        }
    }
}
