package it.univaq.disim.lpo.dominiouniversitario.controller;

import it.univaq.disim.lpo.dominiouniversitario.core.Appello;
import it.univaq.disim.lpo.dominiouniversitario.core.EsameSostenuto;
import it.univaq.disim.lpo.dominiouniversitario.core.Studenti;
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneEsami;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerbalizzazioneVotoDialogController {

    @FXML
    private Label studentInfoLabel;
    @FXML
    private TextField votoField;
    @FXML
    private CheckBox lodeCheckBox;
    @FXML
    private Label messaggioLabel;
    @FXML
    private Button verbalizzaButton;
    @FXML
    private Button nonSuperatoButton;

    private static final Logger log = LoggerFactory.getLogger(VerbalizzazioneVotoDialogController.class);

    private Studenti studente;
    private Appello appello;
    private String idDocente;
    private Stage dialogStage;
    private boolean isConfirmed = false;

    @FXML
    public void initialize() {
        log.debug("Inizializzazione VerbalizzazioneVotoDialogController");
        lodeCheckBox.setDisable(true);
        votoField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue != null && !newValue.isEmpty()) {
                    int voto = Integer.parseInt(newValue);
                    if (voto == 30) {
                        lodeCheckBox.setDisable(false);
                    } else {
                        lodeCheckBox.setDisable(true);
                        lodeCheckBox.setSelected(false);
                    }
                } else {
                    lodeCheckBox.setDisable(true);
                    lodeCheckBox.setSelected(false);
                }
            } catch (NumberFormatException e) {
                lodeCheckBox.setDisable(true);
                lodeCheckBox.setSelected(false);
            }
        });
    }

    public void setDialogStage(Stage dialogStage) {
        log.debug("Salvataggio riferimento al dialog stage");
        this.dialogStage = dialogStage;

    }

    public void setDati(Studenti studente, Appello appello, String idDocente) {
        log.debug("Impostazione dati per verbalizzazione voto");
        this.studente = studente;
        this.appello = appello;
        this.idDocente = idDocente;
        studentInfoLabel.setText("Studente: " + studente.getNome() + " " + studente.getCognome() + " (" + studente.getMatricola() + ")");
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    @FXML
    private void handleVerbalizza(ActionEvent event) {
        log.debug("Salvataggio verbalizzazione voto");
        if (validateInput()) {
            int voto = Integer.parseInt(votoField.getText());
            boolean lode = lodeCheckBox.isSelected();

            EsameSostenuto esame = GestioneEsami.getInstance().registraVoto(
                idDocente,
                studente,
                appello,
                voto,
                lode
            );

            if (esame != null) {
                log.info("Verbalizzazione voto salvata con successo");
                isConfirmed = true;
                dialogStage.close();
            } else {
                messaggioLabel.setText("Errore durante la registrazione. Controlla la console.");
            }
        }
    }

    @FXML
    private void handleNonSuperato(ActionEvent event) {
        log.debug("Salvataggio verbalizzazione Non Superato");
        EsameSostenuto esame = GestioneEsami.getInstance().registraVoto(
            idDocente,
            studente,
            appello,
            0,
            false
        );

        if (esame != null) {
            log.info("Verbalizzazione Non Superato salvata con successo");
            isConfirmed = true;
            dialogStage.close();
        } else {
            messaggioLabel.setText("Errore durante la registrazione. Controlla la console.");
        }
    }

    @FXML
    private void handleAnnulla(ActionEvent event) {
        dialogStage.close();
    }

    private boolean validateInput() {
        log.debug("Validazione input per verbalizzazione voto");
        String votoText = votoField.getText();
        if (votoText == null || votoText.isEmpty()) {
            messaggioLabel.setText("Inserisci un voto.");
            return false;
        }
        try {
            int voto = Integer.parseInt(votoText);
            if (voto < 18 || voto > 30) {
                log.warn("Voto inserito non valido: {}", voto);
                messaggioLabel.setText("Il voto deve essere tra 18 e 30.");
                return false;
            }
            if (lodeCheckBox.isSelected() && voto != 30) {
                log.warn("Lode selezionata con voto non pari a 30: {}", voto);
                messaggioLabel.setText("La lode richiede voto 30.");
                return false;
            }
        } catch (NumberFormatException e) {
            log.warn("Voto inserito non è un numero valido: {}", votoText);
            messaggioLabel.setText("Voto non valido.");
            return false;
        }
        return true;
    }
}
