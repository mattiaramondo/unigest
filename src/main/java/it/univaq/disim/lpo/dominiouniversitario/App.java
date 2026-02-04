package it.univaq.disim.lpo.dominiouniversitario;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneStudenti;
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneDocenti;
import it.univaq.disim.lpo.dominiouniversitario.service.methods.GestioneAule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class App extends Application {

    private static final Logger log = LoggerFactory.getLogger(App.class);
    private static Scene scene;
    private static final String FXML_BASE_PATH = "/fxml/";

    @Override
    public void start(Stage stage) {
        try {
            log.debug("Avvio applicazione in corso...");
            
            GestioneStudenti.getInstance().initializeData();
            log.info("Dati studenti caricati in memoria: {} studenti.", 
                     GestioneStudenti.getInstance().getStudentiJson().size());

            GestioneDocenti.getInstance().initializeData();
            log.info("Dati docenti caricati in memoria: {} docenti.", 
                     GestioneDocenti.getInstance().getDocenti().size());

            GestioneAule.getInstance().initializeData();
            log.info("Dati aule caricati in memoria: {} aule.", 
                     GestioneAule.getInstance().getAule().size());

            String cssPath = getClass().getResource("/styles.css").toExternalForm();

            scene = new Scene(loadFXML("NewLoginPage"));
            scene.getStylesheets().add(cssPath);
            stage.setScene(scene);
            
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            
            stage.setTitle("Gestione Università");
            stage.show();
            stage.setOnCloseRequest(event -> {
                log.debug("Chiusura applicazione in corso...");
                
                GestioneStudenti.getInstance().saveData();
                GestioneDocenti.getInstance().saveData();
                GestioneAule.getInstance().saveData();
                
                log.info("Dati salvati correttamente. Applicazione terminata.");
            });
            
        } catch (IOException e) {
            log.error("Errore critico in corso: " + e.getMessage(), e);
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore Critico");
            alert.setHeaderText("Impossibile avviare l'applicazione");
            alert.setContentText("Errore durante il caricamento dei dati:" + e.getMessage());
            alert.showAndWait();
            System.exit(1);
        }
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        String fullPath = FXML_BASE_PATH + fxml + ".fxml";
        URL resourceUrl = App.class.getResource(fullPath);

        if (resourceUrl == null) {
            throw new IOException("Risorsa FXML non trovata nel classpath: " + fullPath);
        }
        
        FXMLLoader fxmlLoader = new FXMLLoader(resourceUrl);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}