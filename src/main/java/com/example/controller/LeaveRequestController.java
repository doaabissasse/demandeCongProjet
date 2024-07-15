package com.example.controller;

import com.example.repository.LeaveRequestRepository;
import com.example.resources.LeaveRequest;
import com.example.service.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private PDFService pdfService;

    @PostMapping("/leave-requests")
    public LeaveRequest createLeaveRequest(@RequestBody LeaveRequest leaveRequest) {
        leaveRequest.setStatus("Pending");
        return leaveRequestRepository.save(leaveRequest);
    }

    @GetMapping("/leave-requests")
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    @GetMapping("/leave-requests/{id}/pdf")
    public ResponseEntity<byte[]> generateLeaveRequestPDF(@PathVariable String id) throws IOException {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id).orElse(null);

        if (leaveRequest == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] pdfBytes = pdfService.generateLeaveRequestPDF(leaveRequest);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=leave_request_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
