# Rapporto Lavoro Giornaliero - Web Demo Mobile-First

Una versione mobile-first dell'applicazione web per il tracciamento dei rapporti di lavoro giornalieri, completamente in italiano.

## Caratteristiche principali

### Design mobile-first
- **Ottimizzato per dispositivi mobili**: Touch target di almeno 48px per facile interazione
- **Outdoor-friendly**: Alto contrasto, font grandi (16-24px), spaziatura generosa
- **Tema chiaro**: Colore primario verde (#4CAF50) per una buona visibilità in esterni
- **Responsive**: Si adatta a telefoni, tablet e desktop

### Navigazione
- **Home Page**: Caselle grandi e touch-friendly per accedere alle sezioni principali:
  - 📝 **Rapportino**: Diario giornaliero con gestione clienti e attività
  - 📋 **Buono di consegna**: Placeholder (prossimamente)
  - 📅 **Calendario**: Placeholder (prossimamente)
  - 🔧 **Manutenzioni**: Placeholder (prossimamente)
  - 📁 **Storico**: Visualizza e filtra tutti i rapporti salvati

- **Bottom navigation** con 3 schede:
  - 🏠 **Home**: Pagina principale con accesso rapido alle sezioni
  - 📝 **Rapportino**: Diario giornaliero
  - 📁 **Storico**: Archivio rapporti

- **Header bar** con barra di accento colorata:
  - Home = verde
  - Rapportino = blu
  - Storico = arancione

### Sistema di ruoli
- **Prima esecuzione**: Selezione ruolo "Autista" 🚛 o "Operatore" 👷
- **Comportamento materiali**:
  - **Autista**: Sezione materiali visibile per impostazione predefinita
  - **Operatore**: Sezione materiali nascosta con pulsante "Aggiungi materiali (opzionale)"
- **Impostazioni**: Cambia ruolo o cancella tutti i dati

### Rapportino (Diario Giornaliero)
- **Gestione multi-cliente**: Aggiungi più clienti nella stessa giornata
- **Card colorate per cliente**: Ogni cliente ha una card con bordo colorato e badge località
- **Dati cliente**:
  - Nome Cliente
  - Nome Cantiere
  - Località Cantiere
- **Voci per cliente**: Ogni cliente può avere più voci di tre tipi:
  1. **Attività**: Macchina, ore lavorate (input numerico con tastiera decimale), note
  2. **Materiale**: Nome, quantità, unità (m³ o ton con selezione rapida), località da/a
  3. **Movimento Veicolo**: Veicolo, metodo trasporto, località da/a, note
- **Anteprima ore totali**: Calcolo automatico delle ore giornaliere
- **Finalizza giornata**: Salva il rapporto completo

### Funzionalità
- ✅ **Home page con sezioni touch-friendly**: Accesso rapido a tutte le funzioni
- ✅ **Multi-cliente giornaliero**: Gestisci più clienti nella stessa giornata
- ✅ **Card cliente colorate**: Visualizzazione chiara con bordi colorati e badge località
- ✅ **Input numerico per ore**: Tastiera numerica dedicata (inputmode="decimal")
- ✅ **Selezione unità m³/ton**: Radio buttons per materiali
- ✅ **Tre tipi di voce**: Attività, Materiali, Movimento Veicolo
- ✅ **Conferma eliminazione**: Dialogo di conferma per tutte le operazioni di eliminazione
- ✅ **Filtri archivio**: Filtra per data e cliente/cantiere
- ✅ **Persistenza localStorage**: Dati salvati localmente
- ✅ **IDs univoci**: Ogni giornata, cliente e voce ha un ID univoco

## Come usare

### Aprire la demo
1. Naviga nella cartella `docs/web-demo/`
2. Apri `index.html` in un browser moderno
3. Seleziona il tuo ruolo (Autista o Operatore)
4. Inizia a creare rapporti!

### Creare un rapporto
1. Tocca **Rapportino** nella home page o nella barra di navigazione
2. Tocca **+ Aggiungi Cliente** per aggiungere un nuovo cliente
3. Compila i dati del cliente:
   - **Nome Cliente**: Nome dell'azienda o persona
   - **Nome Cantiere**: Nome o descrizione del cantiere
   - **Località Cantiere**: Città o indirizzo (appare nel badge)
4. Tocca **+ Aggiungi Voce** e seleziona il tipo:
   - **Attività**: Per ore lavorate con macchina
   - **Materiale**: Per materiali utilizzati o trasportati
   - **Movimento Veicolo**: Per spostamenti mezzi
5. Compila i campi della voce selezionata
6. Ripeti i passi 4-5 per aggiungere più voci
7. Ripeti i passi 2-6 per aggiungere più clienti
8. Tocca **Finalizza Giornata** per salvare il rapporto completo

### Visualizzare i rapporti
- **Home**: Mostra statistiche ore settimanali e mensili
- **Storico**: Visualizza tutti i rapporti finalizzati
  - Filtra per intervallo di date
  - Filtra per nome cliente o cantiere
  - Tocca "Visualizza" per vedere i dettagli completi di un rapporto

### Gestire le impostazioni
1. Tocca l'icona ⚙️ in alto a destra
2. Visualizza il tuo ruolo corrente
3. Cambia ruolo o cancella tutti i dati

## Dettagli tecnici

### Struttura file
```
web-demo/
├── index.html   # Struttura HTML con modali e schermate
├── styles.css   # Stili CSS mobile-first
├── app.js       # Logica applicazione e gestione dati
└── README.md    # Questa documentazione
```

### Nessuna dipendenza
- ✅ HTML5 puro
- ✅ CSS3 con Flexbox e Grid
- ✅ JavaScript vanilla (ES6+)
- ❌ Nessun framework o libreria
- ❌ Nessun tool di build
- ❌ Nessuna CDN esterna

### Compatibilità browser
Funziona in tutti i browser moderni che supportano:
- JavaScript ES6
- LocalStorage API
- CSS Grid e Flexbox
- Elementi form HTML5

Testato in: Chrome, Firefox, Safari, Edge

### Formato dati

#### DraftDay (Giornata in bozza)
```javascript
{
  id: 1234567890.123,           // ID univoco
  date: "2026-01-03",           // Data YYYY-MM-DD
  role: "autista",              // Ruolo utente
  status: "draft",              // "draft" o "closed"
  clients: [/* array ClientSection */],
  createdAt: 1704326400000
}
```

#### ClientSection (Sezione cliente)
```javascript
{
  id: 1234567890.456,           // ID univoco
  clientName: "Costruzioni Rossi SRL",
  jobSiteName: "Cantiere Via Roma",
  jobSiteLocation: "Milano",
  entries: [/* array Entry */]
}
```

#### Entry (Voce: Attività, Materiale, o Movimento)
```javascript
// Attività
{
  id: 1234567890.789,
  type: "activity",
  data: {
    machine: "Escavatore CAT 320",
    hours: 8.0,
    notes: "Scavo fondamenta"
  },
  createdAt: 1704326400000
}

// Materiale
{
  id: 1234567890.101,
  type: "material",
  data: {
    name: "Cemento",
    quantity: 15.5,
    unit: "mc",  // "mc" (m³) o "ton"
    fromLocation: "Deposito A",
    toLocation: "Cantiere Via Roma"
  },
  createdAt: 1704326400000
}

// Movimento Veicolo
{
  id: 1234567890.112,
  type: "vehicleMovement",
  data: {
    vehicle: "Camion IVECO 450",
    transportMethod: "Strada",
    fromLocation: "Deposito",
    toLocation: "Cantiere",
    notes: "Trasporto materiali"
  },
  createdAt: 1704326400000
}
```

#### SavedReport (Rapporto finalizzato)
```javascript
{
  id: 1234567890.123,           // Stesso ID della DraftDay
  date: "2026-01-03",
  role: "autista",
  clients: [/* array ClientSection */],
  totalHours: 16.0,             // Calcolato automaticamente
  createdAt: 1704326400000,
  finalizedAt: 1704330000000
}
```

### Ruolo utente
Il ruolo è salvato separatamente in localStorage:
```javascript
localStorage.getItem('userRole') // 'autista' o 'operatore'
```

## Differenze dalla versione precedente (web-demo-v1)

### Design e Navigazione
- **Home page centralizzata**: Nuova pagina iniziale con card grandi per accesso alle sezioni
- **Struttura semplificata**: Home, Rapportino, Storico invece di Dashboard, Nuovo, Archivio
- **Card colorate per cliente**: Ogni cliente ha bordo colorato univoco e badge località
- **Placeholder sezioni future**: Buono di consegna, Calendario, Manutenzioni visibili ma disabilitati

### Modello Dati
- **Gestione multi-cliente giornaliera**: Un rapporto può contenere più clienti
- **IDs univoci gerarchici**: Giornata → Cliente → Voce
- **Tre tipi di voci**: Attività, Materiale, Movimento Veicolo (invece di solo materiali)
- **Struttura più ricca**: Dati cliente completi (nome, cantiere, località)

### Funzionalità
- **Input numerico ottimizzato**: `inputmode="decimal"` per tastiera numerica su mobile
- **Unità m³/ton**: Selezione rapida con radio buttons
- **Località per materiali**: Campi "Da" e "A" per tracciare spostamenti
- **Conferme obbligatorie**: Dialogo di conferma per tutte le eliminazioni
- **Anteprima ore totali**: Calcolo automatico visibile in tempo reale
- Navigazione bottom invece di tab in alto
- Barra di accento colorata nell'header
- Font più grandi e spaziatura generosa
- Tema verde invece di viola

### Funzionalità nuove
- Sistema di ruoli (Autista/Operatore)
- Comportamento materiali condizionale per ruolo
- Pulsanti +/- per input ore
- Sezioni a passi nel form Nuovo
- Modal impostazioni
- Interfaccia completamente in italiano

### Compatibilità dati
⚠️ **Struttura dati completamente rinnovata**
- Nuovo modello dati con giornate, clienti multipli e voci strutturate
- Chiavi storage: `draftDays` (bozze) e `savedReports` (finalizzati)
- Non compatibile con i dati della versione precedente
- Per migrare: esportare i dati vecchi e reinserirli manualmente

## Limitazioni
- Dati salvati localmente (specifici del browser, non sincronizzati)
- Nessuna autenticazione utente
- Nessuna funzionalità export PDF (specificato nei requisiti ma non implementato)
- Nessuna funzionalità import/export dati
- Nessuna integrazione API backend
- Limitato alla capacità localStorage del browser (~5-10MB)
- Modifiche ai rapporti finalizzati non ancora implementate

## GitHub Pages
Questa demo è accessibile tramite GitHub Pages all'indirizzo:
`https://[username].github.io/report/web-demo/`

## Licenza
Questa demo fa parte del progetto Daily Work Report App.
