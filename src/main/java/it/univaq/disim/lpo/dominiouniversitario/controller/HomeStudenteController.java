package it.univaq.disim.lpo.dominiouniversitario.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.univaq.disim.lpo.dominiouniversitario.core.Studenti;
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneStudenti;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeStudenteController implements Initializable {

    @FXML
    protected Label labelBenvenutoStudente;
    @FXML
    protected Label labelNome;
    @FXML
    protected Label labelCognome;
    @FXML
    protected Label labelEmail;
    @FXML
    protected Label labelMatricola;
    @FXML
    protected Label labelPrimaImmatricolazione;
    @FXML
    protected Label labelAnnoDiCorso;
    @FXML
    protected Label labelCorsoDiLaurea;
    @FXML
    protected Label labelTipologiaLaurea;
    @FXML
    protected Label labelCreditiStudente;
    @FXML
    protected Label labelMediaPonderata;
    @FXML
    protected Button profileButton;
    @FXML
    protected Button careerButton;
    @FXML
    protected Button examsButton;

    private Studenti studenteCorrente;
    
    private Button activeButton;

    private static final Logger log = LoggerFactory.getLogger(HomeStudenteController.class);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        log.debug("HomeStudenteController avviato");
        if (profileButton != null) {
            setActiveButton(profileButton);
        }
        
        log.debug("Recupero dati singleton dello studente");
        studenteCorrente = GestioneStudenti.getInstance().getProfiloStudente();
        if (studenteCorrente != null) {
            
            String nomeCompleto = studenteCorrente.getNome() + " " + studenteCorrente.getCognome();
            labelBenvenutoStudente.setText("Benvenuto, " + nomeCompleto + "!");
            log.debug("Popolamento etichette con i dati dello studente: {}", nomeCompleto);

            labelNome.setText(studenteCorrente.getNome());
            labelCognome.setText(studenteCorrente.getCognome());
            labelEmail.setText(studenteCorrente.getEmail());
            labelMatricola.setText(studenteCorrente.getMatricola());
            labelPrimaImmatricolazione.setText("2022/2023");
            labelAnnoDiCorso.setText(String.valueOf(studenteCorrente.getAnnoDiCorso()));
            labelCorsoDiLaurea.setText(studenteCorrente.getCorsoDiLaurea().toString());
            labelTipologiaLaurea.setText(studenteCorrente.getTipologiaLaurea().toString());
            
            int totaleCfu = GestioneStudenti.getInstance().getCfu(studenteCorrente);
            double mediaPonderata = GestioneStudenti.getInstance().getMediaPonderata(studenteCorrente);
            
            labelCreditiStudente.setText(String.valueOf(totaleCfu));
            labelMediaPonderata.setText(String.format("%.2f", mediaPonderata));
            
            log.debug("Dati studente caricati per: {}", studenteCorrente.getMatricola());
            
        } else {
            log.error("Dati studente non caricati correttamente");
            labelBenvenutoStudente.setText("Errore: Sessione utente non trovata.");
        }
    }
    
    private void setActiveButton(Button button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("vbox-button-active");
            activeButton.setDisable(false);
        }
        
        activeButton = button;
        activeButton.getStyleClass().add("vbox-button-active");
        activeButton.setDisable(true);
    }
    
    public void setActiveButtonFromChild(Button button) {
        setActiveButton(button);
    }    

    @FXML
    public void goToRiepilogoCarriera(ActionEvent event) {
        log.debug("Navigazione a Riepilogo Carriera per {}", studenteCorrente.getNome());
        cambiaScena(event, "RiepilogoCarriera.fxml");
    }

    @FXML
    public void goToPrenotazioneEsami(ActionEvent event) {
        log.debug("Navigazione a Prenotazione Esami per {}", studenteCorrente.getNome());
        cambiaScena(event, "PrenotazioneEsami.fxml");
    }

    @FXML
    public void goToEsitoEsami(ActionEvent event) {
        log.debug("Navigazione a Esito Esami per {}", studenteCorrente.getNome());
        cambiaScena(event, "EsitoEsami.fxml");
    }

    private void cambiaScena(ActionEvent event, String fxmlFileName) {
        try {
            log.debug("Cambio scena verso {}", fxmlFileName);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFileName));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            log.info("Scena caricata correttamente:");
            
        } catch (IOException e) {
            log.error("Errore nel caricamento della scena: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            log.debug("Logout iniziato per {}", studenteCorrente.getNome());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NewLoginPage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            log.info("Logout effettuato, ritorno alla pagina di login");
        } catch (IOException e) {
            log.error("Errore durante il logout: {}", e.getMessage(), e);
        }
    }
}