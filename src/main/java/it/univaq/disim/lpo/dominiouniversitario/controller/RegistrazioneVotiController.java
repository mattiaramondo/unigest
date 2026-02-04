package it.univaq.disim.lpo.dominiouniversitario.controller;

import it.univaq.disim.lpo.dominiouniversitario.core.Appello;
import it.univaq.disim.lpo.dominiouniversitario.core.Docenti;
import it.univaq.disim.lpo.dominiouniversitario.core.Studenti;
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneDocenti;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistrazioneVotiController {

    @FXML
    private Button votesButton;

    @FXML
    private ComboBox<Appello> appelliComboBox;

    @FXML
    private TableView<Studenti> studentiTable;

    @FXML
    private TableColumn<Studenti, String> colonnaMatricola;

    @FXML
    private TableColumn<Studenti, String> colonnaNome;

    @FXML
    private TableColumn<Studenti, String> colonnaCognome;

    @FXML
    private TableColumn<Studenti, String> colonnaEmail;

    @FXML
    private TextField votoField;

    @FXML
    private CheckBox lodeCheckBox;

    @FXML
    private Label messaggioLabel;

    private Docenti docenteLoggato;

    private static final Logger log = LoggerFactory.getLogger(RegistrazioneVotiController.class);

    @FXML
    public void initialize() {
        log.debug("Inizializzazione RegistrazioneVotiController");
        if (votesButton != null) {
            votesButton.setDisable(true);
            votesButton.getStyleClass().add("vbox-button-active");
        }

        docenteLoggato = GestioneDocenti.getInstance().getProfiloDocente();
        if (docenteLoggato == null) {
            messaggioLabel.setText("Errore: Nessun docente loggato.");
            return;
        }

        log.debug("Configurazione colonne tabella studenti");
        colonnaMatricola.setCellValueFactory(new PropertyValueFactory<>("matricola"));
        colonnaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colonnaCognome.setCellValueFactory(new PropertyValueFactory<>("cognome"));
        colonnaEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        configuraAppelliComboBox();

        appelliComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                caricaStudentiIscritti(newVal);
            } else {
                studentiTable.getItems().clear();
            }
        });

        log.debug("Configurazione evento doppio click sulla tabella studenti");
        studentiTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && studentiTable.getSelectionModel().getSelectedItem() != null) {
                apriDialogVerbalizzazione();
            }
        });
    }

    private void apriDialogVerbalizzazione() {
        log.debug("Apertura dialog verbalizzazione voto");
        Studenti studenteSelezionato = studentiTable.getSelectionModel().getSelectedItem();
        Appello appelloSelezionato = appelliComboBox.getSelectionModel().getSelectedItem();

        if (studenteSelezionato != null && appelloSelezionato != null) {
            try {
                log.debug("Caricamento dialog VerbalizzazioneVotoDialog.fxml");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VerbalizzazioneVotoDialog.fxml"));
                Parent page = loader.load();

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Verbalizzazione Voto");
                dialogStage.initOwner(votesButton.getScene().getWindow());
                dialogStage.setScene(new Scene(page));

                VerbalizzazioneVotoDialogController controller = loader.getController();
                controller.setDialogStage(dialogStage);
                controller.setDati(studenteSelezionato, appelloSelezionato, docenteLoggato.getIdDocente());

                dialogStage.showAndWait();

                if (controller.isConfirmed()) {
                    messaggioLabel.setText("Operazione completata per " + studenteSelezionato.getCognome());
                    messaggioLabel.setStyle("-fx-text-fill: green;");
                    caricaStudentiIscritti(appelloSelezionato);
                }

            } catch (IOException e) {
                log.error("Errore nell'apertura della finestra di verbalizzazione voto", e);
                e.printStackTrace();
                messaggioLabel.setText("Errore nell'apertura della finestra.");
            }
        }
    }

    private void configuraAppelliComboBox() {
        log.debug("Configurazione ComboBox appelli");
        List<Appello> appelli = docenteLoggato.getAppelli();
        ObservableList<Appello> appelliObservable = FXCollections.observableArrayList(appelli);
        appelliComboBox.setItems(appelliObservable);

        appelliComboBox.setCellFactory(param -> new ListCell<Appello>() {
            @Override
            protected void updateItem(Appello item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    log.info("Elemento vuoto o nullo nella ComboBox appelli");
                    setText(null);
                } else {
                    log.info("Visualizzazione appello: {} - {}", item.getInsegnamento().getNome(), item.getDataEsame());
                    setText(item.getInsegnamento().getNome() + " - " + item.getDataEsame());
                }
            }
        });

        appelliComboBox.setConverter(new StringConverter<Appello>() {
            @Override
            public String toString(Appello object) {
                if (object == null) {
                    return null;
                }
                return object.getInsegnamento().getNome() + " - " + object.getDataEsame();
            }

            @Override
            public Appello fromString(String string) {
                return appelliComboBox.getItems().stream().filter(ap -> 
                    (ap.getInsegnamento().getNome() + " - " + ap.getDataEsame()).equals(string)).findFirst().orElse(null);
            }
        });
    }

    private void caricaStudentiIscritti(Appello appello) {
        log.debug("Caricamento studenti iscritti per l'appello: {}", appello.getInsegnamento().getNome());
        List<Studenti> iscritti = GestioneDocenti.getInstance().visualizzaIscritti(docenteLoggato.getIdDocente(), appello);
        ObservableList<Studenti> iscrittiObservable = FXCollections.observableArrayList(iscritti);
        studentiTable.setItems(iscrittiObservable);
    }

    @FXML
    private void handleProfiloDocente(ActionEvent event) {
        log.debug("Navigazione al Profilo Docente");
        navigateTo("/fxml/HomeInsegnante.fxml", event);
    }

    @FXML
    private void handleGestioneAppelli(ActionEvent event) {
        log.debug("Navigazione a Gestione Appelli");
        navigateTo("/fxml/GestioneAppelli.fxml", event);
    }

    @FXML
    private void handleInsegnamentiAttivi(ActionEvent event) {
        log.debug("Navigazione a Insegnamenti Attivi");
        navigateTo("/fxml/InsegnamentiAttivi.fxml", event);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        log.debug("Logout in corso");
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
            log.error("Errore durante la navigazione a {}: {}", fxmlPath, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
