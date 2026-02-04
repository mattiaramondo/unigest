package it.univaq.disim.lpo.dominiouniversitario.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.univaq.disim.lpo.dominiouniversitario.core.CorsiLaurea;
import it.univaq.disim.lpo.dominiouniversitario.core.Studenti;
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneStudenti;
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

public class AggiungiStudenteController {

    private static final Logger log = LoggerFactory.getLogger(AggiungiStudenteController.class);

    @FXML
    private TextField nomeField;
    @FXML
    private TextField cognomeField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confermaPasswordField;
    @FXML
    private ComboBox<Studenti.AnnoAccademico> annoCorsoComboBox;
    @FXML
    private TextField corsoLaureaField;
    @FXML
    private ComboBox<Studenti.TipoLaurea> tipologiaLaureaComboBox;
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
    private String matricolaOriginale = null;

    @FXML
    public void initialize() {
        log.debug("AggiungiStudenteController inizializzato");

        annoCorsoComboBox.setItems(FXCollections.observableArrayList(Studenti.AnnoAccademico.values()));

        tipologiaLaureaComboBox.setItems(FXCollections.observableArrayList(Studenti.TipoLaurea.values()));

        Studenti studenteDaModificare = GestioneStudenti.getInstance().getStudenteDaModificare();
        if (studenteDaModificare != null) {
            log.info("Modalità modifica attivata per studente {}", studenteDaModificare.getMatricola());
            isModifica = true;
            matricolaOriginale = studenteDaModificare.getMatricola();
            precompilaCampi(studenteDaModificare);
            aggiungiButton.setText("Salva Modifiche");
        }
    }

    @FXML
    public void onAggiungiStudente(ActionEvent event) {
        if (isModifica) {
            modificaStudente(event);
        } else {
            aggiungiStudente(event);
        }
    }

    private void aggiungiStudente(ActionEvent event) {
        log.debug("Tentativo di aggiunta nuovo studente");

        String nome = nomeField.getText();
        String cognome = cognomeField.getText();
        String password = passwordField.getText();
        String confermaPassword = confermaPasswordField.getText();
        Studenti.AnnoAccademico annoCorso = annoCorsoComboBox.getValue();
        String corsoLaurea = corsoLaureaField.getText();
        Studenti.TipoLaurea tipologiaLaurea = tipologiaLaureaComboBox.getValue();

        if (nome == null || nome.trim().isEmpty() ||
            cognome == null || cognome.trim().isEmpty() ||
            password == null || password.isEmpty() ||
            confermaPassword == null || confermaPassword.isEmpty() ||
            annoCorso == null ||
            corsoLaurea == null || corsoLaurea.trim().isEmpty() ||
            tipologiaLaurea == null) {
            log.warn("Aggiunta studente fallita: campi obbligatori mancanti");
            mostraAlert("Campi mancanti", "Per favore, compila tutti i campi obbligatori.");
            return;
        }

        if (!password.equals(confermaPassword)) {
            log.warn("Aggiunta studente fallita: password non corrispondenti");
            mostraAlert("Errore Password", "Le password non corrispondono. Riprova.");
            return;
        }

        try {
            CorsiLaurea corso = new CorsiLaurea(corsoLaurea.trim(), corsoLaurea.trim().toUpperCase().replace(" ", "_"));

            Studenti nuovoStudente = new Studenti(
                nome.trim(),
                cognome.trim(),
                password,
                null,
                annoCorso,
                tipologiaLaurea,
                corso
            );

            nuovoStudente.assegnaMatricolaUnica(GestioneStudenti.getInstance());

            GestioneStudenti.getInstance().aggiungiStudente(nuovoStudente);

            GestioneStudenti.getInstance().salvaStudenti();

            log.info("Studente {} {} aggiunto con successo. Matricola: {}", nome, cognome, nuovoStudente.getMatricola());
            mostraAlert("Successo", "Studente " + nome + " " + cognome + " aggiunto con successo!\nMatricola: " + nuovoStudente.getMatricola());
            pulisciCampi();

        } catch (Exception e) {
            log.error("Errore durante l'aggiunta dello studente: {}", e.getMessage(), e);
            mostraAlert("Errore", "Impossibile aggiungere lo studente: " + e.getMessage());
        }
    }

    private void modificaStudente(ActionEvent event) {
        log.debug("Tentativo di modifica studente {}", matricolaOriginale);

        String nome = nomeField.getText();
        String cognome = cognomeField.getText();
        String password = passwordField.getText();
        String confermaPassword = confermaPasswordField.getText();
        Studenti.AnnoAccademico annoCorso = annoCorsoComboBox.getValue();
        String corsoLaurea = corsoLaureaField.getText();
        Studenti.TipoLaurea tipologiaLaurea = tipologiaLaureaComboBox.getValue();

        if (nome == null || nome.trim().isEmpty() ||
            cognome == null || cognome.trim().isEmpty() ||
            annoCorso == null ||
            corsoLaurea == null || corsoLaurea.trim().isEmpty() ||
            tipologiaLaurea == null) {
            log.warn("Modifica studente fallita: campi obbligatori mancanti");
            mostraAlert("Campi mancanti", "Per favore, compila tutti i campi obbligatori.");
            return;
        }

        if ((password != null && !password.isEmpty()) || (confermaPassword != null && !confermaPassword.isEmpty())) {
            if (!password.equals(confermaPassword)) {
                log.warn("Modifica studente fallita: password non corrispondenti");
                mostraAlert("Errore Password", "Le password non corrispondono. Riprova.");
                return;
            }
        }

        try {
            Studenti studente = GestioneStudenti.getInstance().cercaStudente(matricolaOriginale);
            if (studente == null) {
                mostraAlert("Errore", "Studente non trovato.");
                return;
            }

            studente.setNome(nome.trim());
            studente.setCognome(cognome.trim());
            if (password != null && !password.isEmpty()) {
                studente.setPassword(password);
            }
            studente.setAnnoDiCorso(annoCorso);
            studente.setTipologiaLaurea(tipologiaLaurea);
            
            CorsiLaurea corso = new CorsiLaurea(corsoLaurea.trim(), corsoLaurea.trim().toUpperCase().replace(" ", "_"));
            studente.setCorsoDiLaurea(corso);

            GestioneStudenti.getInstance().salvaStudenti();

            log.info("Studente {} modificato con successo", matricolaOriginale);
            mostraAlert("Successo", "Studente " + nome + " " + cognome + " modificato con successo!");
            
            GestioneStudenti.getInstance().clearStudenteDaModificare();
            cambiaScena(event, "RicercaStudente.fxml");

        } catch (Exception e) {
            log.error("Errore durante la modifica dello studente: {}", e.getMessage(), e);
            mostraAlert("Errore", "Impossibile modificare lo studente: " + e.getMessage());
        }
    }

    private void precompilaCampi(Studenti studente) {
        nomeField.setText(studente.getNome());
        cognomeField.setText(studente.getCognome());
        annoCorsoComboBox.setValue(studente.getAnnoDiCorso());
        if (studente.getCorsoDiLaurea() != null) {
            corsoLaureaField.setText(studente.getCorsoDiLaurea().getNome());
        }
        tipologiaLaureaComboBox.setValue(studente.getTipologiaLaurea());
    }

    @FXML
    public void onRicercaStudente(ActionEvent event) {
        GestioneStudenti.getInstance().clearStudenteDaModificare();
        try {
            cambiaScena(event, "RicercaStudente.fxml");
        } catch (IOException e) {
            log.error("Errore cambio scena: {}", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile aprire Ricerca Studente.");
        }
    }

    @FXML
    public void onRicercaDocente(ActionEvent event) {
        GestioneStudenti.getInstance().clearStudenteDaModificare();
        try {
            cambiaScena(event, "RicercaDocente.fxml");
        } catch (IOException e) {
            log.error("Errore cambio scena: {}", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile aprire Ricerca Docente.");
        }
    }

    @FXML
    public void onRicercaAule(ActionEvent event) {
        GestioneStudenti.getInstance().clearStudenteDaModificare();
        try {
            cambiaScena(event, "RicercaAule.fxml");
        } catch (IOException e) {
            log.error("Errore cambio scena: {}", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile aprire Ricerca Aule.");
        }
    }

    @FXML
    public void onLogout(ActionEvent event) {
        GestioneStudenti.getInstance().clearStudenteDaModificare();
        try {
            cambiaScena(event, "NewLoginPage.fxml");
        } catch (IOException e) {
            log.error("Errore logout: {}", e.getMessage(), e);
            mostraAlert("Errore tecnico", "Impossibile tornare al login.");
        }
    }

    private void pulisciCampi() {
        nomeField.clear();
        cognomeField.clear();
        passwordField.clear();
        confermaPasswordField.clear();
        annoCorsoComboBox.setValue(null);
        corsoLaureaField.clear();
        tipologiaLaureaComboBox.setValue(null);
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
