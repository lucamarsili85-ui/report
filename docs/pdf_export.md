# PDF Export Specification for Work Reports

## Overview

This document describes the PDF layout, field mappings, and generation flow for exporting work reports to PDF format. The PDF export feature allows users to generate professional, printable work report documents suitable for sharing with clients, managers, and for record-keeping.

## PDF Layout & Structure

### 1. Header Section

**Position**: Top of the page  
**Content**:
- Company logo (optional, configurable)
- Report title: "Daily Work Report"
- Report ID and generation date/time
- Page numbers (e.g., "Page 1 of 2")

**Styling**:
- Background: Light gray or company primary color
- Font: Bold, 18-20pt for title
- Alignment: Centered or left-aligned based on logo placement

### 2. Job Site Information

**Position**: Below header  
**Fields**:
- **Date**: Work date formatted as "MMMM dd, yyyy" (e.g., "January 15, 2024")
- **Job Site**: Full job site name/location
- **Report ID**: Unique identifier for tracking

**Styling**:
- Section border or background color to distinguish from other sections
- Font: 14pt for labels, 12pt for values
- Layout: Key-value pairs, left-aligned

### 3. Crew & Equipment Section

**Position**: Below job site information  
**Fields**:
- **Machine/Equipment**: Type and ID of machinery used
- **Hours Worked**: Total hours with decimal precision (e.g., "8.5 hours")
- **Operator**: Name of machine operator (if available in extended model)

**Styling**:
- Grid or table layout
- Font: 12pt
- Border: Light borders between fields

### 4. Work Summary Section

**Position**: Middle section of the document  
**Fields**:
- **Work Performed**: Description of work completed
- **Notes**: Additional comments, observations, or important information
- **Progress Status**: Percentage complete or status indicator (if tracked)

**Styling**:
- Multi-line text area for notes
- Font: 11-12pt for body text
- Minimum height: 3-4 lines even if notes are short

### 5. Materials Table

**Position**: Below work summary  
**Purpose**: List all materials used during the work day

**Table Columns**:
| Column | Description | Width | Alignment |
|--------|-------------|-------|-----------|
| # | Sequential number | 5% | Center |
| Material Name | Name/description of material | 40% | Left |
| Quantity | Amount used (numeric) | 20% | Right |
| Unit | Unit of measurement (kg, m³, units, etc.) | 15% | Left |
| Notes | Additional material notes | 20% | Left |

**Table Styling**:
- Header row: Bold text, light background color
- Borders: Horizontal lines between rows
- Alternating row colors for better readability (optional)
- Font: 10-11pt
- Total row: If applicable, show sum of quantities (for materials with same unit)

**Empty State**:
- If no materials: Display "No materials recorded for this work day"
- Gray italic text, centered in the materials section

### 6. Notes & Safety Section

**Position**: Below materials table  
**Fields**:
- **Safety Incidents**: Any safety-related observations or incidents
- **Weather Conditions**: Weather during work (if tracked)
- **Special Instructions**: Any special notes or instructions
- **Additional Comments**: Free-form text area

**Styling**:
- Each subsection with bold label
- Font: 10-11pt
- Light border around section

### 7. Signature Block

**Position**: Bottom of document, above footer  
**Fields**:
- **Submitted by**: Name and signature line
- **Date Submitted**: Timestamp of report submission
- **Supervisor Approval**: Signature line for supervisor
- **Date Approved**: Date line for approval

**Styling**:
- Horizontal signature lines
- Font: 10pt
- Date format: "MM/dd/yyyy"
- Spacing: Adequate space for handwritten signatures (if printed)

### 8. Footer Section

**Position**: Bottom of each page  
**Content**:
- Document generated timestamp: "Generated on: [timestamp]"
- Disclaimer or confidentiality notice (optional)
- Company contact information (optional)

**Styling**:
- Small font: 8-9pt
- Gray text color
- Centered or split (left/right alignment for different elements)

## Field Mappings

Mapping between WorkReport domain model and PDF fields:

| Domain Field | PDF Section | PDF Field Name | Format/Transform |
|--------------|-------------|----------------|------------------|
| id | Header | Report ID | "WR-{id}" or similar prefix |
| date | Job Site Info | Date | SimpleDateFormat("MMMM dd, yyyy") |
| jobSite | Job Site Info | Job Site | Direct mapping |
| machine | Crew & Equipment | Machine/Equipment | Direct mapping |
| hoursWorked | Crew & Equipment | Hours Worked | "{value} hours" with 1 decimal place |
| notes | Work Summary | Notes | Direct mapping, handle empty as "N/A" |
| createdAt | Footer | Generated on | SimpleDateFormat("MM/dd/yyyy hh:mm a") |
| updatedAt | Header | Last Modified | SimpleDateFormat("MM/dd/yyyy hh:mm a") (optional) |
| materials | Materials Table | Materials Table | Iterate and format each Material |

### Material Field Mappings

| Material Field | Table Column | Format/Transform |
|----------------|--------------|------------------|
| name | Material Name | Direct mapping |
| quantity | Quantity | Number format with 2 decimal places |
| unit | Unit | Direct mapping |
| note | Notes | Direct mapping, empty if not provided |

## Styling Guidelines

### Color Scheme
- **Primary Color**: Company branding color or default blue (#2196F3)
- **Secondary Color**: Light gray for backgrounds (#F5F5F5)
- **Text Colors**: 
  - Primary text: Black (#000000)
  - Secondary text: Dark gray (#666666)
  - Labels: Medium gray (#888888)
- **Borders**: Light gray (#DDDDDD)

### Typography
- **Header Title**: Sans-serif, Bold, 18-20pt
- **Section Headers**: Sans-serif, Bold, 14pt
- **Labels**: Sans-serif, Regular, 11pt
- **Body Text**: Sans-serif, Regular, 10-12pt
- **Footer**: Sans-serif, Regular, 8-9pt

**Recommended Fonts**:
- Primary: Helvetica, Arial, or similar sans-serif
- Fallback: System default sans-serif

### Spacing & Layout
- **Page Margins**: 0.75 inches (1.9 cm) on all sides
- **Section Spacing**: 0.25 inches (0.6 cm) between sections
- **Line Spacing**: 1.2x for body text
- **Table Row Height**: Minimum 0.3 inches (0.76 cm)

### Page Settings
- **Page Size**: Letter (8.5" × 11") or A4 (210mm × 297mm)
- **Orientation**: Portrait
- **Headers/Footers**: Enabled on all pages

## Generation Flow

### High-Level Process

```
1. Retrieve WorkReport data from database/repository
   ↓
2. Initialize PDF document with page settings
   ↓
3. Render header section with report metadata
   ↓
4. Render job site information section
   ↓
5. Render crew & equipment section
   ↓
6. Render work summary with notes
   ↓
7. Render materials table (iterate materials list)
   ↓
8. Render notes & safety section
   ↓
9. Render signature block
   ↓
10. Render footer on all pages
   ↓
11. Finalize and save/share PDF document
```

### Implementation Steps

#### Step 1: Data Preparation
```kotlin
// Fetch the WorkReport by ID
val workReport = repository.getWorkReportById(reportId)

// Format dates
val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
val timestampFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault())
val formattedDate = dateFormat.format(Date(workReport.date))
val generatedTimestamp = timestampFormat.format(Date())
```

#### Step 2: PDF Library Initialization
```kotlin
// Example using common PDF libraries like iText or PDFDocument
val document = PdfDocument()
val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
val page = document.startPage(pageInfo)
val canvas = page.canvas
```

#### Step 3: Rendering Components
```kotlin
// Header
renderHeader(canvas, workReport.id, generatedTimestamp)

// Job Site Information
var yPosition = renderJobSiteInfo(canvas, formattedDate, workReport.jobSite, workReport.id)

// Crew & Equipment
yPosition = renderCrewEquipment(canvas, yPosition, workReport.machine, workReport.hoursWorked)

// Work Summary
yPosition = renderWorkSummary(canvas, yPosition, workReport.notes)

// Materials Table
yPosition = renderMaterialsTable(canvas, yPosition, workReport.materials)

// Signature Block
renderSignatureBlock(canvas, yPosition)

// Footer
renderFooter(canvas, generatedTimestamp)
```

#### Step 4: Materials Table Rendering
```kotlin
fun renderMaterialsTable(canvas: Canvas, startY: Float, materials: List<Material>): Float {
    if (materials.isEmpty()) {
        // Render empty state
        canvas.drawText("No materials recorded for this work day", x, y, emptyStatePaint)
        return startY + emptyStateHeight
    }
    
    // Render table header
    drawTableHeader(canvas, startY)
    
    // Render each material row
    var currentY = startY + headerHeight
    materials.forEachIndexed { index, material ->
        drawMaterialRow(canvas, currentY, index + 1, material)
        currentY += rowHeight
    }
    
    return currentY
}

fun drawMaterialRow(canvas: Canvas, y: Float, rowNumber: Int, material: Material) {
    // Draw row number
    canvas.drawText(rowNumber.toString(), x1, y, centerPaint)
    
    // Draw material name
    canvas.drawText(material.name, x2, y, leftPaint)
    
    // Draw quantity (formatted)
    val formattedQuantity = String.format("%.2f", material.quantity)
    canvas.drawText(formattedQuantity, x3, y, rightPaint)
    
    // Draw unit
    canvas.drawText(material.unit, x4, y, leftPaint)
    
    // Draw note
    canvas.drawText(material.note.ifEmpty { "-" }, x5, y, leftPaint)
    
    // Draw horizontal line
    canvas.drawLine(x1, y + rowHeight, x5 + width, y + rowHeight, borderPaint)
}
```

#### Step 5: Finalization
```kotlin
// Finish the page
document.finishPage(page)

// Write to file
val file = File(context.getExternalFilesDir(null), "WorkReport_${workReport.id}.pdf")
document.writeTo(FileOutputStream(file))

// Close document
document.close()

// Share or open the PDF
sharePdfFile(file)
```

### Error Handling

- **Missing Data**: Display "N/A" or default placeholder text
- **Long Text**: Implement text wrapping or truncation with ellipsis
- **Empty Materials**: Show empty state message
- **Formatting Errors**: Log error and use fallback formatting
- **File I/O Errors**: Handle permissions and storage errors gracefully

### Export Options

The PDF export should support multiple output options:

1. **Save to Device Storage**: Save PDF to Downloads or Documents folder
2. **Share via Intent**: Share PDF using Android's share sheet (email, cloud storage, etc.)
3. **Preview before Export**: Show PDF preview with option to print or share
4. **Batch Export**: Export multiple reports as single or multiple PDFs
5. **Email Integration**: Direct email attachment option

## Future Enhancements

- **Customizable Templates**: Allow users to select from multiple PDF templates
- **Logo Upload**: Company logo customization
- **Digital Signatures**: Integrate digital signature capture
- **QR Codes**: Add QR code with report link or verification
- **Multi-language Support**: Generate PDFs in different languages
- **Custom Fields**: Support for user-defined custom fields
- **Charts/Graphs**: Visual representation of hours, materials, etc.
- **Photo Attachments**: Include photos in the PDF export
- **Watermarks**: Add draft/confidential watermarks
- **Password Protection**: Optional PDF encryption

## Library Recommendations

### Android PDF Libraries

1. **iText** (Commercial license required for production)
   - Feature-rich
   - Professional output quality
   - Extensive documentation

2. **PDFDocument** (Android native)
   - Built into Android SDK
   - No external dependencies
   - Good for simple PDFs

3. **Apache PDFBox** (Android port)
   - Open source
   - Good community support
   - Moderate complexity

4. **PdfRenderer** (For preview only)
   - Native Android API
   - For rendering existing PDFs

### Recommended Approach

For this application, **PDFDocument** (Android native) is recommended for the initial implementation:
- No licensing costs
- Part of Android SDK (API level 19+)
- Sufficient for structured report layouts
- Can be enhanced with third-party libraries later if needed

## Testing Checklist

- [ ] PDF generates with all sections present
- [ ] Materials table handles 0, 1, and many materials correctly
- [ ] Long text wraps or truncates appropriately
- [ ] Dates format correctly in all locales
- [ ] Numbers format with correct decimal places
- [ ] PDF opens in standard PDF readers
- [ ] File naming follows convention
- [ ] Share functionality works on different Android versions
- [ ] Storage permissions handled correctly
- [ ] Generated PDF matches design specifications
- [ ] Empty fields show appropriate placeholders
- [ ] Multi-page reports handled correctly (if content exceeds one page)

## Security Considerations

- **Permissions**: Request storage permissions appropriately
- **Data Sensitivity**: Consider if work reports contain sensitive information
- **Access Control**: Implement appropriate file access restrictions
- **Temporary Files**: Clean up temporary files after generation
- **Path Traversal**: Validate file paths to prevent security issues

## Conclusion

This specification provides a comprehensive guide for implementing PDF export functionality for work reports. The layout is designed to be professional, readable, and suitable for both digital distribution and printing. The generation flow ensures all data is properly formatted and presented, with special attention to the materials table feature.
