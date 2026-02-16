# 🎓 University Management System (UniGest)

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge\&logo=openjdk\&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-UI-007396?style=for-the-badge\&logo=java\&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge\&logo=apache-maven\&logoColor=white)

> **Progetto finale per il corso di Laboratorio di Programmazione a Oggetti (LPO)**
> *Università degli Studi dell'Aquila*

---

## 📖 Descrizione

**UniGest** è un'applicazione desktop progettata per digitalizzare e gestire i processi accademici di un ateneo.
Il software simula un portale di segreteria completo, permettendo l'interazione tra le tre figure chiave dell'ecosistema universitario:

* 👨‍🎓 **Studenti**
* 👩‍🏫 **Docenti**
* 🛠 **Amministratori**

---

## ✨ Funzionalità Principali

Il sistema gestisce l'accesso sicuro tramite **login** e reindirizza l'utente a **dashboard personalizzate** in base al ruolo.

### 👨‍🎓 Area Studente

* **Prenotazione Esami**: visualizzazione degli appelli disponibili e iscrizione immediata
* **Libretto Digitale**: storico esami, voti verbalizzati, media ponderata e CFU acquisiti
* **Profilo**: gestione dati anagrafici e visualizzazione stato accademico

### 👩‍🏫 Area Docente

* **Gestione Appelli**: creazione, modifica e cancellazione degli esami
* **Verbalizzazione Voti**: inserimento degli esiti per gli studenti prenotati
* **Dashboard Corsi**: panoramica degli insegnamenti e degli iscritti

### 🛠 Area Amministratore (Segreteria)

* **Gestione Utenti**: CRUD completo per Studenti e Docenti
* **Gestione Didattica**: configurazione Corsi di Laurea e Insegnamenti
* **Gestione Logistica**: ricerca e gestione delle aule universitarie
* **Analisi**: monitoraggio degli insegnamenti attivi

---

## 🏗 Architettura e Stack Tecnologico

Il progetto **non utilizza database relazionali tradizionali**, ma implementa un
**sistema di persistenza custom basato su file JSON**, simulando un database NoSQL leggero e portabile.

### 🔧 Tecnologie Utilizzate

* **Linguaggio**: Java 
* **Interfaccia Grafica**: JavaFX 
* **Build System**: Apache Maven
* **Persistenza Dati**: JSON tramite **Jackson Databind**


### 🧩 Design Pattern

* **Singleton**: gestione condivisa delle risorse (es. `ViewDispatcher`)

---

## 📂 Struttura del Progetto

```text
it.univaq.disim.lpo.dominiouniversitario
├── core/            # Domain Model (Studente, Esame, Corso...)
├── controller/      # JavaFX Controllers
├── service/         # Business Logic
│   └── methods/     # Implementazioni (JSON parsing)
├── view/            # Scene management e ViewDispatcher
└── resources/
    ├── fxml/        # Layout JavaFX
    ├── styles.css   # Stile grafico
    └── *.json       # Persistenza dati (DB simulato)
```

---

## 🚀 Installazione e Avvio

### 1️⃣ Prerequisiti

Assicurati di avere installato:

* Java JDK 17 o superiore
* Apache Maven
* Git

### 2️⃣ Clona il Repository

```bash
git clone https://github.com/mattiaramondo/unigest.git
```

### 3️⃣ Compila il Progetto

```bash
mvn clean install
```

### 4️⃣ Avvia l’Applicazione

```bash
mvn javafx:run
```

---

## 🔑 Credenziali di Test (Demo)

Il sistema è preconfigurato con dati di esempio salvati nei file JSON.

| Ruolo              | Username | Password |
| ------------------ | -------- | -------- |
| **Amministratore** | admin    | admin    |
| **Docente**        | docente  | password |
| **Studente**       | studente | password |

> ⚠️ Le credenziali reali sono gestite nei file
> `studenti.json` e `docenti.json`

---

## 📌 Note Finali

Questo progetto è stato sviluppato a scopo **didattico** come prova finale del corso di
**Laboratorio di Programmazione a Oggetti**.

