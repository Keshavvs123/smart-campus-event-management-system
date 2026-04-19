package com.smcem.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.File;
import java.io.IOException;

/**
 * CREATIONAL DESIGN PATTERN: Builder Pattern
 * Assembles a complex object (PDF Certificate) strictly step-by-step.
 */
public class CertificateBuilder {
    private String outputPath;
    private String title;
    private String issuer;
    private String recipientName;
    private String achievementText;
    private String eventName;
    private String date;
    private String certificateNumber;

    public CertificateBuilder setOutputPath(String outputPath) {
        this.outputPath = outputPath;
        return this;
    }

    public CertificateBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public CertificateBuilder setIssuer(String issuer) {
        this.issuer = issuer;
        return this;
    }

    public CertificateBuilder setRecipientName(String recipientName) {
        this.recipientName = recipientName;
        return this;
    }

    public CertificateBuilder setAchievementText(String achievementText) {
        this.achievementText = achievementText;
        return this;
    }

    public CertificateBuilder setEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public CertificateBuilder setDate(String date) {
        this.date = date;
        return this;
    }

    public CertificateBuilder setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
        return this;
    }

    public String build() throws IOException {
        new File(outputPath).getParentFile().mkdirs();

        PdfWriter writer = new PdfWriter(outputPath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.setMargins(36, 36, 36, 36);

        document.add(new Paragraph(title).setFontSize(32).setBold().setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph(issuer).setFontSize(14).setTextAlignment(TextAlignment.CENTER).setMarginBottom(30));
        document.add(new Paragraph("This is to certify that").setFontSize(12).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph(recipientName).setFontSize(18).setBold().setTextAlignment(TextAlignment.CENTER).setMarginBottom(15));
        document.add(new Paragraph(achievementText).setFontSize(12).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph(eventName).setFontSize(14).setBold().setTextAlignment(TextAlignment.CENTER).setMarginBottom(20));

        Table table = new Table(2);
        table.addCell(new Cell().add(new Paragraph("Date:")).setBold());
        table.addCell(new Cell().add(new Paragraph(date)));
        table.addCell(new Cell().add(new Paragraph("Certificate Number:")).setBold());
        table.addCell(new Cell().add(new Paragraph(certificateNumber)));
        document.add(table);

        document.close();
        return outputPath;
    }
}
