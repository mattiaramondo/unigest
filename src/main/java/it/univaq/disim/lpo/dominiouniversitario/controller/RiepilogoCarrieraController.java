package it.univaq.disim.lpo.dominiouniversitario.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import it.univaq.disim.lpo.dominiouniversitario.core.Studenti;
import it.univaq.disim.lpo.dominiouniversitario.core.EsameSostenuto;
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneStudenti;
import java.io.IOException;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RiepilogoCarrieraController {
    
    @FXML
    private Label labelBenvenutoStudente;
    @FXML
    private Label labelCarrieraStudente;
    @FXML
    private Label labelTotaleCfu;
    @FXML
    private Label labelMediaPonderata;
    
    @FXML
    private Button profileButton;
    @FXML
    private Button careerButton;
    @FXML
    private Button examsButton;
    @FXML
    private Button esitoEsamiButton;

    @FXML
    private TableView<EsameSostenuto> tableviewTabellaEsami;

    @FXML
    private TableColumn<EsameSostenuto, String> columnCodice;
    @FXML
    private TableColumn<EsameSostenuto, String> columnEsame;
    @FXML
    private TableColumn<EsameSostenuto, Integer> columnCfu;
    @FXML
    private TableColumn<EsameSostenuto, String> columnVoto;
    @FXML
    private TableColumn<EsameSostenuto, Boolean> columnLode;
    @FXML
    private TableColumn<EsameSostenuto, LocalDate> columnData;

    private static final Logger log = LoggerFactory.getLogger(RiepilogoCarrieraController.class);

    private Studenti studenteCorrente;

    @FXML
    public void initialize() {
        log.debug("Inizializzazione RiepilogoCarrieraController");
        if (careerButton != null) {
            setActiveButton(careerButton);
        }
        
        studenteCorrente = GestioneStudenti.getInstance().getProfiloStudente();
        if (studenteCorrente != null) {
            String nomeCompleto = studenteCorrente.getNome() + " " + studenteCorrente.getCognome();
            labelBenvenutoStudente.setText("Benvenuto, " + nomeCompleto + "!");
            labelCarrieraStudente.setText("Carriera di " + nomeCompleto);
            
            int totaleCfu = GestioneStudenti.getInstance().getCfu(studenteCorrente);
            double mediaPonderata = GestioneStudenti.getInstance().getMediaPonderata(studenteCorrente);
            labelTotaleCfu.setText("Totale CFU: " + totaleCfu);
            labelMediaPonderata.setText("Media Ponderata: " + String.format("%.2f", mediaPonderata));
        } else {
            labelBenvenutoStudente.setText("Errore: Sessione utente non trovata.");
        }

        log.debug("Configurazione colonne tabella esami sostenuti");
        columnCodice.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getInsegnamento().getCodiceInsegnamento())
        );
        columnEsame.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getInsegnamento().getNome())
        );
        columnCfu.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getCfu()).asObject()
        );
        columnVoto.setCellValueFactory(cellData -> {
            int voto = cellData.getValue().getVoto();
            if (voto == 0) {
                return new SimpleStringProperty("Non Superato");
            } else {
                return new SimpleStringProperty(String.valueOf(voto));
            }
        });
        columnLode.setCellValueFactory(cellData -> 
            new SimpleBooleanProperty(cellData.getValue().isLode())
        );
        columnData.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getData())
        );

        if (studenteCorrente != null && studenteCorrente.getListaEsami() != null) {
            ObservableList<EsameSostenuto> esamiList = FXCollections.observableArrayList();
            java.util.Set<String> codiciAggiunti = new java.util.HashSet<>();
            
            for (EsameSostenuto esame : studenteCorrente.getListaEsami()) {
                if (esame.getVoto() >= 18) {
                    String codice = esame.getInsegnamento().getCodiceInsegnamento();
                    if (!codiciAggiunti.contains(codice)) {
                        esamiList.add(esame);
                        codiciAggiunti.add(codice);
                    }
                }
            }
            tableviewTabellaEsami.setItems(esamiList);
        } else {
            tableviewTabellaEsami.setItems(FXCollections.observableArrayList());
        }
    }
    
    private void setActiveButton(Button button) {
        if (profileButton != null) {
            profileButton.getStyleClass().remove("vbox-button-active");
            profileButton.setDisable(false);
        }
        if (careerButton != null) {
            careerButton.getStyleClass().remove("vbox-button-active");
            careerButton.setDisable(false);
        }
        if (examsButton != null) {
            examsButton.getStyleClass().remove("vbox-button-active");
            examsButton.setDisable(false);
        }
        
        if (button != null) {
            button.getStyleClass().add("vbox-button-active");
            button.setDisable(true);
        }
    }

    @FXML
    public void goToProfileStudente(ActionEvent event) {
        System.out.println("Navigazione al Profilo Studente");
        cambiaScena(event, "ProfiloStudente.fxml");
    }
    
    @FXML
    public void goToPrenotazioneEsami(ActionEvent event) {
        System.out.println("Navigazione a Prenotazione Esami");
        cambiaScena(event, "PrenotazioneEsami.fxml");
    }

    @FXML
    public void goToEsitoEsami(ActionEvent event) {
        System.out.println("Navigazione a Esito Esami");
        cambiaScena(event, "EsitoEsami.fxml");
    }
    
    private void cambiaScena(ActionEvent event, String fxmlFileName) {
        log.debug("Cambio scena verso {}", fxmlFileName);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFileName));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            log.info("Scena caricata correttamente");
            
        } catch (IOException e) {
            log.error("Errore nel caricamento della scena: {}", fxmlFileName, e);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        log.debug("Logout in corso");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NewLoginPage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            log.info("Logout completato con successo");
        } catch (IOException e) {
            log.error("Errore durante il logout: {}", e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
