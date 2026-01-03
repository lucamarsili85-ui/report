# Rapportino Giornaliero - Mobile-First Progressive Diary

Una versione completamente ridisegnata dell'applicazione web per il tracciamento dei rapporti di lavoro giornalieri, ottimizzata per l'uso in cantiere con smartphone.

## Caratteristiche Principali

### Design Mobile-First
- **Ottimizzato per smartphone**: Touch target di almeno 48px per facile interazione anche con guanti
- **Alto contrasto**: Perfetto per ambienti esterni luminosi
- **Font grandi**: 16-24px per facile lettura
- **Tema verde**: Palette basata sul verde (#4CAF50) per una buona visibilità
- **Nessuna bottom navigation**: Solo freccia indietro in alto a sinistra

### Flusso Progressivo Giornaliero

#### 1. Punto d'Ingresso
Dalla Home → "Rapportino Giornaliero"

Schermata semplice con:
- Nome Cliente (input testo)
- Località Cantiere (input testo)
- Pulsante: "Crea sezione cliente"

#### 2. Sezioni Cliente
Ogni cliente viene visualizzato in una **card colorata** con:
- Bordo colorato distintivo (colore diverso per ogni cliente)
- Nome cliente + località cantiere
- Espandibile verticalmente (cresce con le attività)

Pulsanti in ogni sezione:
- "+ Aggiungi Macchina"
- "+ Aggiungi Materiale"

#### 3. Attività Macchina
Campi richiesti:
- Nome macchina
- Ore (input numerico manuale)
- Descrizione attività (opzionale)

Le voci vengono salvate immediatamente nella sezione cliente.

#### 4. Attività Materiale
Campi richiesti:
- Nome materiale
- Quantità (input numerico)
- Unità: m³ / ton (selezione rapida)
- Note opzionali

Include supporto per **trasporto attrezzature** (es. rullo spostato da A a B).
Le voci vengono salvate immediatamente nella sezione cliente.

#### 5. Diario Giornaliero Progressivo
La schermata funziona come un **diario live**:
- Tutti i clienti visibili in sequenza
- Ogni cliente mostra anteprima macchine e materiali salvati
- Le attività possono essere modificate o rimosse prima del salvataggio finale

#### 6. Salvataggio a Fine Giornata
Solo a fine giornata:
- Pulsante: "Salva Rapportino Giornaliero"

Questa azione:
- Blocca il rapporto
- Calcola il totale ore giornaliero
- Suddivisione per cliente
- Riepilogo macchine e materiali

## Come Usare

### Aprire la Demo
1. Naviga nella cartella `docs/web-demo-v2/`
2. Apri `index.html` in un browser moderno
3. Inizia a creare il rapporto giornaliero!

### Creare un Rapporto Giornaliero

1. **Home**: Tocca "Rapportino Giornaliero"

2. **Aggiungi Cliente**:
   - Inserisci il nome del cliente
   - Inserisci la località del cantiere
   - Tocca "Crea sezione cliente"

3. **Aggiungi Attività Macchina**:
   - Tocca "+ Aggiungi Macchina" nella sezione del cliente
   - Inserisci nome macchina (es. "Escavatore CAT 320")
   - Inserisci ore lavorate (es. "8.5")
   - Inserisci descrizione opzionale
   - L'attività viene aggiunta immediatamente

4. **Aggiungi Materiale**:
   - Tocca "+ Aggiungi Materiale" nella sezione del cliente
   - Inserisci nome materiale (es. "Cemento")
   - Inserisci quantità (es. "15.5")
   - Scegli unità (OK per m³, Annulla per tonnellate)
   - Inserisci note opzionali
   - Il materiale viene aggiunto immediatamente

5. **Aggiungi Altri Clienti**:
   - Ripeti i passi 2-4 per ogni cliente della giornata
   - Ogni cliente avrà un colore di bordo diverso

6. **Visualizza Totale Ore**:
   - Il totale ore viene calcolato automaticamente
   - Visibile in tempo reale sopra il pulsante di salvataggio

7. **Finalizza Giornata**:
   - Tocca "Salva Rapportino Giornaliero"
   - Conferma il salvataggio
   - Il rapporto viene bloccato e salvato nello storico

### Visualizzare i Rapporti
- **Storico**: Visualizza tutti i rapporti finalizzati
  - Tocca "Visualizza" per vedere i dettagli completi
  - Include data, clienti, ore totali, e tutte le attività

## Dettagli Tecnici

### Struttura File
```
web-demo-v2/
├── index.html   # Struttura HTML con schermate e modali
├── styles.css   # Stili CSS mobile-first
├── app.js       # Logica applicazione e gestione dati
└── README.md    # Questa documentazione
```

### Nessuna Dipendenza
- ✅ HTML5 puro
- ✅ CSS3 con Flexbox e Grid
- ✅ JavaScript vanilla (ES6+)
- ❌ Nessun framework o libreria
- ❌ Nessun tool di build
- ❌ Nessuna CDN esterna

### Compatibilità Browser
Funziona in tutti i browser moderni che supportano:
- JavaScript ES6
- LocalStorage API
- CSS Grid e Flexbox
- Elementi form HTML5

Testato in: Chrome, Firefox, Safari, Edge

### Formato Dati

#### DailyReport (Rapporto Giornaliero)
```javascript
{
  id: 1234567890,                  // ID univoco
  date: "2026-01-03",             // Data YYYY-MM-DD
  status: "draft",                // "draft" o "finalized"
  clients: [/* array ClientSection */],
  createdAt: 1704326400000,
  finalizedAt: 1704330000000,     // Solo per rapporti finalizzati
  totalHours: 16.0                // Calcolato al salvataggio
}
```

#### ClientSection (Sezione Cliente)
```javascript
{
  id: 1234567890.123,
  clientName: "Costruzioni Rossi SRL",
  jobSite: "Via Roma 10, Milano",
  colorClass: "color-1",          // Colore distintivo
  activities: [/* array Activity */]
}
```

#### Activity (Attività - Macchina o Materiale)
```javascript
// Macchina
{
  id: 1234567890.456,
  type: "machine",
  machine: "Escavatore CAT 320",
  hours: 8.5,
  description: "Scavo fondamenta",
  createdAt: 1704326400000
}

// Materiale
{
  id: 1234567890.789,
  type: "material",
  name: "Cemento",
  quantity: 15.5,
  unit: "m³",                     // "m³" o "ton"
  notes: "Per fondamenta",
  createdAt: 1704326400000
}
```

### Gestione Dati
- **Bozza corrente**: Salvata in `currentDailyReport` (localStorage)
- **Rapporti finalizzati**: Salvati in `savedDailyReports` (localStorage)
- **Salvataggio progressivo**: Ogni modifica viene salvata automaticamente
- **Finalizzazione**: Blocca il rapporto e lo sposta nello storico

## Differenze dalle Versioni Precedenti

### Workflow Completamente Ridisegnato
- **Multi-cliente in una giornata**: Un rapporto contiene più clienti
- **Salvataggio progressivo**: Tutto viene salvato man mano durante la giornata
- **Finalizzazione unica**: Solo un salvataggio finale a fine giornata
- **Nessuna bottom navigation**: Solo freccia indietro in alto

### Design Semplificato
- **Entry point semplice**: Solo nome cliente + località
- **Card clienti colorate**: Bordi colorati per distinguere visivamente
- **Input modali minimali**: Prompt nativi per massima velocità
- **Focus su mobile**: Touch target grandi, font leggibili

### Funzionalità Core
- ✅ **Dashboard entry semplice**: Nome + località → crea sezione
- ✅ **Sezioni cliente espandibili**: Crescono con le attività
- ✅ **Input numerico manuale**: Nessun +/- per le ore
- ✅ **Toggle unità m³/ton**: Selezione rapida tramite confirm
- ✅ **Anteprima ore totali**: Calcolo automatico in tempo reale
- ✅ **Diario progressivo live**: Vedi tutto prima del salvataggio
- ✅ **Modifica prima del salvataggio**: Rimuovi attività/clienti
- ✅ **Storico rapporti**: Visualizza rapporti finalizzati
- ✅ **Persistenza localStorage**: Dati salvati localmente
- ✅ **Conferme obbligatorie**: Dialogo per eliminazioni e salvataggio

## Requisiti Soddisfatti

Questo design implementa esattamente i requisiti specificati:

- ✅ Mobile-first UI (touch target 48px, layout pulito)
- ✅ Palette verde
- ✅ Nessuna bottom navigation (solo freccia indietro)
- ✅ Lingua italiana
- ✅ Multi-client workflow
- ✅ Progressive daily diary
- ✅ Attività macchina (nome, ore manuali, descrizione)
- ✅ Attività materiale (nome, quantità, unità toggle, note)
- ✅ Sezioni cliente con bordi colorati
- ✅ Salvataggio progressivo
- ✅ Finalizzazione a fine giornata
- ✅ Calcolo totale ore
- ✅ Visualizzazione storico

## Limitazioni
- Dati salvati localmente (specifici del browser, non sincronizzati)
- Nessuna autenticazione utente
- Nessuna funzionalità export PDF
- Nessuna integrazione API backend
- Limitato alla capacità localStorage del browser (~5-10MB)
- Modifica rapporti finalizzati non disponibile (come da requisiti)

## Prossimi Sviluppi (Fuori Scope)
- Vista calendario mensile
- Modulo manutenzioni
- Buoni di consegna
- Export PDF
- Backend con sincronizzazione cloud
- Autenticazione multi-utente

## Licenza
Questa demo fa parte del progetto Daily Work Report App.
