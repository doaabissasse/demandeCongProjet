package com.example.service;

import com.example.resources.LeaveRequest;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Service
public class PDFService {

    private static final Logger logger = LoggerFactory.getLogger(PDFService.class);

    public byte[] generateLeaveRequestPDF(LeaveRequest leaveRequest) throws IOException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();
            logger.info("Document opened successfully.");

            // Add Logo
            ClassPathResource logoResource = new ClassPathResource("looginfo.png");
            Image logo = Image.getInstance(logoResource.getURL());
            logo.scaleToFit(180, 80);
            logo.setAlignment(Element.ALIGN_LEFT);
            document.add(logo);
            document.add(new Paragraph(""));
            logger.info("Logo added to the document.");

            // Date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dateDemande = leaveRequest.getDateDemande();
            if (dateDemande == null) {
                throw new IllegalArgumentException("La date de demande ne peut pas être null.");
            }
            Paragraph date = new Paragraph("Casablanca, " + sdf.format(dateDemande));
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);
            document.add(new Paragraph("\n"));
            logger.info("Date added to the document.");

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("Demande de Congé", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));
            logger.info("Title added to the document.");

            // Employee Info
            Font employeFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.BLACK);
            Paragraph employeeInfo = new Paragraph(
                    leaveRequest.getEmplnom() +" "+
                            leaveRequest.getEmplprenom() + "\n" +
                            "CIN: " + leaveRequest.getEmplCIN() + "\n" +
                            "Email: " + leaveRequest.getEmail() + "\n" +
                            "Télé: " + leaveRequest.getTele() + "\n"+
                            "Departement: " + leaveRequest.getDepartemant()+ "\n\n",
                    employeFont
            );
            employeeInfo.setAlignment(Element.ALIGN_LEFT);
            document.add(employeeInfo);
            document.add(new Paragraph("\n"));
            logger.info("Employee information added to the document.");

            // Letter Body
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font grayFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);

            Paragraph letterBody = new Paragraph();
            letterBody.add(new Phrase("Madame, Monsieur,\n\n", bodyFont));
            letterBody.add(new Phrase("Par la présente, je souhaite solliciter un congé de ", bodyFont));
            letterBody.add(new Phrase(String.valueOf(leaveRequest.getNbrJourCong()), grayFont));
            letterBody.add(new Phrase(" jours, à compter du ", bodyFont));
            letterBody.add(new Phrase(sdf.format(leaveRequest.getStartDate()), grayFont));
            letterBody.add(new Phrase(" jusqu'au ", bodyFont));
            letterBody.add(new Phrase(sdf.format(leaveRequest.getEndDate()), grayFont));
            letterBody.add(new Phrase(".\nJe m'engage à m'assurer que toutes mes tâches en cours seront complétées avant mon départ et à transmettre les dossiers nécessaires à mes collègues afin de garantir la continuité du service pendant mon absence. Je resterai joignable par email et par téléphone en cas de besoin urgent.\n\nJe vous remercie par avance pour votre compréhension et votre accord concernant cette demande. Je suis à votre disposition pour toute information complémentaire et j'attends votre réponse avec impatience.\n\nVeuillez agréer, Madame, Monsieur, l'expression de mes salutations distinguées.\n\n", bodyFont));

            document.add(letterBody);
            document.add(new Paragraph("\n"));
            logger.info("Letter body added to the document.");

            // Validation Date
            Date validationDate = leaveRequest.getDateValidation();
            String validationDateStr = (validationDate != null) ? sdf.format(validationDate) : "Date de validation:";
            Paragraph validationDateParagraph = new Paragraph(validationDateStr, bodyFont);
            document.add(validationDateParagraph);
            document.add(new Paragraph("\n\n"));
            logger.info("Validation date added to the document.");

            // Employee Signature
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

            cell = new PdfPCell(new Phrase("\n\n\n\n\n", bodyFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setPadding(10);
            signatureTable.addCell(cell);

            cell = new PdfPCell(new Phrase("\n\n\n\n\n", bodyFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setPadding(10);
            signatureTable.addCell(cell);

            document.add(signatureTable);
            logger.info("Signature table added to the document.");

            // Company Address
            Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph companyInfo = new Paragraph(
                    "Loginfo ingénierie\n" +
                            "82 Rue Soumaya, Casablanca 20100\n",
                    infoFont
            );
            companyInfo.setAlignment(Element.ALIGN_LEFT);
            companyInfo.setSpacingBefore(20f);
            document.add(companyInfo);
            logger.info("Company address added to the document.");

        } catch (DocumentException e) {
            logger.error("Error creating PDF document: ", e);
            throw new IOException("Erreur de création du PDF: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            logger.error("Error with input data: ", e);
            throw new IOException("Erreur de création du PDF: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error: ", e);
            throw new IOException("Erreur inattendue lors de la création du PDF: " + e.getMessage(), e);
        } finally {
            if (document.isOpen()) {
                document.close();
                logger.info("Document closed successfully.");
            }
        }

        return out.toByteArray();
    }
}
