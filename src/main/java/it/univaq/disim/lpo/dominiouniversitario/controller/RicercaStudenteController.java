package it.univaq.disim.lpo.dominiouniversitario.controller;

import it.univaq.disim.lpo.dominiouniversitario.core.Studenti;
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneStudenti;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javafx.beans.property.SimpleStringProperty;

public class RicercaStudenteController {

    private static final Logger log = LoggerFactory.getLogger(RicercaStudenteController.class);

    @FXML
    private TextField matricolaField;
    @FXML
    private Button ricercaButton;

    @FXML
    private TableView<Studenti> studentiTable;
    @FXML
    private TableColumn<Studenti, String> matricolaCol;
    @FXML
    private TableColumn<Studenti, String> cognomeCol;
    @FXML
    private TableColumn<Studenti, String> nomeCol;
    @FXML
    private TableColumn<Studenti, String> corsoCol;
    @FXML
    private TableColumn<Studenti, Void> azioniCol;

    @FXML
    private Button profiloAdminButton;
    @FXML
    private Button ricercaStudenteNavButton;
    @FXML
    private Button ricercaDocenteNavButton;
    @FXML
    private Button altroButton;
    @FXML
    private Button logoutButton;

    @FXML
    public void initialize() {
        log.debug("RicercaStudenteController inizializzato");

        if (ricercaStudenteNavButton != null) {
            ricercaStudenteNavButton.setDisable(true);
            ricercaStudenteNavButton.getStyleClass().add("vbox-button-active");
        }

        matricolaCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMatricola()));
        cognomeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCognome()));
        nomeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNome()));
        corsoCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getCorsoDiLaurea() != null ? cell.getValue().getCorsoDiLaurea().toString() : ""));

        Callback<TableColumn<Studenti, Void>, TableCell<Studenti, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Studenti, Void> call(final TableColumn<Studenti, Void> param) {
                final TableCell<Studenti, Void> cell = new TableCell<>() {
                    private final Button btnApri = new Button("Apri");
                    private final Button btnModifica = new Button("Modifica");
                    private final Button btnRimuovi = new Button("Rimuovi");
                    {
                        btnApri.setOnAction((ActionEvent event) -> {
                            Studenti s = getTableView().getItems().get(getIndex());
                            log.info("Apertura profilo studente {}", s.getMatricola());
                            GestioneStudenti.getInstance().setProfiloStudente(s);
                            try {
                                cambiaScena(event, "ProfiloStudente.fxml");
                            } catch (IOException e) {
                                log.error("Errore aprendo ProfiloStudente: {}", e.getMessage(), e);
                                mostraAlert("Errore tecnico", "Impossibile aprire il profilo: " + e.getMessage());
                            }
                        });
                        
                        btnModifica.setOnAction((ActionEvent event) -> {
                            Studenti s = getTableView().getItems().get(getIndex());
                            log.info("Modifica studente {}", s.getMatricola());
                            GestioneStudenti.getInstance().setStudenteDaModificare(s);
                            try {
                                cambiaScena(event, "CreazioneStudente.fxml");
                            } catch (IOException e) {
                                log.error("Errore aprendo CreazioneStudente: {}", e.getMessage(), e);
                                mostraAlert("Errore tecnico", "Impossibile aprire la pagina di modifica: " + e.getMessage());
                            }
                        });
                        
                        btnRimuovi.setOnAction((ActionEvent event) -> {
                            Studenti s = getTableView().getItems().get(getIndex());
                            log.info("Rimozione studente {}", s.getMatricola());
                            
                            GestioneStudenti.getInstance().rimuoviStudente(s.getMatricola());
                            GestioneStudenti.getInstance().saveData();
                            
                            studentiTable.setItems(FXCollections.observableArrayList(GestioneStudenti.getInstance().getStudentiJson()));
                            
                            mostraAlert("Successo", "Studente " + s.getCognome() + " " + s.getNome() + " rimosso con successo.");
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox box = new HBox(5, btnApri, btnModifica, btnRimuovi);
                            setGraphic(box);
                        }
                    }
                };
                return cell;
            }
        };

        azioniCol.setCellFactory(cellFactory);

        ObservableList<Studenti> all = FXCollections.observableArrayList(GestioneStudenti.getInstance().getStudentiJson());
        studentiTable.setItems(all);
    }

    @FXML
    public void onRicerca(ActionEvent event) {
        String matricola = matricolaField.getText() != null ? matricolaField.getText().trim() : "";
        log.info("Ricerca studente per matricola: {}", matricola);

        if (matricola.isEmpty()) {
            studentiTable.setItems(FXCollections.observableArrayList(GestioneStudenti.getInstance().getStudentiJson()));
            return;
        }

        Studenti s = GestioneStudenti.getInstance().cercaStudente(matricola);
        if (s != null) {
            studentiTable.setItems(FXCollections.observableArrayList(s));
        } else {
            studentiTable.getItems().clear();
            mostraAlert("Nessun risultato", "Nessuno studente trovato con matricola: " + matricola);
        }
    }

    @FXML
    public void onProfiloAdmin(ActionEvent event) {
        try {
            cambiaScena(event, "HomeAdmin.fxml");
        } catch (IOException e) {
            log.error("Errore cambio scena HomeAdmin: {}", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile tornare alla Home Admin: " + e.getMessage());
        }
    }

    @FXML
    public void onRicercaStudente(ActionEvent event) {
        mostraAlert("Info", "Sei già nella pagina di ricerca studenti.");
    }

    @FXML
    public void onRicercaDocente(ActionEvent event) {
        try {
            cambiaScena(event, "RicercaDocente.fxml");
        } catch (IOException e) {
            log.error("Errore cambio scena RicercaDocente: {}", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile aprire Ricerca Docente: " + e.getMessage());
        }
    }

    @FXML
    public void onRicercaAule(ActionEvent event) {
        try {
            cambiaScena(event, "RicercaAule.fxml");
        } catch (IOException e) {
            log.error("Errore cambio scena RicercaAule: {}", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile aprire Ricerca Aule: " + e.getMessage());
        }
    }

    @FXML
    public void onAltro(ActionEvent event) {
        mostraAlert("Funzionalità", "Sezione non ancora implementata.");
    }

    @FXML
    public void onLogout(ActionEvent event) {
        try {
            cambiaScena(event, "NewLoginPage.fxml");
        } catch (IOException e) {
            log.error("Errore logout: {}", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile tornare al login: " + e.getMessage());
        }
    }

    public void onCreazioneStudenteOggetto(ActionEvent event) {
        GestioneStudenti.getInstance().clearStudenteDaModificare();
        
        try {
            cambiaScena(event, "CreazioneStudente.fxml");
        } catch (IOException e) {
            log.error("Errore apertura CreazioneStudente: {}", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile aprire la schermata di creazione studente: " + e.getMessage());
        }
    }

    private void cambiaScena(ActionEvent event, String fxmlFileName) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFileName));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
        log.info("Scena {} caricata", fxmlFileName);
    }

    private void mostraAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
