package it.univaq.disim.lpo.dominiouniversitario.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneStudenti; 
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneDocenti;
import it.univaq.disim.lpo.dominiouniversitario.core.Studenti;
import it.univaq.disim.lpo.dominiouniversitario.core.Docenti;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField inputEmail; 
    @FXML
    private PasswordField inputPassword; 
    @FXML
    private Label errorLogin;

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @FXML
    public void initialize() {
        log.debug("LoginController avviato");
    }

    @FXML
    public void loginButton(ActionEvent event) {
        
        String email = inputEmail.getText();
        String password = inputPassword.getText();
        log.debug("Tentativo di login con email e password");

        if (email.isEmpty() || password.isEmpty()) {
            log.warn("Tentativo di login fallito a causa di campi vuoti");
            mostraAlert("Attenzione", "Per favore, inserisci email e password.");
            return;
        }

        if (email.endsWith("@student.univaq.it")) {
            Studenti studenteLoggato = GestioneStudenti.getInstance().verificaLogin(email, password);
            if (studenteLoggato != null) {
                log.info("Login dello studente {} completato", studenteLoggato.getNome());
                GestioneStudenti.getInstance().setProfiloStudente(studenteLoggato);
                try {
                    log.debug("Cambio scena verso Home Studente");
                    cambiaScenaInHome(event, "ProfiloStudente.fxml");
                } catch (IOException e) {
                    log.error("Errore durante il cambio scena: {}", e.getMessage(), e);
                    mostraAlert("Errore Tecnico", "Impossibile caricare la schermata successiva: " + e.getMessage());
                }
            } else {
                log.warn("Login studente fallito, riprovare");
                errorLogin.setText("Email o password errati. Riprova.");
            }
        } else if (email.endsWith("@univaq.it")) {
            Docenti docenteLoggato = GestioneDocenti.getInstance().verificaLogin(email, password);
            if (docenteLoggato != null) {
                log.info("Login del docente {} completato", docenteLoggato.getNome());
                GestioneDocenti.getInstance().setProfiloDocente(docenteLoggato);
                try {
                    log.debug("Cambio scena verso Home Docente");
                    cambiaScenaInHome(event, "HomeInsegnante.fxml");
                } catch (IOException e) {
                    log.error("Errore durante il cambio scena: {}", e.getMessage(), e);
                    mostraAlert("Errore Tecnico", "Impossibile caricare la schermata successiva: " + e.getMessage());
                }
            } else {
                log.warn("Login docente fallito, riprovare");
                errorLogin.setText("Email o password errati. Riprova.");
            }
        } else if (email.equals("admin")) {
            if (password.equals("admin")) {
                log.info("Login dell'admin completato");
                try {
                    log.debug("Cambio scena verso Home Admin");
                    cambiaScenaInHome(event, "HomeAdmin.fxml");
                } catch (IOException e) {
                    log.error("Errore durante il cambio scena: {}", e.getMessage(), e);
                    mostraAlert("Errore Tecnico", "Impossibile caricare la schermata successiva: " + e.getMessage());
                }
            } else {
                log.warn("Login admin fallito, riprovare");
                errorLogin.setText("Email o password errati. Riprova.");
            }
        } else {
            log.warn("Dominio email non riconosciuto");
            mostraAlert("Errore Accesso", "Dominio email non riconosciuto.");
        }
    }

    private void cambiaScenaInHome(ActionEvent event, String fxmlFileName) throws IOException {
        
        log.debug("Cambio scena verso {}", fxmlFileName);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFileName));
        Parent root = loader.load();
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
        log.info("Scena caricata correttamente");
    }

    private void mostraAlert(String title, String content) {
        log.warn("Visualizzazione alert: {} - {}", title, content);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}