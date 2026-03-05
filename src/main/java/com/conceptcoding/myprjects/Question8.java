package com.conceptcoding.myprjects;
/*
* ## **🎮 Question 8: Report Generation**
**Scenario:**
System generates reports in different formats:
- PDF reports
- Excel reports
- HTML reports
- CSV reports
All reports follow same structure:
1. Add header
2. Add data rows
3. Add footer
4. Format and save
But formatting differs for each type.
**Requirements:**
- Same generation flow for all reports
- Each format implements its own styling
- Easy to add new formats
**Question:** Which design pattern(s) would you use?
* */

//**Excellent!** You've identified the perfect pattern! Template Method Pattern is **exactly right** for this scenario.
//
//Let me provide a complete implementation:
//
//## Complete Implementation
//
//```java
/// / ============= TEMPLATE METHOD PATTERN =============
//

import java.util.Arrays;
import java.util.List;

//// Abstract class defining the template
abstract class ReportGenerator {

    // Template method - defines the algorithm structure
    // This is FINAL so subclasses can't change the flow
    public final void generateReport(ReportData data) {
        System.out.println("\n=== Starting Report Generation ===");

        // Step 1: Setup
        setup();

        // Step 2: Add header
        addHeader(data.getTitle(), data.getDate());

        // Step 3: Add data rows
        addDataRows(data.getRows());

        // Step 4: Add footer
        addFooter(data.getFooterText());

        // Step 5: Format
        format();

        // Step 6: Save
        save(data.getFileName());

        System.out.println("=== Report Generation Complete ===\n");
    }

    // Abstract methods - must be implemented by subclasses
    protected abstract void addHeader(String title, String date);
    protected abstract void addDataRows(List<String[]> rows);
    protected abstract void addFooter(String footerText);
    protected abstract void format();
    protected abstract void save(String fileName);

    // Hook methods - optional override (with default implementation)
    protected void setup() {
        System.out.println("Default setup completed");
    }

    // Common utility method (can be used by all subclasses)
    protected void validateData(List<String[]> rows) {
        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException("Data rows cannot be empty");
        }
    }
}

// ============= CONCRETE IMPLEMENTATIONS =============

class PDFReportGenerator extends ReportGenerator {
    private StringBuilder pdfContent;

    @Override
    protected void setup() {
        System.out.println("[PDF] Initializing PDF document");
        pdfContent = new StringBuilder();
        pdfContent.append("PDF Document Start\n");
    }

    @Override
    protected void addHeader(String title, String date) {
        System.out.println("[PDF] Adding PDF header with styling");
        pdfContent.append("==========================================\n");
        pdfContent.append("         " + title.toUpperCase() + "\n");
        pdfContent.append("         Date: " + date + "\n");
        pdfContent.append("==========================================\n\n");
    }

    @Override
    protected void addDataRows(List<String[]> rows) {
        System.out.println("[PDF] Adding data rows with PDF table formatting");
        validateData(rows);

        pdfContent.append("Data Section:\n");
        pdfContent.append("------------------------------------------\n");
        for (String[] row : rows) {
            pdfContent.append("| ");
            for (String cell : row) {
                pdfContent.append(String.format("%-15s | ", cell));
            }
            pdfContent.append("\n");
        }
        pdfContent.append("------------------------------------------\n\n");
    }

    @Override
    protected void addFooter(String footerText) {
        System.out.println("[PDF] Adding PDF footer");
        pdfContent.append("==========================================\n");
        pdfContent.append("Footer: " + footerText + "\n");
        pdfContent.append("==========================================\n");
    }

    @Override
    protected void format() {
        System.out.println("[PDF] Applying PDF-specific formatting (fonts, margins, page breaks)");
        pdfContent.append("\n[PDF Formatting Applied: Arial, 12pt, A4 size]\n");
    }

    @Override
    protected void save(String fileName) {
        System.out.println("[PDF] Saving to: " + fileName + ".pdf");
        // Simulate file save
        System.out.println("Content Preview:\n" + pdfContent.toString());
    }
}

class ExcelReportGenerator extends ReportGenerator {
    private List<String> excelRows;

    @Override
    protected void setup() {
        System.out.println("[EXCEL] Creating new Excel workbook");
        excelRows = new ArrayList<>();
    }

    @Override
    protected void addHeader(String title, String date) {
        System.out.println("[EXCEL] Adding header to Excel sheet with bold formatting");
        excelRows.add("HEADER_ROW: " + title + " | Date: " + date);
        excelRows.add(""); // Empty row for spacing
    }

    @Override
    protected void addDataRows(List<String[]> rows) {
        System.out.println("[EXCEL] Adding data rows to Excel cells");
        validateData(rows);

        // Add column headers
        if (!rows.isEmpty()) {
            excelRows.add("COL_HEADERS: Name, Department, Salary, Performance");
        }

        // Add data
        for (int i = 0; i < rows.size(); i++) {
            String[] row = rows.get(i);
            excelRows.add("ROW_" + (i + 1) + ": " + String.join(" | ", row));
        }
    }

    @Override
    protected void addFooter(String footerText) {
        System.out.println("[EXCEL] Adding footer to Excel sheet");
        excelRows.add(""); // Empty row
        excelRows.add("FOOTER: " + footerText);
    }

    @Override
    protected void format() {
        System.out.println("[EXCEL] Applying Excel formatting (borders, colors, formulas)");
        excelRows.add("\n[Excel Formatting: Borders, Alternate row colors, Auto-fit columns]");
    }

    @Override
    protected void save(String fileName) {
        System.out.println("[EXCEL] Saving to: " + fileName + ".xlsx");
        System.out.println("Excel Content:\n" + String.join("\n", excelRows));
    }
}

class HTMLReportGenerator extends ReportGenerator {
    private StringBuilder htmlContent;

    @Override
    protected void setup() {
        System.out.println("[HTML] Creating HTML document structure");
        htmlContent = new StringBuilder();
        htmlContent.append("<!DOCTYPE html>\n<html>\n<head>\n");
        htmlContent.append("<style>\n");
        htmlContent.append("  body { font-family: Arial, sans-serif; }\n");
        htmlContent.append("  table { border-collapse: collapse; width: 100%; }\n");
        htmlContent.append("  th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
        htmlContent.append("  th { background-color: #4CAF50; color: white; }\n");
        htmlContent.append("</style>\n</head>\n<body>\n");
    }

    @Override
    protected void addHeader(String title, String date) {
        System.out.println("[HTML] Adding HTML header with CSS styling");
        htmlContent.append("<div style='text-align: center; margin: 20px;'>\n");
        htmlContent.append("  <h1>" + title + "</h1>\n");
        htmlContent.append("  <p>Date: " + date + "</p>\n");
        htmlContent.append("</div>\n");
    }

    @Override
    protected void addDataRows(List<String[]> rows) {
        System.out.println("[HTML] Adding data rows as HTML table");
        validateData(rows);

        htmlContent.append("<table>\n");
        htmlContent.append("  <tr><th>Name</th><th>Department</th><th>Salary</th><th>Performance</th></tr>\n");

        for (String[] row : rows) {
            htmlContent.append("  <tr>");
            for (String cell : row) {
                htmlContent.append("<td>" + cell + "</td>");
            }
            htmlContent.append("</tr>\n");
        }

        htmlContent.append("</table>\n");
    }

    @Override
    protected void addFooter(String footerText) {
        System.out.println("[HTML] Adding HTML footer");
        htmlContent.append("<div style='text-align: center; margin: 20px; color: gray;'>\n");
        htmlContent.append("  <p>" + footerText + "</p>\n");
        htmlContent.append("</div>\n");
    }

    @Override
    protected void format() {
        System.out.println("[HTML] Applying HTML/CSS responsive formatting");
        htmlContent.append("<!-- Responsive formatting applied -->\n");
    }

    @Override
    protected void save(String fileName) {
        htmlContent.append("</body>\n</html>");
        System.out.println("[HTML] Saving to: " + fileName + ".html");
        System.out.println("HTML Content:\n" + htmlContent.toString());
    }
}

class CSVReportGenerator extends ReportGenerator {
    private StringBuilder csvContent;

    @Override
    protected void setup() {
        System.out.println("[CSV] Initializing CSV file");
        csvContent = new StringBuilder();
    }

    @Override
    protected void addHeader(String title, String date) {
        System.out.println("[CSV] Adding CSV header row");
        csvContent.append("# " + title + "\n");
        csvContent.append("# Date: " + date + "\n");
        csvContent.append("\n");
    }

    @Override
    protected void addDataRows(List<String[]> rows) {
        System.out.println("[CSV] Adding data rows as comma-separated values");
        validateData(rows);

        // Add column headers
        csvContent.append("Name,Department,Salary,Performance\n");

        // Add data
        for (String[] row : rows) {
            csvContent.append(String.join(",", row)).append("\n");
        }
    }

    @Override
    protected void addFooter(String footerText) {
        System.out.println("[CSV] Adding CSV footer comment");
        csvContent.append("\n# " + footerText + "\n");
    }

    @Override
    protected void format() {
        System.out.println("[CSV] CSV formatting (escaping special characters)");
        // In real implementation, would escape commas, quotes, etc.
    }

    @Override
    protected void save(String fileName) {
        System.out.println("[CSV] Saving to: " + fileName + ".csv");
        System.out.println("CSV Content:\n" + csvContent.toString());
    }
}

// ============= DATA MODEL =============

class ReportData {
    private String title;
    private String date;
    private List<String[]> rows;
    private String footerText;
    private String fileName;

    public ReportData(String title, String date, List<String[]> rows,
                      String footerText, String fileName) {
        this.title = title;
        this.date = date;
        this.rows = rows;
        this.footerText = footerText;
        this.fileName = fileName;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public List<String[]> getRows() { return rows; }
    public String getFooterText() { return footerText; }
    public String getFileName() { return fileName; }
}

// ============= FACTORY FOR REPORT GENERATORS (BONUS) =============

class ReportGeneratorFactory {
    public static ReportGenerator getGenerator(String type) {
        switch (type.toUpperCase()) {
            case "PDF":
                return new PDFReportGenerator();
            case "EXCEL":
                return new ExcelReportGenerator();
            case "HTML":
                return new HTMLReportGenerator();
            case "CSV":
                return new CSVReportGenerator();
            default:
                throw new IllegalArgumentException("Unknown report type: " + type);
        }
    }
}


//// ============= DEMO =============
//
public class ReportGenerationDemo {
    public static void main(String[] args) {
        // Sample data
        List<String[]> employeeData = Arrays.asList(
            new String[]{"John Doe", "Engineering", "$95000", "Excellent"},
            new String[]{"Jane Smith", "Marketing", "$78000", "Good"},
            new String[]{"Bob Johnson", "Sales", "$82000", "Excellent"},
            new String[]{"Alice Williams", "HR", "$71000", "Good"}
        );

        ReportData data = new ReportData(
            "Employee Performance Report Q4 2024",
            "2024-12-31",
            employeeData,
            "Confidential - Internal Use Only",
            "employee_report_q4"
        );

        // Generate PDF Report
        System.out.println("**************************************************");
        ReportGenerator pdfGenerator = new PDFReportGenerator();
        pdfGenerator.generateReport(data);

        // Generate Excel Report
        System.out.println("\n**************************************************");
        ReportGenerator excelGenerator = new ExcelReportGenerator();
        excelGenerator.generateReport(data);

        // Generate HTML Report
        System.out.println("\n**************************************************");
        ReportGenerator htmlGenerator = new HTMLReportGenerator();
        htmlGenerator.generateReport(data);

        // Generate CSV Report
        System.out.println("\n**************************************************");
        ReportGenerator csvGenerator = new CSVReportGenerator();
        csvGenerator.generateReport(data);

        // Using Factory Pattern
        System.out.println("\n**************************************************");
        System.out.println("USING FACTORY PATTERN:");
        ReportGenerator factoryGenerator = ReportGeneratorFactory.getGenerator("PDF");
        factoryGenerator.generateReport(data);
    }
}
//```
//
//## Key Design Highlights
//
//### ✅ **Template Method Pattern Benefits:**
//
//1. **Same Flow, Different Implementation:**
//   - `generateReport()` is the template method (FINAL)
//   - All reports follow: setup → header → data → footer → format → save
//   - Each subclass implements its own styling
//
//2. **Easy to Add New Formats:**
//   ```java
//   class JSONReportGenerator extends ReportGenerator {
//       // Just implement the 5 abstract methods!
//       protected void addHeader(String title, String date) {
//           // JSON-specific header
//       }
//       // ... other methods
//   }
//   ```
//
//3. **Code Reuse:**
//   - Common logic in base class (`validateData()`)
//   - No duplication of the workflow
//
//4. **Hook Methods:**
//   - `setup()` has default implementation
//   - Subclasses can override if needed
//
//### 📊 **Pattern Structure:**
//
//```
//ReportGenerator (Abstract)
//├── generateReport() [TEMPLATE - FINAL]
//├── setup() [HOOK - optional override]
//├── addHeader() [ABSTRACT]
//├── addDataRows() [ABSTRACT]
//├── addFooter() [ABSTRACT]
//├── format() [ABSTRACT]
//└── save() [ABSTRACT]
//
//PDFReportGenerator
//├── implements all abstract methods
//└── PDF-specific formatting
//
//ExcelReportGenerator
//├── implements all abstract methods
//└── Excel-specific formatting
//
//... and so on
//```
//
//### 🎯 **When to Use Template Method:**
//
//✅ Use when:
//- Multiple classes share same algorithm structure
//- Steps are same, but implementation differs
//- You want to prevent subclasses from changing the flow
//- Common behavior can be factored into base class
//
//❌ Don't use when:
//- Algorithm structure varies significantly
//- Need runtime flexibility in the flow
//- Strategy pattern might be better
//
//### 🆚 **Template Method vs Strategy:**
//
//| Template Method | Strategy |
//|----------------|----------|
//| Fixed algorithm structure | Flexible algorithm |
//| Inheritance-based | Composition-based |
//| Compile-time binding | Runtime binding |
//| Use for: Reports, Workflows | Use for: Algorithms, Behaviors |
//
//Your analysis was perfect! Template Method is the ideal choice here. 🎯

public class Question8 {
}
