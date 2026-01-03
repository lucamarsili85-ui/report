# Rapporto Lavoro Giornaliero - Web Demo Mobile-First

Una versione mobile-first dell'applicazione web per il tracciamento dei rapporti di lavoro giornalieri, completamente in italiano.

## Caratteristiche principali

### Design mobile-first
- **Ottimizzato per dispositivi mobili**: Touch target di almeno 48px per facile interazione
- **Outdoor-friendly**: Alto contrasto, font grandi (16-24px), spaziatura generosa
- **Tema chiaro**: Colore primario verde (#4CAF50) per una buona visibilità in esterni
- **Responsive**: Si adatta a telefoni, tablet e desktop

### Navigazione
- **Bottom navigation** con 3 schede:
  - 📊 **Dashboard**: Visualizza ore settimanali e mensili, rapporti recenti
  - ➕ **Nuovo**: Crea un nuovo rapporto di lavoro
  - 📁 **Archivio**: Visualizza e filtra tutti i rapporti

- **Header bar** con barra di accento colorata:
  - Dashboard = blu
  - Nuovo = verde
  - Archivio = arancione

### Sistema di ruoli
- **Prima esecuzione**: Selezione ruolo "Autista" 🚛 o "Operatore" 👷
- **Comportamento materiali**:
  - **Autista**: Sezione materiali visibile per impostazione predefinita
  - **Operatore**: Sezione materiali nascosta con pulsante "Aggiungi materiali (opzionale)"
- **Impostazioni**: Cambia ruolo o cancella tutti i dati

### Nuovo Rapporto - Sezioni a passi
1. **Cantiere**: Data e nome cantiere con autocomplete
2. **Lavoro**: Macchina e ore lavorate con pulsanti +/- e input manuale
3. **Materiali**: Aggiungi materiali con nome, quantità, unità e note
4. **Note**: Note opzionali sul lavoro

### Funzionalità
- ✅ Autocomplete per cantieri (suggerimenti da rapporti precedenti)
- ✅ Input ore con grandi pulsanti +/- (incremento 0.5 ore)
- ✅ Gestione materiali dinamica
- ✅ Filtri archivio (data, cantiere, macchina)
- ✅ Persistenza localStorage
- ✅ Compatibilità dati con versione precedente

## Come usare

### Aprire la demo
1. Naviga nella cartella `docs/web-demo/`
2. Apri `index.html` in un browser moderno
3. Seleziona il tuo ruolo (Autista o Operatore)
4. Inizia a creare rapporti!

### Creare un rapporto
1. Tocca **Nuovo** nella barra di navigazione
2. Compila i campi obbligatori (*):
   - **Data**: Seleziona la data del lavoro
   - **Cantiere**: Digita il nome del cantiere (l'autocomplete suggerirà quelli usati prima)
   - **Macchina**: Inserisci la macchina o attrezzatura usata
   - **Ore lavorate**: Usa i pulsanti +/- o digita manualmente
3. Aggiungi materiali (opzionale):
   - Se sei Autista: la sezione è già visibile
   - Se sei Operatore: tocca "Aggiungi materiali (opzionale)"
   - Tocca "+ Aggiungi materiale" per aggiungere righe
4. Aggiungi note (opzionale)
5. Tocca **Salva rapporto**

### Visualizzare i rapporti
- **Dashboard**: Mostra ore totali settimanali e mensili + 5 rapporti più recenti
- **Archivio**: Visualizza tutti i rapporti con opzioni di filtro
  - Filtra per intervallo di date
  - Filtra per nome cantiere
  - Filtra per nome macchina

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
I rapporti sono salvati in localStorage come JSON con questa struttura:

```javascript
{
  id: 1234567890,
  date: 1704326400000,  // Unix timestamp
  jobSite: "Cantiere A",
  machine: "Escavatore CAT 320",
  hoursWorked: 8.5,
  notes: "Completato scavo fondamenta",
  materials: [
    {
      name: "Cemento",
      quantity: 15.5,
      unit: "m³",
      note: "Grado C30"
    }
  ],
  createdAt: 1704326400000
}
```

### Ruolo utente
Il ruolo è salvato separatamente in localStorage:
```javascript
localStorage.getItem('userRole') // 'autista' o 'operatore'
```

## Differenze dalla versione precedente (web-demo-v1)

### Design
- Layout mobile-first ottimizzato per touch
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
✅ Stessa struttura dati localStorage
✅ Stessa chiave storage: `dailyWorkReports`
✅ I dati creati nella versione precedente sono compatibili

## Limitazioni
- Dati salvati localmente (specifici del browser, non sincronizzati)
- Nessuna autenticazione utente
- Nessuna funzionalità export/import dati
- Nessuna integrazione API backend
- Limitato alla capacità localStorage del browser (~5-10MB)

## GitHub Pages
Questa demo è accessibile tramite GitHub Pages all'indirizzo:
`https://[username].github.io/report/web-demo/`

## Licenza
Questa demo fa parte del progetto Daily Work Report App.
