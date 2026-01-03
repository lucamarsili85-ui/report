# Manual Testing Guide for DRAFT/FINAL State Management

This guide helps verify that the DRAFT/FINAL state management feature works correctly.

## Prerequisites
- Open `index.html` in a modern web browser
- Clear localStorage before testing: Open browser console and run `localStorage.clear()`

## Test Scenario 1: Create and Finalize Report (DRAFT → FINAL)

### Steps:
1. **Home Screen**
   - Click "Rapportino Giornaliero"
   - ✓ Should navigate to daily report screen

2. **Create Client Section (DRAFT mode)**
   - Enter "Costruzioni Rossi SRL" in Nome Cliente
   - Enter "Via Roma 10, Milano" in Località Cantiere
   - Click "Crea sezione cliente"
   - ✓ Client section should appear with green border
   - ✓ Should see "+ Aggiungi Macchina" and "+ Aggiungi Materiale" buttons
   - ✓ Should see delete button (🗑️) on client section

3. **Add Machine Activity**
   - Click "+ Aggiungi Macchina"
   - Enter "Escavatore CAT 320" for machine name
   - Enter "8.5" for hours
   - Enter "Scavo fondamenta" for description
   - ✓ Machine activity should appear in the client section
   - ✓ Total hours should update to 8.5
   - ✓ Delete button (×) should be visible on activity

4. **Add Material Activity**
   - Click "+ Aggiungi Materiale"
   - Enter "Cemento" for name
   - Enter "15.5" for quantity
   - Click OK for m³ unit
   - Enter "Per fondamenta" for notes
   - ✓ Material activity should appear in the client section
   - ✓ Delete button (×) should be visible on activity

5. **Finalize Report (DRAFT → FINAL)**
   - Click "Salva Rapportino Giornaliero"
   - Confirm the dialog
   - ✓ Alert: "Rapportino finalizzato! Ora è in modalità anteprima."
   - ✓ "Aggiungi Cliente" dashboard should be HIDDEN
   - ✓ "+ Aggiungi Macchina" and "+ Aggiungi Materiale" buttons should be HIDDEN
   - ✓ Delete buttons (🗑️ and ×) should be HIDDEN
   - ✓ Save button text should change to "Modifica Rapporto"
   - ✓ Report should still be visible (not navigated away)

## Test Scenario 2: Reopen Finalized Report (FINAL → DRAFT)

### Steps:
1. **From FINAL mode (continuing from Scenario 1)**
   - ✓ Verify in preview mode (no edit buttons)
   - Click "Modifica Rapporto"
   - Confirm the dialog
   - ✓ Alert: "Rapporto riaperto per modifica"
   - ✓ "Aggiungi Cliente" dashboard should be VISIBLE
   - ✓ "+ Aggiungi Macchina" and "+ Aggiungi Materiale" buttons should be VISIBLE
   - ✓ Delete buttons (🗑️ and ×) should be VISIBLE
   - ✓ Save button text should be "Salva Rapportino Giornaliero"

2. **Make Changes**
   - Add another machine or material
   - ✓ Changes should be saved immediately
   - ✓ Total hours should update if machine added

## Test Scenario 3: Edit from History

### Steps:
1. **Finalize a Report**
   - Create a report with at least one client and activity
   - Click "Salva Rapportino Giornaliero" and confirm
   - Navigate back to home (click ← button)

2. **View History**
   - Click "Storico"
   - ✓ Should see the finalized report in the list
   - ✓ Should see both "Visualizza" and "Modifica" buttons

3. **View Details (Modal)**
   - Click "Visualizza"
   - ✓ Modal should open with report details
   - ✓ All activities should be displayed
   - Close modal

4. **Edit from History**
   - Click "Modifica"
   - Confirm the dialog
   - ✓ Should navigate to daily report screen
   - ✓ Report should be in DRAFT mode (all edit buttons visible)
   - ✓ Report should be removed from history
   - ✓ Can add/remove activities

## Test Scenario 4: Progressive Save in DRAFT Mode

### Steps:
1. **Create Draft Report**
   - Start new report
   - Add a client section
   - Add one activity
   - ✓ Changes should save automatically

2. **Refresh Page**
   - Refresh the browser (F5)
   - Click "Rapportino Giornaliero"
   - ✓ Should resume with the same client and activity
   - ✓ Should still be in DRAFT mode

3. **Add More Activities**
   - Add another activity
   - ✓ Should save immediately
   - ✓ Total hours should update

## Expected UI Differences

### DRAFT Mode
- ✅ "Aggiungi Cliente" dashboard visible
- ✅ "+ Aggiungi Macchina" buttons visible
- ✅ "+ Aggiungi Materiale" buttons visible
- ✅ Delete buttons (🗑️) on client sections
- ✅ Delete buttons (×) on activities
- ✅ Button text: "Salva Rapportino Giornaliero" (green)

### FINAL Mode (Preview)
- ❌ "Aggiungi Cliente" dashboard HIDDEN
- ❌ "+ Aggiungi Macchina" buttons HIDDEN
- ❌ "+ Aggiungi Materiale" buttons HIDDEN
- ❌ Delete buttons (🗑️) HIDDEN
- ❌ Delete buttons (×) HIDDEN
- ✅ Button text: "Modifica Rapporto" (blue/primary color)
- ✅ All data visible in read-only format

## Browser Console Verification

Open browser console and check localStorage:
```javascript
// View current report
JSON.parse(localStorage.getItem('currentDailyReport'))

// Check status
JSON.parse(localStorage.getItem('currentDailyReport')).status
// Should be 'draft' or 'final'

// View saved reports
JSON.parse(localStorage.getItem('savedDailyReports'))
```

## Common Issues to Check

1. **State not persisting**: Check localStorage is enabled
2. **Buttons not hiding**: Verify `isPreviewMode` is set correctly
3. **Total hours wrong**: Check all machine activities have valid hour values
4. **Can't edit finalized**: Verify "Modifica Rapporto" button works

## Success Criteria

✅ All test scenarios pass without errors
✅ UI clearly distinguishes DRAFT from FINAL mode
✅ Progressive save works (reload doesn't lose data)
✅ Can finalize report and enter preview mode
✅ Can reopen finalized report for editing
✅ Can edit finalized reports from history
✅ State transitions work correctly: DRAFT ↔ FINAL
