package it.univaq.disim.lpo.dominiouniversitario.controller;

import it.univaq.disim.lpo.dominiouniversitario.core.Docenti;
import it.univaq.disim.lpo.dominiouniversitario.core.Insegnamenti;
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneDocenti;
import java.io.IOException;
import java.util.List;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsegnamentiAttiviController {

    @FXML
    private Button insegnamentiButton;

    @FXML
    private TableView<Insegnamenti> insegnamentiTable;

    @FXML
    private TableColumn<Insegnamenti, String> colonnaCodice;

    @FXML
    private TableColumn<Insegnamenti, String> colonnaNome;

    @FXML
    private TableColumn<Insegnamenti, Number> colonnaCfu;

    @FXML
    private TableColumn<Insegnamenti, String> colonnaCorsoLaurea;

    private Docenti docenteLoggato;

    private static final Logger log = LoggerFactory.getLogger(InsegnamentiAttiviController.class);

    @FXML
    public void initialize() {
        log.debug("Inizializzazione InsegnamentiAttiviController");
        if (insegnamentiButton != null) {
            insegnamentiButton.setDisable(true);
            insegnamentiButton.getStyleClass().add("vbox-button-active");
        }

        docenteLoggato = GestioneDocenti.getInstance().getProfiloDocente();
        if (docenteLoggato == null) {
            log.error("Errore: Nessun docente loggato.");
            return;
        }

        colonnaCodice.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCodiceInsegnamento()));
        colonnaNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        colonnaCfu.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCfu()));
        colonnaCorsoLaurea.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCorsoDiLaurea() != null) {
                return new SimpleStringProperty(cellData.getValue().getCorsoDiLaurea().getNome());
            } else {
                return new SimpleStringProperty("N/A");
            }
        });

        caricaInsegnamenti();
    }

    private void caricaInsegnamenti() {
        log.debug("Caricamento insegnamenti per il docente: {}", docenteLoggato.getNome());
        List<Insegnamenti> insegnamenti = docenteLoggato.getInsegnamenti();
        
        it.univaq.disim.lpo.dominiouniversitario.core.CorsiLaurea filtro = GestioneDocenti.getInstance().getCorsoSelezionato();
        if (filtro != null) {
            insegnamenti = insegnamenti.stream()
                .filter(i -> i.getCorsoDiLaurea() != null && i.getCorsoDiLaurea().equals(filtro))
                .collect(java.util.stream.Collectors.toList());
        }

        ObservableList<Insegnamenti> insegnamentiObservable = FXCollections.observableArrayList(insegnamenti);
        insegnamentiTable.setItems(insegnamentiObservable);
        log.info("Insegnamenti caricati: {}", insegnamenti.size());
    }

    @FXML
    private void handleProfiloDocente(ActionEvent event) {
        log.debug("Navigazione a HomeInsegnante");
        navigateTo("/fxml/HomeInsegnante.fxml", event);
    }

    @FXML
    private void handleGestioneAppelli(ActionEvent event) {
        log.debug("Navigazione a GestioneAppelli");
        navigateTo("/fxml/GestioneAppelli.fxml", event);
    }

    @FXML
    private void handleRegistrazioneVoti(ActionEvent event) {
        log.debug("Navigazione a RegistrazioneVoti");
        navigateTo("/fxml/RegistrazioneVoti.fxml", event);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        log.debug("Logout e navigazione a NewLoginPage");
        navigateTo("/fxml/NewLoginPage.fxml", event);
    }

    private void navigateTo(String fxmlPath, ActionEvent event) {
        try {
            log.debug("Navigazione a {}", fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            log.info("Scena caricata correttamente");
        } catch (IOException e) {
            log.error("Errore durante la navigazione a {}", fxmlPath, e);
        }
    }
}
