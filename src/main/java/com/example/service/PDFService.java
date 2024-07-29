package com.example.service;

import com.example.resources.Employe;
import com.example.resources.Signature;
import com.example.resources.LeaveRequest;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
public class PDFService {

    @Autowired
    private SignatureService signatureService;

    @Autowired
    private EmployeeService employeeService;

    public byte[] generateLeaveRequestPDF(LeaveRequest leaveRequest) throws IOException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Ajout du logo
            ClassPathResource logoResource = new ClassPathResource("looginfo.png");
            Image logo = Image.getInstance(logoResource.getURL());
            logo.scaleToFit(180, 80);
            logo.setAlignment(Element.ALIGN_LEFT);
            document.add(logo);
            document.add(new Paragraph(""));

            // Ajout de la date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dateDemande = leaveRequest.getDateDemande();
            Paragraph date = new Paragraph("Casablanca, " + sdf.format(dateDemande));
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);
            document.add(new Paragraph("\n"));

            // Ajout du titre
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("Demande de Congé", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Ajout des informations de l'employé
            Font employeFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.BLACK);
            Paragraph employeeInfo = new Paragraph(
                    leaveRequest.getEmplnom() + " " +
                            leaveRequest.getEmplprenom() + "\n" +
                            "CIN: " + leaveRequest.getEmplCIN() + "\n" +
                            "Email: " + leaveRequest.getEmail() + "\n" +
                            "Télé: " + leaveRequest.getTele() + "\n" +
                            "Departement: " + leaveRequest.getDepartemant() + "\n\n",
                    employeFont
            );
            employeeInfo.setAlignment(Element.ALIGN_LEFT);
            document.add(employeeInfo);
            document.add(new Paragraph("\n"));

            // Ajout du corps de la lettre
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font grayFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
            Paragraph letterBody = new Paragraph();
            letterBody.add(new Phrase("Madame, Monsieur,\n\n", bodyFont));
            letterBody.add(new Phrase("Par la présente, je souhaite solliciter un congé de ", bodyFont));
            letterBody.add(new Phrase(leaveRequest.getType(), grayFont));
            letterBody.add(new Phrase(" pour une durée de ", bodyFont));
            letterBody.add(new Phrase(String.valueOf(leaveRequest.getNbrJourCong()), grayFont));
            letterBody.add(new Phrase(" jours, à compter du ", bodyFont));
            letterBody.add(new Phrase(sdf.format(leaveRequest.getStartDate()), grayFont));
            letterBody.add(new Phrase(" jusqu'au ", bodyFont));
            letterBody.add(new Phrase(sdf.format(leaveRequest.getEndDate()), grayFont));
            letterBody.add(new Phrase(".\nJe m'engage à m'assurer que toutes mes tâches en cours seront complétées avant mon départ et à transmettre les dossiers nécessaires à mes collègues afin de garantir la continuité du service pendant mon absence. Je resterai joignable par email et par téléphone en cas de besoin urgent.\n\nJe vous remercie par avance pour votre compréhension et votre accord concernant cette demande. Je suis à votre disposition pour toute information complémentaire et j'attends votre réponse avec impatience.\n\nVeuillez agréer, Madame, Monsieur, l'expression de mes salutations distinguées.\n\n", bodyFont));
            document.add(letterBody);
            document.add(new Paragraph("\n"));

            // Ajout de la date de validation
            Date validationDate = leaveRequest.getDateValidation();
            String validationDateStr = (validationDate != null) ? sdf.format(validationDate) : "Date de validation:";
            Paragraph validationDateParagraph = new Paragraph(validationDateStr, bodyFont);
            document.add(validationDateParagraph);
            document.add(new Paragraph("\n\n"));

            // Récupération de l'ID de l'employé par email
            Optional<Employe> employeeOpt = employeeService.getEmployeeByEmail(leaveRequest.getEmail());
            Optional<Signature> employeeSignatureOpt = Optional.empty();
            if (employeeOpt.isPresent()) {
                employeeSignatureOpt = signatureService.getSignatureByUserId(employeeOpt.get().getId());
            }

            // Récupération des signatures de l'administrateur
            Optional<Signature> adminSignatureOpt = signatureService.getSignatureByUserId("admin");

            // Ajout des signatures au document
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

            // Ajout des images des signatures
            if (employeeSignatureOpt.isPresent()) {
                Image employeeSignature = Image.getInstance(employeeSignatureOpt.get().getSignatureUrl());
                employeeSignature.scaleToFit(150, 75);
                cell = new PdfPCell(employeeSignature);
                cell.setBorder(Rectangle.BOTTOM);
                cell.setPadding(10);
                signatureTable.addCell(cell);
            } else {
                cell = new PdfPCell(new Phrase("\n\n\n\n\n", bodyFont));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setPadding(10);
                signatureTable.addCell(cell);
            }

            if (adminSignatureOpt.isPresent()) {
                Image adminSignature = Image.getInstance(adminSignatureOpt.get().getSignatureUrl());
                adminSignature.scaleToFit(150, 75);
                cell = new PdfPCell(adminSignature);
                cell.setBorder(Rectangle.BOTTOM);
                cell.setPadding(10);
                signatureTable.addCell(cell);
            } else {
                cell = new PdfPCell(new Phrase("\n\n\n\n\n", bodyFont));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setPadding(10);
                signatureTable.addCell(cell);
            }

            document.add(signatureTable);

            // Ajout des informations de l'entreprise
            Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph companyInfo = new Paragraph(
                    "Loginfo ingénierie\n" +
                            "82 Rue Soumaya, Casablanca 20100\n",
                    infoFont
            );
            companyInfo.setAlignment(Element.ALIGN_LEFT);
            companyInfo.setSpacingBefore(20f);
            document.add(companyInfo);

        } catch (DocumentException e) {
            throw new IOException("Erreur de création du PDF: " + e.getMessage(), e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }

        return out.toByteArray();
    }
}
