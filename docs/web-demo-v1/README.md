# Daily Work Report - Web Demo

A self-contained web application for tracking daily work reports, built with plain HTML, CSS, and vanilla JavaScript.

## Features

- **Dashboard**: View weekly and monthly hours summaries plus recent reports
- **New Report Form**: Create reports with date, job site, machine, hours, notes, and materials
- **Materials Tracking**: Add/remove materials with name, quantity, unit, and optional notes
- **Job Site Autocomplete**: Type-ahead suggestions from previously entered job sites
- **Reports Archive**: Filter reports by date range, job site, or machine
- **LocalStorage Persistence**: All data persists in the browser (no backend required)

## How to Use

### Opening the Demo

Simply open `index.html` in any modern web browser:

1. Navigate to the `docs/web-demo/` directory
2. Double-click `index.html` or open it with your browser
3. Start creating work reports!

### Creating a Report

1. Click the **New Report** tab
2. Fill in the required fields:
   - **Date**: Select the work date (defaults to today)
   - **Job Site**: Enter the job site name (autocomplete will suggest previously used sites)
   - **Machine**: Enter the machine or equipment used
   - **Hours**: Enter hours worked (e.g., 8.5)
   - **Notes**: Optional notes about the work
3. Optionally add materials:
   - Click **+ Add Material**
   - Enter material name, quantity, unit (e.g., "m³", "kg"), and optional note
   - Add multiple materials or remove unwanted ones
4. Click **Save Report**

### Viewing Reports

- **Dashboard**: Shows this week's and this month's total hours, plus the 5 most recent reports
- **Archive**: View all reports with filtering options
  - Filter by date range (From/To dates)
  - Filter by job site name (partial match)
  - Filter by machine name (partial match)
  - Click **Apply Filters** to filter or **Clear** to reset

### Managing Data

- **Delete Reports**: Click the **Delete** button on any report card
- **LocalStorage**: All data is stored in your browser's LocalStorage
  - Data persists between sessions
  - Data is browser-specific (not synced across devices)
  - Clear browser data to reset the demo

## Technical Details

### File Structure

```
web-demo/
├── index.html   # Main HTML structure
├── styles.css   # All styles and responsive design
├── app.js       # Application logic and data management
└── README.md    # This file
```

### No Dependencies

This demo uses:
- ✅ Plain HTML5
- ✅ CSS3 with Flexbox and Grid
- ✅ Vanilla JavaScript (ES6+)
- ❌ No frameworks or libraries
- ❌ No build tools
- ❌ No external CDNs or assets

### Browser Compatibility

Works in all modern browsers that support:
- ES6 JavaScript
- LocalStorage API
- CSS Grid and Flexbox
- HTML5 form elements

Tested in: Chrome, Firefox, Safari, Edge

### Data Format

Reports are stored in LocalStorage as JSON with this structure:

```javascript
{
  id: 1234567890,
  date: 1704326400000,  // Unix timestamp
  jobSite: "Construction Site A",
  machine: "Excavator CAT 320",
  hoursWorked: 8.5,
  notes: "Completed foundation excavation",
  materials: [
    {
      name: "Concrete",
      quantity: 15.5,
      unit: "m³",
      note: "Grade C30"
    }
  ],
  createdAt: 1704326400000
}
```

### Responsive Design

- **Desktop** (1200px+): Full layout with side-by-side cards
- **Tablet** (768px-1024px): Adjusted grid layouts
- **Mobile** (< 768px): Single-column layout with stacked elements

## Limitations

- Data is stored locally (browser-specific, not synced)
- No user authentication
- No data export/import functionality
- No backend API integration
- Limited to browser LocalStorage capacity (~5-10MB)

## Future Enhancements

Potential improvements for a production version:
- Backend API integration
- User authentication
- Data export (PDF, CSV)
- Cloud sync across devices
- Advanced reporting and analytics
- Photo attachments
- Offline PWA support
- Print-friendly views

## License

This demo is part of the Daily Work Report App project.
