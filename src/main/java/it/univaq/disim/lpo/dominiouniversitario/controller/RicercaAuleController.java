package it.univaq.disim.lpo.dominiouniversitario.controller;

import it.univaq.disim.lpo.dominiouniversitario.core.AuleEdifici;
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneAule;
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

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class RicercaAuleController {

    private static final Logger log = LoggerFactory.getLogger(RicercaAuleController.class);

    @FXML
    private TextField nomeAulaRicercaField;
    @FXML
    private Button ricercaButton;

    @FXML
    private TextField nomeAulaField;
    @FXML
    private TextField capienzaField;
    @FXML
    private Button aggiungiAulaButton;

    @FXML
    private TableView<AuleEdifici> auleTable;
    @FXML
    private TableColumn<AuleEdifici, String> nomeAulaCol;
    @FXML
    private TableColumn<AuleEdifici, Number> capienzaCol;
    @FXML
    private TableColumn<AuleEdifici, Void> azioniCol;

    @FXML
    private Button ricercaStudenteNavButton;
    @FXML
    private Button ricercaDocenteNavButton;
    @FXML
    private Button ricercaAuleNavButton;
    @FXML
    private Button logoutButton;

    @FXML
    public void initialize() {
        log.debug("RicercaAuleController inizializzato");

        if (ricercaAuleNavButton != null) {
            ricercaAuleNavButton.setDisable(true);
            ricercaAuleNavButton.getStyleClass().add("vbox-button-active");
        }

        nomeAulaCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNomeAula()));
        capienzaCol.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getCapienza()));

        Callback<TableColumn<AuleEdifici, Void>, TableCell<AuleEdifici, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<AuleEdifici, Void> call(final TableColumn<AuleEdifici, Void> param) {
                final TableCell<AuleEdifici, Void> cell = new TableCell<>() {
                    private final Button btnRimuovi = new Button("Rimuovi");
                    {
                        btnRimuovi.setOnAction((ActionEvent event) -> {
                            AuleEdifici aula = getTableView().getItems().get(getIndex());
                            log.info("Rimozione aula: {}", aula.getNomeAula());
                            
                            GestioneAule.getInstance().rimuoviAula(aula.getNomeAula());
                            GestioneAule.getInstance().saveData();
                            
                            aggiornaTabella();
                            
                            mostraAlert("Successo", "Aula " + aula.getNomeAula() + " rimossa con successo.");
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox box = new HBox(btnRimuovi);
                            box.setSpacing(5);
                            setGraphic(box);
                        }
                    }
                };
                return cell;
            }
        };

        azioniCol.setCellFactory(cellFactory);

        aggiornaTabella();
    }

    @FXML
    public void onRicerca(ActionEvent event) {
        String nomeAula = nomeAulaRicercaField.getText() != null ? nomeAulaRicercaField.getText().trim() : "";
        log.info("Ricerca aula per nome: {}", nomeAula);

        if (nomeAula.isEmpty()) {
            aggiornaTabella();
            return;
        }

        ObservableList<AuleEdifici> risultati = FXCollections.observableArrayList();
        for (AuleEdifici aula : GestioneAule.getInstance().getAule()) {
            if (aula.getNomeAula().toLowerCase().contains(nomeAula.toLowerCase())) {
                risultati.add(aula);
            }
        }

        if (risultati.isEmpty()) {
            auleTable.getItems().clear();
            mostraAlert("Nessun risultato", "Nessuna aula trovata con nome: " + nomeAula);
        } else {
            auleTable.setItems(risultati);
        }
    }

    @FXML
    public void onAggiungiAula(ActionEvent event) {
        String nomeAula = nomeAulaField.getText() != null ? nomeAulaField.getText().trim() : "";
        String capienzaStr = capienzaField.getText() != null ? capienzaField.getText().trim() : "";

        log.info("Tentativo di aggiunta aula: {} con capienza {}", nomeAula, capienzaStr);

        if (nomeAula.isEmpty()) {
            mostraAlert("Errore", "Il nome dell'aula non può essere vuoto.");
            return;
        }

        if (capienzaStr.isEmpty()) {
            mostraAlert("Errore", "La capienza non può essere vuota.");
            return;
        }

        int capienza;
        try {
            capienza = Integer.parseInt(capienzaStr);
            if (capienza <= 0) {
                mostraAlert("Errore", "La capienza deve essere un numero positivo.");
                return;
            }
        } catch (NumberFormatException e) {
            mostraAlert("Errore", "La capienza deve essere un numero valido.");
            return;
        }

        if (GestioneAule.getInstance().getAulaByNome(nomeAula) != null) {
            mostraAlert("Errore", "Un'aula con questo nome esiste già.");
            return;
        }

        AuleEdifici nuovaAula = new AuleEdifici(nomeAula, capienza);
        GestioneAule.getInstance().aggiungiAula(nuovaAula);
        GestioneAule.getInstance().saveData();

        log.info("Aula {} aggiunta con successo", nomeAula);

        nomeAulaField.clear();
        capienzaField.clear();

        aggiornaTabella();

        mostraAlert("Successo", "Aula " + nomeAula + " aggiunta con successo.");
    }

    private void aggiornaTabella() {
        ObservableList<AuleEdifici> all = FXCollections.observableArrayList(GestioneAule.getInstance().getAule());
        auleTable.setItems(all);
        log.debug("Tabella aule aggiornata con {} elementi", all.size());
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
        try {
            cambiaScena(event, "RicercaDocente.fxml");
        } catch (IOException e) {
            log.error("Errore cambio scena RicercaDocente: {}", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile aprire Ricerca Docente: " + e.getMessage());
        }
    }

    @FXML
    public void onRicercaAule(ActionEvent event) {
        mostraAlert("Info", "Sei già nella pagina di gestione aule.");
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
