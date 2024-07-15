package com.example.service;

import com.example.resources.LeaveRequest;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PDFService {

    public byte[] generateLeaveRequestPDF(LeaveRequest leaveRequest) throws IOException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("Leave Request Details"));
            document.add(new Paragraph("Employee Email: " + leaveRequest.getEmployeeEmail()));
            document.add(new Paragraph("Leave Type: " + leaveRequest.getType()));
            document.add(new Paragraph("Start Date: " + leaveRequest.getStartDate()));
            document.add(new Paragraph("End Date: " + leaveRequest.getEndDate()));
            document.add(new Paragraph("Status: " + leaveRequest.getStatus()));
        } catch (DocumentException e) {
            throw new IOException("Error creating PDF", e);
        } finally {
            document.close();
        }

        return out.toByteArray();
    }
}
