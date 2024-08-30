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

/**
 * Service pour la génération de documents PDF pour les demandes de congé.
 */
@Service
public class PDFService {

    @Autowired
    private SignatureService signatureService; // Service pour accéder aux signatures des employés et des administrateurs

    @Autowired
    private EmployeeService employeeService; // Service pour accéder aux détails des employés

    /**
     * Génère un PDF pour une demande de congé.
     * @param leaveRequest La demande de congé contenant les informations nécessaires pour le PDF.
     * @return Le contenu du PDF sous forme de tableau d'octets.
     * @throws IOException Si une erreur se produit lors de la création du PDF.
     */
    public byte[] generateLeaveRequestPDF(LeaveRequest leaveRequest) throws IOException {
        Document document = new Document(); // Création d'un nouvel objet Document pour le PDF
        ByteArrayOutputStream out = new ByteArrayOutputStream(); // Flux de sortie pour stocker le contenu du PDF en mémoire

        try {
            PdfWriter.getInstance(document, out); // Création d'un PdfWriter pour écrire dans le document
            document.open(); // Ouverture du document pour ajout de contenu

            // Ajout du logo de l'entreprise
            ClassPathResource logoResource = new ClassPathResource("looginfo.png");
            Image logo = Image.getInstance(logoResource.getURL()); // Chargement de l'image du logo
            logo.scaleToFit(180, 80); // Redimensionnement de l'image du logo
            logo.setAlignment(Element.ALIGN_LEFT); // Alignement du logo à gauche
            document.add(logo); // Ajout du logo au document
            document.add(new Paragraph("")); // Ajout d'un espace

            // Ajout de la date de la demande
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dateDemande = leaveRequest.getDateDemande();
            Paragraph date = new Paragraph("Casablanca, " + sdf.format(dateDemande));
            date.setAlignment(Element.ALIGN_RIGHT); // Alignement de la date à droite
            document.add(date); // Ajout de la date au document
            document.add(new Paragraph("\n")); // Ajout d'un espace

            // Ajout du titre de la demande
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("Demande de Congé", titleFont);
            title.setAlignment(Element.ALIGN_CENTER); // Alignement du titre au centre
            document.add(title); // Ajout du titre au document
            document.add(new Paragraph("\n")); // Ajout d'un espace

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
            employeeInfo.setAlignment(Element.ALIGN_LEFT); // Alignement des informations de l'employé à gauche
            document.add(employeeInfo); // Ajout des informations de l'employé au document
            document.add(new Paragraph("\n")); // Ajout d'un espace

            // Ajout du corps de la lettre de demande
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
            document.add(letterBody); // Ajout du corps de la lettre au document
            document.add(new Paragraph("\n")); // Ajout d'un espace

            // Ajout de la date de validation si elle existe
            Date validationDate = leaveRequest.getDateValidation();
            String validationDateStr = (validationDate != null)
                    ? "Date de validation: " + sdf.format(validationDate)
                    : "Date de validation";
            Paragraph validationDateParagraph = new Paragraph(validationDateStr, bodyFont);
            document.add(validationDateParagraph); // Ajout de la date de validation au document
            document.add(new Paragraph("\n\n")); // Ajout d'un espace

            // Récupération de la signature de l'employé
            Optional<Employe> employeeOpt = employeeService.getEmployeeByEmail(leaveRequest.getEmail());
            Optional<Signature> employeeSignatureOpt = Optional.empty();
            if (employeeOpt.isPresent()) {
                employeeSignatureOpt = signatureService.getSignatureByUserId(employeeOpt.get().getId());
            }

            // Récupération de la signature de l'administrateur
            Optional<Signature> adminSignatureOpt = signatureService.getSignatureByUserId("admin");

            // Création d'un tableau pour les signatures
            PdfPTable signatureTable = new PdfPTable(2);
            signatureTable.setWidthPercentage(100);
            signatureTable.setSpacingBefore(20f); // Espacement avant le tableau des signatures

            // Cellule pour la signature de l'employé
            PdfPCell cell = new PdfPCell(new Phrase("Signature de l'employé", bodyFont));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPadding(10);
            signatureTable.addCell(cell);

            // Cellule pour la signature de l'administrateur
            cell = new PdfPCell(new Phrase("Signature de l'administrateur", bodyFont));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPadding(10);
            signatureTable.addCell(cell);

            // Ajout de la signature de l'employé si elle existe
            if (employeeSignatureOpt.isPresent()) {
                Image employeeSignature = Image.getInstance(employeeSignatureOpt.get().getSignatureUrl());
                employeeSignature.scaleToFit(150, 75); // Redimensionnement de l'image de signature
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

            // Ajout de la signature de l'administrateur si elle existe
            if (adminSignatureOpt.isPresent()) {
                Image adminSignature = Image.getInstance(adminSignatureOpt.get().getSignatureUrl());
                adminSignature.scaleToFit(150, 75); // Redimensionnement de l'image de signature
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

            document.add(signatureTable); // Ajout du tableau des signatures au document

            // Ajout des informations de l'entreprise
            Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph companyInfo = new Paragraph(
                    "Loginfo ingénierie\n" +
                            "82 Rue Soumaya, Casablanca 20100\n",
                    infoFont
            );
            companyInfo.setAlignment(Element.ALIGN_LEFT); // Alignement des informations de l'entreprise à gauche
            companyInfo.setSpacingBefore(20f); // Espacement avant les informations de l'entreprise
            document.add(companyInfo); // Ajout des informations de l'entreprise au document

        } catch (DocumentException e) {
            throw new IOException("Erreur de création du PDF: " + e.getMessage(), e); // Gestion des exceptions
        } finally {
            if (document.isOpen()) {
                document.close(); // Fermeture du document pour garantir que le contenu est bien écrit
            }
        }

        return out.toByteArray(); // Retourne le contenu du PDF en tant que tableau d'octets
    }
}
