package it.univaq.disim.lpo.dominiouniversitario.controller;

import it.univaq.disim.lpo.dominiouniversitario.core.Appello;
import it.univaq.disim.lpo.dominiouniversitario.core.Docenti;
import it.univaq.disim.lpo.dominiouniversitario.core.Insegnamenti;
import it.univaq.disim.lpo.dominiouniversitario.core.AuleEdifici;
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneDocenti;
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneAule;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggiungiAppelloDialogController {

    @FXML
    private Label titleLabel;
    @FXML
    private ComboBox<Insegnamenti> insegnamentoComboBox;
    @FXML
    private DatePicker dataEsamePicker;
    @FXML
    private DatePicker dataAperturaPicker;
    @FXML
    private DatePicker dataChiusuraPicker;
    @FXML
    private TextField maxStudentiField;
    @FXML
    private ComboBox<AuleEdifici> aulaComboBox;
    @FXML
    private ComboBox<Appello.StatoAppello> statoComboBox;
    @FXML
    private Button btnAnnulla;
    @FXML
    private Button btnSalva;

    private Stage dialogStage;
    private Appello appelloCreato;
    private Appello appelloEsistente;
    private boolean okClicked = false;

    private static final Logger log = LoggerFactory.getLogger(AggiungiAppelloDialogController.class);

    @FXML
    public void initialize() {
        log.debug("Inizializzazione AggiungiAppelloDialogController");
        Docenti docente = GestioneDocenti.getInstance().getProfiloDocente();
        if (docente != null) {
            java.util.List<Insegnamenti> insegnamenti = docente.getInsegnamenti();
            
            it.univaq.disim.lpo.dominiouniversitario.core.CorsiLaurea filtro = GestioneDocenti.getInstance().getCorsoSelezionato();
            if (filtro != null) {
                insegnamenti = insegnamenti.stream()
                    .filter(i -> i.getCorsoDiLaurea() != null && i.getCorsoDiLaurea().equals(filtro))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            insegnamentoComboBox.setItems(FXCollections.observableArrayList(insegnamenti));
            
            insegnamentoComboBox.setConverter(new StringConverter<Insegnamenti>() {
                @Override
                public String toString(Insegnamenti insegnamento) {
                    return insegnamento != null ? insegnamento.getNome() : "";
                }

                @Override
                public Insegnamenti fromString(String string) {
                    return null;
                }
            });
            
            statoComboBox.setItems(FXCollections.observableArrayList(Appello.StatoAppello.values()));
            statoComboBox.setValue(Appello.StatoAppello.APERTO);
            
            java.util.List<AuleEdifici> aule = GestioneAule.getInstance().getAule();
            aulaComboBox.setItems(FXCollections.observableArrayList(aule));
            
            aulaComboBox.setConverter(new StringConverter<AuleEdifici>() {
                @Override
                public String toString(AuleEdifici aula) {
                    return aula != null ? aula.getNomeAula() + " (" + aula.getCapienza() + " posti)" : "";
                }

                @Override
                public AuleEdifici fromString(String string) {
                    return null;
                }
            });
            
            aulaComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    maxStudentiField.setText(String.valueOf(newValue.getCapienza()));
                    log.debug("Aula selezionata: {} - Capienza: {}", newValue.getNomeAula(), newValue.getCapienza());
                }
            });
        }
    }

    public void setDialogStage(Stage dialogStage) {
        log.debug("Impostazione dialogStage in AggiungiAppelloDialogController");
        this.dialogStage = dialogStage;
    }

    public void setAppello(Appello appello) {
        log.debug("Impostazione appello esistente in AggiungiAppelloDialogController");
        this.appelloEsistente = appello;
        if (appello != null) {
            titleLabel.setText("Modifica Appello");
            insegnamentoComboBox.setValue(appello.getInsegnamento());
            insegnamentoComboBox.setDisable(true); 
            dataEsamePicker.setValue(appello.getDataEsame());
            dataAperturaPicker.setValue(appello.getDataApertura());
            dataChiusuraPicker.setValue(appello.getDataChiusuraIscrizione());
            maxStudentiField.setText(String.valueOf(appello.getNumeroMassimoStudenti()));
            
            String luogoAppello = appello.getLuogo();
            if (luogoAppello != null && !luogoAppello.isEmpty()) {
                AuleEdifici aulaCorrispondente = GestioneAule.getInstance().getAulaByNome(luogoAppello);
                if (aulaCorrispondente != null) {
                    aulaComboBox.setValue(aulaCorrispondente);
                }
            }
            
            statoComboBox.setValue(appello.getStato());
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public Appello getAppelloCreato() {
        return appelloCreato;
    }

    @FXML
    private void handleSalva(ActionEvent event) {
        if (isInputValid()) {
            Insegnamenti insegnamento = insegnamentoComboBox.getValue();
            LocalDate dataEsame = dataEsamePicker.getValue();
            LocalDate dataApertura = dataAperturaPicker.getValue();
            LocalDate dataChiusura = dataChiusuraPicker.getValue();
            int maxStudenti = Integer.parseInt(maxStudentiField.getText());
            AuleEdifici aulaSelezionata = aulaComboBox.getValue();
            String luogo = aulaSelezionata != null ? aulaSelezionata.getNomeAula() : "";
            Appello.StatoAppello stato = statoComboBox.getValue();

            if (appelloEsistente != null) {
                appelloEsistente.setInsegnamento(insegnamento);
                appelloEsistente.setDataEsame(dataEsame);
                appelloEsistente.setDataApertura(dataApertura);
                appelloEsistente.setDataChiusuraIscrizione(dataChiusura);
                appelloEsistente.setNumeroMassimoStudenti(maxStudenti);
                appelloEsistente.setLuogo(luogo);
                appelloEsistente.setStato(stato);
                appelloCreato = appelloEsistente;
            } else {
                log.info("Creazione nuovo appello in AggiungiAppelloDialogController");
                appelloCreato = new Appello(insegnamento, dataEsame, dataApertura, dataChiusura, maxStudenti, luogo);
                appelloCreato.setStato(stato);
            }
            
            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleAnnulla(ActionEvent event) {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (insegnamentoComboBox.getValue() == null) {
            log.warn("Insegnamento non selezionato");
            errorMessage += "Seleziona un insegnamento!\n";
        }
        
        LocalDate dataEsame = dataEsamePicker.getValue();
        LocalDate dataApertura = dataAperturaPicker.getValue();
        LocalDate dataChiusura = dataChiusuraPicker.getValue();

        if (dataEsame == null) {
            log.warn("Data esame non selezionata");
            errorMessage += "Seleziona la data dell'esame!\n";
        } else {
            if (dataEsame.isBefore(LocalDate.now())) {
                 errorMessage += "La data dell'esame non può essere nel passato!\n";
            }
        }

        if (dataApertura == null) {
            log.warn("Data apertura iscrizioni non selezionata");
            errorMessage += "Seleziona la data di apertura iscrizioni!\n";
        }

        if (dataChiusura == null) {
            log.warn("Data chiusura iscrizioni non selezionata");
            errorMessage += "Seleziona la data di chiusura iscrizioni!\n";
        }
        
        if (dataEsame != null && dataChiusura != null) {
            if (!dataChiusura.isBefore(dataEsame)) {
                errorMessage += "La chiusura iscrizioni deve avvenire prima della data dell'esame!\n";
            }
        }
        
        if (dataApertura != null && dataChiusura != null) {
            if (!dataApertura.isBefore(dataChiusura)) {
                errorMessage += "L'apertura iscrizioni deve avvenire prima della chiusura!\n";
            }
        }

        if (maxStudentiField.getText() == null || maxStudentiField.getText().length() == 0) {
            log.warn("Numero massimo studenti non inserito");
            errorMessage += "Inserisci il numero massimo di studenti!\n";
        } else {
            try {
                int max = Integer.parseInt(maxStudentiField.getText());
                if (max <= 0) {
                    errorMessage += "Il numero massimo di studenti deve essere maggiore di 0!\n";
                }
            } catch (NumberFormatException e) {
                errorMessage += "Numero massimo studenti non valido (deve essere un intero)!\n";
            }
        }

        if (aulaComboBox.getValue() == null) {
            log.warn("Aula non selezionata");
            errorMessage += "Seleziona un'aula per l'esame!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            log.warn("Dati di input non validi: \n" + errorMessage);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Campi non validi");
            alert.setHeaderText("Correggi i campi non validi");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
}
