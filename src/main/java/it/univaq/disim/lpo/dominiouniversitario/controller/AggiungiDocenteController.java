package it.univaq.disim.lpo.dominiouniversitario.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.univaq.disim.lpo.dominiouniversitario.core.Docenti;
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneDocenti;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AggiungiDocenteController {

    private static final Logger log = LoggerFactory.getLogger(AggiungiDocenteController.class);

    @FXML
    private TextField nomeField;
    @FXML
    private TextField cognomeField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confermaPasswordField;
    @FXML
    private ComboBox<Docenti.TipoDocente> ruoloComboBox;
    @FXML
    private Button aggiungiButton;

    @FXML
    private Button ricercaStudenteNavButton;
    @FXML
    private Button ricercaDocenteNavButton;
    @FXML
    private Button ricercaAuleNavButton;
    @FXML
    private Button logoutButton;

    private boolean isModifica = false;
    private String idOriginale = null;

    @FXML
    public void initialize() {
        log.debug("AggiungiDocenteController inizializzato");

        ruoloComboBox.setItems(FXCollections.observableArrayList(Docenti.TipoDocente.values()));

        Docenti docenteDaModificare = GestioneDocenti.getInstance().getDocenteDaModificare();
        if (docenteDaModificare != null) {
            log.info("Modalità modifica attivata per docente {}", docenteDaModificare.getIdDocente());
            isModifica = true;
            idOriginale = docenteDaModificare.getIdDocente();
            precompilaCampi(docenteDaModificare);
            aggiungiButton.setText("Salva Modifiche");
        }
    }

    @FXML
    public void onAggiungiDocente(ActionEvent event) {
        if (isModifica) {
            modificaDocente(event);
        } else {
            aggiungiDocente(event);
        }
    }

    private void aggiungiDocente(ActionEvent event) {
        log.debug("Tentativo di aggiunta nuovo docente");

        String nome = nomeField.getText();
        String cognome = cognomeField.getText();
        String password = passwordField.getText();
        String confermaPassword = confermaPasswordField.getText();
        Docenti.TipoDocente ruolo = ruoloComboBox.getValue();

        if (nome == null || nome.trim().isEmpty() ||
            cognome == null || cognome.trim().isEmpty() ||
            password == null || password.isEmpty() ||
            confermaPassword == null || confermaPassword.isEmpty() ||
            ruolo == null) {
            log.warn("Aggiunta docente fallita: campi obbligatori mancanti");
            mostraAlert("Campi mancanti", "Per favore, compila tutti i campi obbligatori.");
            return;
        }

        if (!password.equals(confermaPassword)) {
            log.warn("Aggiunta docente fallita: password non corrispondenti");
            mostraAlert("Errore Password", "Le password non corrispondono. Riprova.");
            return;
        }

        try {
            String email = nome.toLowerCase().trim() + "." + cognome.toLowerCase().trim() + "@univaq.it";

            Docenti nuovoDocente = new Docenti(
                nome.trim(),
                cognome.trim(),
                null,
                email,
                password,
                ruolo
            );

            nuovoDocente.assegnaIDUnico(GestioneDocenti.getInstance());

            GestioneDocenti.getInstance().aggiungiDocente(nuovoDocente);

            GestioneDocenti.getInstance().salvaDocenti();

            log.info("Docente {} {} aggiunto con successo. ID: {}", nome, cognome, nuovoDocente.getIdDocente());
            mostraAlert("Successo", "Docente " + nome + " " + cognome + " aggiunto con successo!\nID: " + nuovoDocente.getIdDocente() + "\nEmail: " + email);
            pulisciCampi();

        } catch (Exception e) {
            log.error("Errore durante l'aggiunta del docente: {}", e.getMessage(), e);
            mostraAlert("Errore", "Impossibile aggiungere il docente: " + e.getMessage());
        }
    }

    private void modificaDocente(ActionEvent event) {
        log.debug("Tentativo di modifica docente {}", idOriginale);

        String nome = nomeField.getText();
        String cognome = cognomeField.getText();
        String password = passwordField.getText();
        String confermaPassword = confermaPasswordField.getText();
        Docenti.TipoDocente ruolo = ruoloComboBox.getValue();

        if (nome == null || nome.trim().isEmpty() ||
            cognome == null || cognome.trim().isEmpty() ||
            ruolo == null) {
            log.warn("Modifica docente fallita: campi obbligatori mancanti");
            mostraAlert("Campi mancanti", "Per favore, compila tutti i campi obbligatori.");
            return;
        }

        if ((password != null && !password.isEmpty()) || (confermaPassword != null && !confermaPassword.isEmpty())) {
            if (!password.equals(confermaPassword)) {
                log.warn("Modifica docente fallita: password non corrispondenti");
                mostraAlert("Errore Password", "Le password non corrispondono. Riprova.");
                return;
            }
        }

        try {
            Docenti docente = GestioneDocenti.getInstance().getDocenteById(idOriginale);
            if (docente == null) {
                mostraAlert("Errore", "Docente non trovato.");
                return;
            }

            docente.setNome(nome.trim());
            docente.setCognome(cognome.trim());
            if (password != null && !password.isEmpty()) {
                docente.setPassword(password);
            }
            docente.setRuolo(ruolo);
            
            String email = nome.toLowerCase().trim() + "." + cognome.toLowerCase().trim() + "@univaq.it";
            docente.setEmail(email);

            GestioneDocenti.getInstance().salvaDocenti();

            log.info("Docente {} modificato con successo", idOriginale);
            mostraAlert("Successo", "Docente " + nome + " " + cognome + " modificato con successo!");
            
            GestioneDocenti.getInstance().clearDocenteDaModificare();
            cambiaScena(event, "RicercaDocente.fxml");

        } catch (Exception e) {
            log.error("Errore durante la modifica del docente: {}", e.getMessage(), e);
            mostraAlert("Errore", "Impossibile modificare il docente: " + e.getMessage());
        }
    }

    private void precompilaCampi(Docenti docente) {
        nomeField.setText(docente.getNome());
        cognomeField.setText(docente.getCognome());
        ruoloComboBox.setValue(docente.getRuolo());
    }

    @FXML
    public void onRicercaStudente(ActionEvent event) {
        GestioneDocenti.getInstance().clearDocenteDaModificare();
        try {
            cambiaScena(event, "RicercaStudente.fxml");
        } catch (IOException e) {
            log.error("Errore cambio scena: {}", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile aprire Ricerca Studente.");
        }
    }

    @FXML
    public void onRicercaDocente(ActionEvent event) {
        GestioneDocenti.getInstance().clearDocenteDaModificare();
        try {
            cambiaScena(event, "RicercaDocente.fxml");
        } catch (IOException e) {
            log.error("Errore cambio scena: {}", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile aprire Ricerca Docente.");
        }
    }

    @FXML
    public void onRicercaAule(ActionEvent event) {
        GestioneDocenti.getInstance().clearDocenteDaModificare();
        try {
            cambiaScena(event, "RicercaAule.fxml");
        } catch (IOException e) {
            log.error("Errore cambio scena: {}", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile aprire Ricerca Aule.");
        }
    }

    @FXML
    public void onLogout(ActionEvent event) {
        GestioneDocenti.getInstance().clearDocenteDaModificare();
        try {
            cambiaScena(event, "NewLoginPage.fxml");
        } catch (IOException e) {
            log.error("Errore logout: {}", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile tornare al login.");
        }
    }

    @FXML
    public void onAltro(ActionEvent event) {
        mostraAlert("Errore", "Sezione non ancora implementata.");
    }
    
    private void pulisciCampi() {
        nomeField.clear();
        cognomeField.clear();
        passwordField.clear();
        confermaPasswordField.clear();
        ruoloComboBox.setValue(null);
    }

    private void cambiaScena(ActionEvent event, String fxmlFileName) throws IOException {
        log.debug("Cambio scena verso {}", fxmlFileName);
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
