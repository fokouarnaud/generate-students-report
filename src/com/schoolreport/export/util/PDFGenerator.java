package com.schoolreport.export.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.schoolreport.core.model.Grade;
import com.schoolreport.core.model.School;
import com.schoolreport.core.model.Student;
import com.schoolreport.core.model.Subject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilitaire pour la génération de PDF des bulletins
 */
public class PDFGenerator {
    
    private static final Logger LOGGER = Logger.getLogger(PDFGenerator.class.getName());
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");
    
    private static Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
    private static Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static Font normalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    private static Font smallFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
    
    /**
     * Génère un bulletin scolaire au format PDF
     * @param student l'étudiant
     * @param grades les notes de l'étudiant
     * @param trimester le trimestre
     * @param schoolYear l'année scolaire
     * @param school l'école
     * @param statistics les statistiques de classe (optionnel)
     * @param outputFile le fichier de sortie
     * @return le fichier PDF généré
     */
    public static File generateReportCard(Student student, List<Grade> grades, int trimester, 
                                         String schoolYear, School school, 
                                         Map<String, Object> statistics, File outputFile) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            document.open();
            
            // Ajouter l'en-tête
            addHeader(document, school, trimester, schoolYear);
            
            // Ajouter les informations de l'étudiant
            addStudentInfo(document, student);
            
            // Ajouter le tableau des notes
            addGradesTable(document, grades, statistics);
            
            // Ajouter les moyennes
            addAverages(document, grades, statistics);
            
            // Ajouter l'appréciation
            addAppreciation(document, "APPRÉCIATION", "ASSEZ BIEN");
            
            document.close();
            return outputFile;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating PDF report card", e);
            throw new RuntimeException("Error generating PDF report card", e);
        }
    }
    
    private static void addHeader(Document document, School school, int trimester, String schoolYear) throws DocumentException {
        Paragraph schoolName = new Paragraph(school.getName().toUpperCase(), titleFont);
        schoolName.setAlignment(Element.ALIGN_CENTER);
        document.add(schoolName);
        
        Paragraph bulletinTitle = new Paragraph("BULLETIN DU " + trimester + "ème TRIMESTRE", titleFont);
        bulletinTitle.setAlignment(Element.ALIGN_CENTER);
        bulletinTitle.setSpacingBefore(10);
        document.add(bulletinTitle);
        
        document.add(Chunk.NEWLINE);
    }
    
    private static void addStudentInfo(Document document, Student student) throws DocumentException {
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        
        PdfPCell nameLabel = new PdfPCell(new Phrase("Nom et Prénom", headerFont));
        nameLabel.setBorder(Rectangle.NO_BORDER);
        infoTable.addCell(nameLabel);
        
        PdfPCell className = new PdfPCell(new Phrase("Classe", headerFont));
        className.setBorder(Rectangle.NO_BORDER);
        className.setHorizontalAlignment(Element.ALIGN_RIGHT);
        infoTable.addCell(className);
        
        PdfPCell nameValue = new PdfPCell(new Phrase(student.getLastName() + " " + student.getFirstName(), normalFont));
        nameValue.setBorder(Rectangle.NO_BORDER);
        infoTable.addCell(nameValue);
        
        PdfPCell classValue = new PdfPCell(new Phrase(student.getSchoolClass().getName(), normalFont));
        classValue.setBorder(Rectangle.NO_BORDER);
        classValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        infoTable.addCell(classValue);
        
        document.add(infoTable);
        document.add(Chunk.NEWLINE);
    }
    
    private static void addGradesTable(Document document, List<Grade> grades, Map<String, Object> statistics) throws DocumentException {
        // Modifier pour créer une table avec 4 colonnes (sans colonne COEFF)
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        
        // En-têtes
        PdfPCell headerMatiere = new PdfPCell(new Phrase("MATIÈRES", headerFont));
        headerMatiere.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerMatiere.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(headerMatiere);
        
        PdfPCell headerSeq1 = new PdfPCell(new Phrase("SEQ.1", headerFont));
        headerSeq1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerSeq1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(headerSeq1);
        
        PdfPCell headerSeq2 = new PdfPCell(new Phrase("SEQ.2", headerFont));
        headerSeq2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerSeq2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(headerSeq2);
        
        PdfPCell headerMoy = new PdfPCell(new Phrase("MOY. TRIM", headerFont));
        headerMoy.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerMoy.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(headerMoy);
        
        // Données des notes (sans coefficient)
        for (Grade grade : grades) {
            Subject subject = grade.getSubject();
            
            PdfPCell cellMatiere = new PdfPCell(new Phrase(subject.getName(), normalFont));
            table.addCell(cellMatiere);
            
            PdfPCell cellSeq1 = new PdfPCell();
            if (grade.getSequence1Value() != null) {
                cellSeq1.addElement(new Phrase(DECIMAL_FORMAT.format(grade.getSequence1Value()), normalFont));
            } else {
                cellSeq1.addElement(new Phrase("-", normalFont));
            }
            cellSeq1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cellSeq1);
            
            PdfPCell cellSeq2 = new PdfPCell();
            if (grade.getSequence2Value() != null) {
                cellSeq2.addElement(new Phrase(DECIMAL_FORMAT.format(grade.getSequence2Value()), normalFont));
            } else {
                cellSeq2.addElement(new Phrase("-", normalFont));
            }
            cellSeq2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cellSeq2);
            
            PdfPCell cellMoy = new PdfPCell();
            if (grade.getTermAverage() != null) {
                cellMoy.addElement(new Phrase(DECIMAL_FORMAT.format(grade.getTermAverage()), normalFont));
            } else {
                cellMoy.addElement(new Phrase("-", normalFont));
            }
            cellMoy.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cellMoy);
        }
        
        document.add(table);
        document.add(Chunk.NEWLINE);
    }
    
    private static void addAverages(Document document, List<Grade> grades, Map<String, Object> statistics) throws DocumentException {
        double studentAverage = 0.0;
        int totalSubjects = 0;
        
        // Calcul de la moyenne générale (moyenne simple, sans coefficient)
        for (Grade grade : grades) {
            if (grade.getTermAverage() != null) {
                studentAverage += grade.getTermAverage();
                totalSubjects++;
            }
        }
        
        if (totalSubjects > 0) {
            studentAverage = studentAverage / totalSubjects;
        }
        
        PdfPTable averageTable = new PdfPTable(2);
        averageTable.setWidthPercentage(50);
        averageTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        PdfPCell averageLabel = new PdfPCell(new Phrase("Moyenne Générale", headerFont));
        averageLabel.setBackgroundColor(BaseColor.LIGHT_GRAY);
        averageLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
        averageTable.addCell(averageLabel);
        
        PdfPCell averageValue = new PdfPCell(new Phrase(DECIMAL_FORMAT.format(studentAverage), headerFont));
        averageValue.setBackgroundColor(BaseColor.LIGHT_GRAY);
        averageValue.setHorizontalAlignment(Element.ALIGN_CENTER);
        averageTable.addCell(averageValue);
        
        document.add(averageTable);
        document.add(Chunk.NEWLINE);
    }
    
    private static void addAppreciation(Document document, String label, String value) throws DocumentException {
        PdfPTable appreciationTable = new PdfPTable(2);
        appreciationTable.setWidthPercentage(100);
        
        PdfPCell appreciationLabel = new PdfPCell(new Phrase(label, headerFont));
        appreciationLabel.setBackgroundColor(BaseColor.LIGHT_GRAY);
        appreciationLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
        appreciationTable.addCell(appreciationLabel);
        
        PdfPCell appreciationValue = new PdfPCell(new Phrase(value, normalFont));
        appreciationValue.setHorizontalAlignment(Element.ALIGN_CENTER);
        appreciationTable.addCell(appreciationValue);
        
        document.add(appreciationTable);
    }
    
    /**
     * Génère des bulletins en lot pour tous les étudiants d'une classe
     * @param students la liste des étudiants
     * @param gradesMap la map des notes par étudiant
     * @param trimester le trimestre
     * @param schoolYear l'année scolaire
     * @param school l'école
     * @param statisticsMap les statistiques par classe
     * @param outputDir le répertoire de sortie
     * @return le répertoire contenant les bulletins générés
     */
    public static File generateBatchReportCards(List<Student> students, Map<Long, List<Grade>> gradesMap,
                                              int trimester, String schoolYear, School school,
                                              Map<Long, Map<String, Object>> statisticsMap, File outputDir) {
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        for (Student student : students) {
            List<Grade> grades = gradesMap.get(student.getId());
            
            if (grades != null && !grades.isEmpty()) {
                String fileName = "bulletin_" + student.getLastName() + "_" + student.getFirstName() + 
                                 "_T" + trimester + ".pdf";
                File outputFile = new File(outputDir, fileName);
                
                Map<String, Object> statistics = statisticsMap != null ? 
                                               statisticsMap.get(student.getSchoolClass().getId()) : null;
                
                generateReportCard(student, grades, trimester, schoolYear, school, statistics, outputFile);
            }
        }
        
        return outputDir;
    }
}
