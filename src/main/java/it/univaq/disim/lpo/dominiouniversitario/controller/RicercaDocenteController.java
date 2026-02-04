package it.univaq.disim.lpo.dominiouniversitario.controller;

import it.univaq.disim.lpo.dominiouniversitario.core.Docenti;
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneDocenti;
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

public class RicercaDocenteController {

    private static final Logger log = LoggerFactory.getLogger(RicercaDocenteController.class);

    @FXML
    private TextField nomeField;
    @FXML
    private Button ricercaButton;

    @FXML
    private TableView<Docenti> docentiTable;
    @FXML
    private TableColumn<Docenti, String> areaCol;
    @FXML
    private TableColumn<Docenti, String> cognomeCol;
    @FXML
    private TableColumn<Docenti, String> nomeCol;
    @FXML
    private TableColumn<Docenti, String> ruoloCol;
    @FXML
    private TableColumn<Docenti, Void> azioniCol;

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
        log.debug("RicercaDocenteController inizializzato");

        if (ricercaDocenteNavButton != null) {
            ricercaDocenteNavButton.setDisable(true);
            ricercaDocenteNavButton.getStyleClass().add("vbox-button-active");
        }

        areaCol.setCellValueFactory(cell -> new SimpleStringProperty("Area non disponibile"));
        cognomeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCognome()));
        nomeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNome()));
        ruoloCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getRuolo() != null ? cell.getValue().getRuolo().toString() : ""));

        Callback<TableColumn<Docenti, Void>, TableCell<Docenti, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Docenti, Void> call(final TableColumn<Docenti, Void> param) {
                final TableCell<Docenti, Void> cell = new TableCell<>() {
                    private final Button btnApri = new Button("Apri");
                    private final Button btnModifica = new Button("Modifica");
                    private final Button btnRimuovi = new Button("Rimuovi");
                    {
                        btnApri.setOnAction((ActionEvent event) -> {
                            Docenti d = getTableView().getItems().get(getIndex());
                            log.info("Apertura home docente {}", d.getIdDocente());
                            GestioneDocenti.getInstance().setProfiloDocente(d);
                            try {
                                cambiaScena(event, "HomeInsegnante.fxml");
                            } catch (IOException e) {
                                log.error("Errore aprendo HomeInsegnante: {}", e.getMessage(), e);
                                mostraAlert("Errore tecnico", "Impossibile aprire la home del docente: " + e.getMessage());
                            }
                        });
                        
                        btnModifica.setOnAction((ActionEvent event) -> {
                            Docenti d = getTableView().getItems().get(getIndex());
                            log.info("Modifica docente {}", d.getIdDocente());
                            GestioneDocenti.getInstance().setDocenteDaModificare(d);
                            try {
                                cambiaScena(event, "CreazioneDocente.fxml");
                            } catch (IOException e) {
                                log.error("Errore aprendo CreazioneDocente: {}", e.getMessage(), e);
                                mostraAlert("Errore tecnico", "Impossibile aprire la pagina di modifica: " + e.getMessage());
                            }
                        });
                        
                        btnRimuovi.setOnAction((ActionEvent event) -> {
                            Docenti d = getTableView().getItems().get(getIndex());
                            log.info("Rimozione docente {}", d.getIdDocente());
                            
                            GestioneDocenti.getInstance().rimuoviDocente(d.getIdDocente());
                            GestioneDocenti.getInstance().saveData();
                            
                            docentiTable.setItems(FXCollections.observableArrayList(GestioneDocenti.getInstance().getDocenti()));
                            
                            mostraAlert("Successo", "Docente " + d.getCognome() + " " + d.getNome() + " rimosso con successo.");
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

        ObservableList<Docenti> all = FXCollections.observableArrayList(GestioneDocenti.getInstance().getDocenti());
        docentiTable.setItems(all);
    }

    @FXML
    public void onRicerca(ActionEvent event) {
        String nome = nomeField.getText() != null ? nomeField.getText().trim() : "";
        log.info("Ricerca docente per nome: {}", nome);

        if (nome.isEmpty()) {
            docentiTable.setItems(FXCollections.observableArrayList(GestioneDocenti.getInstance().getDocenti()));
            return;
        }

        ObservableList<Docenti> risultati = FXCollections.observableArrayList();
        for (Docenti d : GestioneDocenti.getInstance().getDocenti()) {
            if (d.getNome().toLowerCase().contains(nome.toLowerCase()) || 
                d.getCognome().toLowerCase().contains(nome.toLowerCase())) {
                risultati.add(d);
            }
        }

        if (risultati.isEmpty()) {
            docentiTable.getItems().clear();
            mostraAlert("Nessun risultato", "Nessun docente trovato con nome: " + nome);
        } else {
            docentiTable.setItems(risultati);
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
        try {
            cambiaScena(event, "RicercaStudente.fxml");
        } catch (IOException e) {
            log.error("Errore cambio scena RicercaStudente: {}", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile aprire Ricerca Studente: " + e.getMessage());
        }
    }

    @FXML
    public void onRicercaDocente(ActionEvent event) {
        mostraAlert("Info", "Sei già nella pagina di ricerca docenti.");
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

    @FXML
    public void onCreazioneDocenteOggetto(ActionEvent event) {
        log.info("Apertura pagina creazione docente");
        GestioneDocenti.getInstance().clearDocenteDaModificare();
        
        try {
            cambiaScena(event, "CreazioneDocente.fxml");
        } catch (IOException e) {
            log.error("Errore cambio scena CreazioneDocente: {}", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile aprire la pagina di creazione docente: " + e.getMessage());
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
