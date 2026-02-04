package it.univaq.disim.lpo.dominiouniversitario.controller;

import it.univaq.disim.lpo.dominiouniversitario.core.Appello;
import it.univaq.disim.lpo.dominiouniversitario.core.Docenti;
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneAppelli;
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneDocenti;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GestioneAppelliController {

    @FXML
    private Button appelliButton;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Appello> tabellaAppelli;
    
    private ObservableList<Appello> appelliList;

    @FXML
    private TableColumn<Appello, String> colonnaInsegnamento;
    @FXML
    private TableColumn<Appello, String> colonnaData;
    @FXML
    private TableColumn<Appello, String> colonnaLuogo;
    @FXML
    private TableColumn<Appello, Number> colonnaIscritti;
    @FXML
    private TableColumn<Appello, String> colonnaDescrizione;
    @FXML
    private TableColumn<Appello, Void> colonnaAzioni;
    @FXML
    private Button btnAggiungiAppello;

    private static final Logger log = LoggerFactory.getLogger(GestioneAppelliController.class);

    @FXML
    public void initialize() {
        log.debug("Inizializzazione GestioneAppelliController");
        if (appelliButton != null) {
            appelliButton.setDisable(true);
            appelliButton.getStyleClass().add("vbox-button-active");
        }

        colonnaInsegnamento.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInsegnamento().getNome()));
        colonnaData.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDataEsame().toString()));
        colonnaLuogo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLuogo()));
        colonnaIscritti.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIscrizioni().size()));
        colonnaDescrizione.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStato().toString()));

        Callback<TableColumn<Appello, Void>, TableCell<Appello, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Appello, Void> call(final TableColumn<Appello, Void> param) {
                log.debug("Creazione celle per la colonna Azioni");
                return new TableCell<>() {
                    private final Button btnModifica = new Button("Modifica");
                    private final Button btnDettagli = new Button("Dettagli");
                    private final Button btnRimuovi = new Button("Rimuovi");
                    private final HBox pane = new HBox(5, btnModifica, btnDettagli, btnRimuovi);

                    {
                        btnModifica.setStyle("-fx-background-color: #f0ad4e; -fx-text-fill: white;");
                        btnDettagli.setStyle("-fx-background-color: #5bc0de; -fx-text-fill: white;");
                        btnRimuovi.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");

                        btnModifica.setOnAction(event -> {
                            Appello appello = getTableView().getItems().get(getIndex());
                            handleModificaAppello(appello);
                        });
                        
                        btnDettagli.setOnAction(event -> {
                            Appello appello = getTableView().getItems().get(getIndex());
                            handleDettagliAppello(appello);
                        });

                        btnRimuovi.setOnAction(event -> {
                            Appello appello = getTableView().getItems().get(getIndex());
                            handleRimuoviAppello(appello);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
            }
        };
        colonnaAzioni.setCellFactory(cellFactory);

        Docenti docente = GestioneDocenti.getInstance().getProfiloDocente();
        if (docente != null) {
            java.util.List<Appello> appelli = docente.getAppelli();
            
            it.univaq.disim.lpo.dominiouniversitario.core.CorsiLaurea filtro = GestioneDocenti.getInstance().getCorsoSelezionato();
            if (filtro != null) {
                appelli = appelli.stream()
                    .filter(a -> a.getInsegnamento().getCorsoDiLaurea() != null && a.getInsegnamento().getCorsoDiLaurea().equals(filtro))
                    .collect(java.util.stream.Collectors.toList());
            }

            this.appelliList = FXCollections.observableArrayList(appelli);
            
            FilteredList<Appello> filteredData = new FilteredList<>(this.appelliList, p -> true);

            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(appello -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();

                    if (appello.getInsegnamento().getNome().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (appello.getDataEsame().toString().contains(lowerCaseFilter)) {
                        return true;
                    }
                    return false;
                });
            });
            log.debug("Filtro di ricerca applicato agli appelli");

            SortedList<Appello> sortedData = new SortedList<>(filteredData);

            sortedData.comparatorProperty().bind(tabellaAppelli.comparatorProperty());

            tabellaAppelli.setItems(sortedData);
        }

        btnAggiungiAppello.setOnAction(this::handleAggiungiAppello);
    }

    @FXML
    private void handleAggiungiAppello(ActionEvent event) {
        log.debug("Apertura dialog Aggiungi Appello");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AggiungiAppelloDialog.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Aggiungi Appello");
            dialogStage.initOwner(appelliButton.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            log.info("Dialog Aggiungi Appello aperto");

            AggiungiAppelloDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            log.debug("Controller del dialog impostato");

            dialogStage.showAndWait();

            if (controller.isOkClicked()) {
                Appello nuovoAppello = controller.getAppelloCreato();
                Docenti docente = GestioneDocenti.getInstance().getProfiloDocente();
                
                GestioneAppelli.getInstance().aggiungiAppello(docente, nuovoAppello);
                
                if (this.appelliList != null) {
                    this.appelliList.add(nuovoAppello);
                }
                log.info("Nuovo appello aggiunto alla lista visualizzata: " + nuovoAppello.getInsegnamento().getNome());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRimuoviAppello(Appello appello) {
        log.debug("Tentativo di rimozione dell'appello");
        if (appello == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");
        alert.setHeaderText("Stai per eliminare l'appello di " + appello.getInsegnamento().getNome());
        alert.setContentText("Sei sicuro di voler procedere? L'operazione non è reversibile.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Docenti docente = GestioneDocenti.getInstance().getProfiloDocente();
            
            log.debug("Rimozione appello tramite GestioneAppelli");
            GestioneAppelli.getInstance().rimuoviAppello(docente, appello);
            
            if (this.appelliList != null) {
                this.appelliList.remove(appello);
            }
            
            log.info("Appello rimosso: " + appello.getInsegnamento().getNome());
        }
    }

    private void handleModificaAppello(Appello appello) {
        log.debug("Apertura dialog Modifica Appello per: " + appello.getInsegnamento().getNome());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AggiungiAppelloDialog.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Modifica Appello");
            dialogStage.initOwner(appelliButton.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            AggiungiAppelloDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setAppello(appello); 

            dialogStage.showAndWait();
            log.info("Dialog Modifica Appello chiuso");

            if (controller.isOkClicked()) {
                log.info("Salvataggio modifiche appello");
                GestioneDocenti.getInstance().salvaDocenti();
                
                tabellaAppelli.refresh();
                
                log.info("Appello modificato: " + appello.getInsegnamento().getNome());
            }

        } catch (IOException e) {
            log.error("Errore nell'apertura della finestra di modifica appello", e);
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Impossibile aprire la finestra di modifica");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleDettagliAppello(Appello appello) {
        log.debug("Visualizzazione dettagli appello per: " + appello.getInsegnamento().getNome());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dettagli Appello");
        alert.setHeaderText("Dettagli per " + appello.getInsegnamento().getNome());
        alert.setContentText("Data Esame: " + appello.getDataEsame() + "\n" +
                             "Iscritti: " + appello.getIscrizioni().size() + " / " + appello.getNumeroMassimoStudenti() + "\n" +
                             "Stato: " + appello.getStato() + "\n" +
                             "Chiusura Iscrizioni: " + appello.getDataChiusuraIscrizione());
        alert.showAndWait();
    }

    @FXML
    private void handleProfiloDocente(ActionEvent event) {
        log.debug("Navigazione a HomeInsegnante");
        cambiaScena(event, "/fxml/HomeInsegnante.fxml");
    }

    @FXML
    private void handleRegistrazioneVoti(ActionEvent event) {
        log.debug("Navigazione a RegistrazioneVoti");
        cambiaScena(event, "/fxml/RegistrazioneVoti.fxml");
    }

    @FXML
    private void handleInsegnamentiAttivi(ActionEvent event) {
        log.debug("Navigazione a InsegnamentiAttivi");
        cambiaScena(event, "/fxml/InsegnamentiAttivi.fxml");
    }

    private void cambiaScena(ActionEvent event, String fxmlPath) {
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

    @FXML
    private void handleLogout(ActionEvent event) {
        log.debug("Logout in corso");
        cambiaScena(event, "/fxml/NewLoginPage.fxml");
    }
}
