# 🎓 University Management System (UniGest)

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-UI-007396?style=for-the-badge&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![Architecture](https://img.shields.io/badge/Pattern-MVC-success?style=for-the-badge)

> **Progetto finale per il corso di Laboratorio di Programmazione a Oggetti (LPO)**
> *Università degli Studi dell'Aquila*

## 📖 Descrizione
**UniGest** è un'applicazione desktop standalone progettata per digitalizzare e gestire i processi accademici di un ateneo. Il software simula un portale di segreteria completo, permettendo l'interazione tra le tre figure chiave dell'ecosistema universitario: **Studenti**, **Docenti** e **Amministratori**.
---

## ✨ Funzionalità Principali

Il sistema gestisce l'accesso sicuro tramite login e reindirizza l'utente a dashboard personalizzate in base al ruolo (RBAC - Role Based Access Control).

### 👨‍🎓 Area Studente
* **Prenotazione Esami:** Visualizzazione degli appelli disponibili e iscrizione immediata.
* **Libretto Digitale:** Consultazione dello storico esami, voti verbalizzati, media ponderata e CFU acquisiti.
* **Profilo:** Gestione dei dati anagrafici e visualizzazione dello stato accademico.

### 👩‍🏫 Area Docente
* **Gestione Appelli:** Creazione, modifica e cancellazione delle date d'esame.
* **Verbalizzazione Voti:** Interfaccia dedicata per l'inserimento degli esiti per gli studenti iscritti.
* **Dashboard Corsi:** Panoramica degli insegnamenti assegnati e degli studenti iscritti ai corsi.

### 🛠 Area Amministratore (Segreteria)
* **Gestione Utenti:** CRUD completo (Create, Read, Update, Delete) per profili Studenti e Docenti.
* **Gestione Didattica:** Configurazione dei Corsi di Laurea, Insegnamenti e assegnazione cattedre.
* **Gestione Logistica:** Ricerca e gestione delle aule universitarie.
* **Analisi:** Monitoraggio degli insegnamenti attivi.

---

## 🏗 Architettura e Stack Tecnologico

Il progetto non utilizza database relazionali tradizionali, ma implementa un **sistema di persistenza custom basato su file JSON**, gestito tramite la libreria Jackson. Questo simula un database NoSQL leggero e portabile.

* **Linguaggio:** Java SE 17
* **Interfaccia Grafica:** JavaFX (con definizione layout in FXML e styling CSS).
* **Design Pattern:**
    * **MVC:** Separazione netta tra logica di business, dati e interfaccia.
    * **Singleton:** Per la gestione condivisa delle risorse (es. `ViewDispatcher`).
    * **DAO/Service Layer:** Astrazione delle operazioni di lettura/scrittura dati.
* **Persistenza Dati:** Serializzazione/Deserializzazione JSON tramite `Jackson Databind`.
* **Logging:** Tracciamento operazioni e debugging tramite `SLF4J` e `Logback`.
* **Build System:** Apache Maven.

### 📂 Struttura del Progetto
```text
it.univaq.disim.lpo.dominiouniversitario
├── core/            # Domain Model (Entità: Studente, Esame, Corso...)
├── controller/      # JavaFX Controllers (Gestione eventi UI)
├── service/         # Business Logic (Interfacce e implementazioni)
│   └── methods/     # Implementazione logica di gestione (JSON parsing)
├── view/            # Gestione Scene, ViewDispatcher e FXML Loader
└── resources/
    ├── fxml/        # File descrittivi dell'interfaccia grafica
    ├── styles.css   # Foglio di stile personalizzato
    └── *.json       # File di persistenza dati (DB simulato)
