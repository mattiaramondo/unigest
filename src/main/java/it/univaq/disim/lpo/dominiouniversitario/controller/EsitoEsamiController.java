package it.univaq.disim.lpo.dominiouniversitario.controller;

import it.univaq.disim.lpo.dominiouniversitario.core.EsameSostenuto;
import it.univaq.disim.lpo.dominiouniversitario.core.Studenti;
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneStudenti;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class EsitoEsamiController {

    @FXML
    private Button resultsButton;
    @FXML
    private ListView<EsameSostenuto> resultsListView;

    private Studenti studenteCorrente;

    private static final Logger log = LoggerFactory.getLogger(EsitoEsamiController.class);

    @FXML
    public void initialize() {
        log.debug("Inizializzazione EsitoEsamiController");
        if (resultsButton != null) {
            resultsButton.setDisable(true);
            resultsButton.getStyleClass().add("vbox-button-active");
        }

        studenteCorrente = GestioneStudenti.getInstance().getProfiloStudente();
        if (studenteCorrente != null) {
            caricaEsiti();
        }
    }

    private void caricaEsiti() {
        log.debug("Caricamento esiti esami per lo studente corrente");
        List<EsameSostenuto> esami = studenteCorrente.getListaEsami();
        ObservableList<EsameSostenuto> esamiObservable = FXCollections.observableArrayList(esami);
        
        resultsListView.setItems(esamiObservable);
        resultsListView.setCellFactory(param -> new ListCell<EsameSostenuto>() {
            @Override
            protected void updateItem(EsameSostenuto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox container = new VBox(5);
                    Label messageLabel = new Label();
                    messageLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
                    
                    Label detailsLabel = new Label(item.getInsegnamento().getNome() + " - Data: " + item.getData());
                    detailsLabel.setFont(Font.font("System", 14));

                    if (item.getVoto() == 0) {
                        messageLabel.setText("Purtroppo, l'esito dell'esame " + item.getInsegnamento().getNome() + " è \"Non superato\"");
                        messageLabel.setTextFill(Color.RED);
                    } else {
                        String votoStr = String.valueOf(item.getVoto());
                        if (item.isLode()) votoStr += " e Lode";
                        messageLabel.setText("Congratulazioni, hai superato l'esame " + item.getInsegnamento().getNome() + " con voto: " + votoStr);
                        messageLabel.setTextFill(Color.GREEN);
                    }
                    
                    container.getChildren().addAll(messageLabel, detailsLabel);
                    setGraphic(container);
                }
            }
        });
    }

    @FXML
    private void handleProfilo(ActionEvent event) {
        log.debug("Navigazione a ProfiloStudente.fxml");
        navigateTo("/fxml/ProfiloStudente.fxml", event);
    }

    @FXML
    private void handleCarriera(ActionEvent event) {
        log.debug("Navigazione a RiepilogoCarriera.fxml");
        navigateTo("/fxml/RiepilogoCarriera.fxml", event);
    }

    @FXML
    private void handlePrenotazioneEsami(ActionEvent event) {
        log.debug("Navigazione a PrenotazioneEsami.fxml");
        navigateTo("/fxml/PrenotazioneEsami.fxml", event);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        log.debug("Logout in corso");
        navigateTo("/fxml/NewLoginPage.fxml", event);
    }

    private void navigateTo(String fxmlPath, ActionEvent event) {
        log.debug("Cambio scena a: " + fxmlPath);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            log.info("Scena caricata correttamente: " + fxmlPath);
        } catch (IOException e) {
            log.error("Errore nel cambio scena a: " + fxmlPath, e);
            e.printStackTrace();
        }
    }
}
