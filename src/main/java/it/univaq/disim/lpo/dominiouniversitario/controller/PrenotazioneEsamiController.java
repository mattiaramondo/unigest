package it.univaq.disim.lpo.dominiouniversitario.controller;

import it.univaq.disim.lpo.dominiouniversitario.core.Appello;
import it.univaq.disim.lpo.dominiouniversitario.core.Studenti;
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneAppelli;
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneStudenti;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.Alert;

public class PrenotazioneEsamiController {

    @FXML
    private Button examsButton;
    @FXML
    private Button esitoEsamiButton;
    @FXML
    private TableView<Appello> tabellaAppelli;
    @FXML
    private TableColumn<Appello, String> colonnaInsegnamento;
    @FXML
    private TableColumn<Appello, String> colonnaData;
    @FXML
    private TableColumn<Appello, Void> colonnaAzioni;

    private Studenti studenteCorrente;

    private static final Logger log = LoggerFactory.getLogger(PrenotazioneEsamiController.class);

    @FXML
    public void initialize() {
        log.debug("Inizializzazione PrenotazioneEsamiController");
        if (examsButton != null) {
            examsButton.setDisable(true);
            examsButton.getStyleClass().add("vbox-button-active");
        }

        studenteCorrente = GestioneStudenti.getInstance().getProfiloStudente();

        colonnaInsegnamento.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInsegnamento().getNome()));
        colonnaData.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDataEsame().toString()));

        Callback<TableColumn<Appello, Void>, TableCell<Appello, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Appello, Void> call(final TableColumn<Appello, Void> param) {
                return new TableCell<>() {
                    {
                        log.debug("Creazione cella azioni per appello");
                    }
                    private final Button btn = new Button("Prenotati");

                    {
                        btn.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white;");
                        btn.setOnAction(event -> {
                            Appello appello = getTableView().getItems().get(getIndex());
                            handlePrenotazione(appello);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            
                            Appello appello = getTableView().getItems().get(getIndex());
                            
                            boolean esameGiaSuperato = false;
                            if (studenteCorrente != null && studenteCorrente.getListaEsami() != null) {
                                for(it.univaq.disim.lpo.dominiouniversitario.core.EsameSostenuto es : studenteCorrente.getListaEsami()) {
                                    if(es.getInsegnamento().getCodiceInsegnamento().equals(appello.getInsegnamento().getCodiceInsegnamento()) && es.getVoto() >= 18) {
                                        esameGiaSuperato = true;
                                        break;
                                    }
                                }
                            }

                            if (esameGiaSuperato) {
                                btn.setDisable(true);
                                btn.setText("Già Superato");
                                btn.setStyle("-fx-background-color: #cccccc; -fx-text-fill: black;");
                            } else if (appello != null && isStudenteIscritto(appello)) {
                                btn.setDisable(false);
                                btn.setText("Annulla Iscrizione");
                                btn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
                                btn.setOnAction(event -> {
                                    handleAnnullaPrenotazione(appello);
                                });
                            } else {
                                btn.setDisable(false);
                                btn.setText("Prenotati");
                                btn.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white;");
                                btn.setOnAction(event -> {
                                    handlePrenotazione(appello);
                                });
                            }
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        colonnaAzioni.setCellFactory(cellFactory);

        log.debug("Caricamento lista appelli disponibili");
        ObservableList<Appello> appelliList = FXCollections.observableArrayList(GestioneAppelli.getInstance().getTuttiGliAppelli());
        tabellaAppelli.setItems(appelliList);
    }

    private void handleAnnullaPrenotazione(Appello appello) {
        log.debug("Annullamento prenotazione per l'appello: {}", appello.getInsegnamento().getNome());
        boolean successo = GestioneAppelli.getInstance().annullaPrenotazione(studenteCorrente, appello);
        
        if (successo) {
            log.info("Prenotazione annullata con successo per l'appello: {}", appello.getInsegnamento().getNome());
            showAlert(Alert.AlertType.INFORMATION, "Prenotazione Annullata", "Hai annullato l'iscrizione all'esame di " + appello.getInsegnamento().getNome());
            tabellaAppelli.refresh(); 
        } else {
            log.error("Errore nell'annullamento della prenotazione per l'appello: {}", appello.getInsegnamento().getNome());
            showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile annullare la prenotazione.");
        }
    }

    private boolean isStudenteIscritto(Appello appello) {
        log.debug("Verifica iscrizione studente per l'appello: {}", appello.getInsegnamento().getNome());
        if (appello.getIscrizioni() == null) return false;
        return appello.getIscrizioni().stream()
                .anyMatch(i -> i.getStudente().getMatricola().equals(studenteCorrente.getMatricola()));
    }

    private void handlePrenotazione(Appello appello) {
        log.debug("Gestione prenotazione per l'appello: {}", appello.getInsegnamento().getNome());
        boolean successo = GestioneAppelli.getInstance().prenotaStudente(studenteCorrente, appello);
        
        if (successo) {
            log.info("Prenotazione effettuata con successo per l'appello: {}", appello.getInsegnamento().getNome());
            showAlert(Alert.AlertType.INFORMATION, "Prenotazione Effettuata", "Ti sei prenotato con successo all'esame di " + appello.getInsegnamento().getNome());
            tabellaAppelli.refresh();
        } else {
            log.error("Errore nella prenotazione per l'appello: {}", appello.getInsegnamento().getNome());
            showAlert(Alert.AlertType.ERROR, "Errore Prenotazione", "Impossibile effettuare la prenotazione. Potresti aver già superato questo esame o essere già iscritto.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleProfiloStudente(ActionEvent event) {
        log.debug("Navigazione a ProfiloStudente.fxml");
        cambiaScena(event, "/fxml/ProfiloStudente.fxml");
    }

    @FXML
    private void handleRiepilogoCarriera(ActionEvent event) {
        log.debug("Navigazione a RiepilogoCarriera.fxml");
        cambiaScena(event, "/fxml/RiepilogoCarriera.fxml");
    }

    @FXML
    private void goToEsitoEsami(ActionEvent event) {
        log.debug("Navigazione a EsitoEsami.fxml");
        cambiaScena(event, "/fxml/EsitoEsami.fxml");
    }

    private void cambiaScena(ActionEvent event, String fxmlPath) {
        try {
            log.debug("Cambio scena a {}", fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            log.info("Scena caricata correttamente");
        } catch (IOException e) {
            log.error("Errore nel caricamento della scena: {}", fxmlPath, e);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        log.debug("Logout in corso");
        cambiaScena(event, "/fxml/NewLoginPage.fxml");
    }
}
