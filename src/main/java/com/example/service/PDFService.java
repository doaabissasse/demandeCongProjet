package com.example.service;

import com.example.resources.LeaveRequest;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class PDFService {

    public byte[] generateLeaveRequestPDF(LeaveRequest leaveRequest) throws IOException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("Demande de Congé", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Paragraph date = new Paragraph("Date: " + sdf.format(new Date()), FontFactory.getFont(FontFactory.HELVETICA, 12));
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);
            document.add(new Paragraph("\n"));

            // Letter format
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph letterBody = new Paragraph(
                    "Monsieur/Madame,\n\n" +
                            "Je soussigné(e), " + leaveRequest.getEmplnom() + " " + leaveRequest.getEmplprenom() +
                            ", employé(e) au département " + leaveRequest.getDepartemant() +
                            ", souhaite par la présente formuler une demande de congé.\n\n" +
                            "Type de congé: " + leaveRequest.getType() + "\n" +
                            "Date de début: " + leaveRequest.getStartDate().toString() + "\n" +
                            "Date de fin: " + leaveRequest.getEndDate().toString() + "\n" +
                            "Nombre de jours de congé: " + leaveRequest.getNbrJourCong() + "\n\n" +
                            "Remarques: " + leaveRequest.getRemarque() + "\n\n" +
                            "Je vous prie d’agréer, Monsieur/Madame, l’expression de mes salutations distinguées.",
                    bodyFont
            );
            document.add(letterBody);
            document.add(new Paragraph("\n\n"));

            // Employee signature
            PdfPTable signatureTable = new PdfPTable(2);
            signatureTable.setWidthPercentage(100);
            signatureTable.setSpacingBefore(20f);

            PdfPCell cell = new PdfPCell(new Phrase("Signature de l'employé", bodyFont));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPadding(10);
            signatureTable.addCell(cell);

            cell = new PdfPCell(new Phrase("Signature de l'administrateur", bodyFont));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPadding(10);
            signatureTable.addCell(cell);

            cell = new PdfPCell(new Phrase("\n\n\n\n", bodyFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setPadding(10);
            signatureTable.addCell(cell);

            cell = new PdfPCell(new Phrase("\n\n\n\n", bodyFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setPadding(10);
            signatureTable.addCell(cell);

            document.add(signatureTable);
        } catch (DocumentException e) {
            throw new IOException("Error creating PDF", e);
        } finally {
            document.close();
        }

        return out.toByteArray();
    }
}
