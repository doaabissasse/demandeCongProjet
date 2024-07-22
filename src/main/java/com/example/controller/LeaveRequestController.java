package com.example.controller;

import com.example.repository.LeaveRequestRepository;
import com.example.resources.LeaveRequest;
import com.example.service.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private PDFService pdfService;

    private static final Logger logger = LoggerFactory.getLogger(LeaveRequestController.class);

    @PostMapping("/leave-requests")
    public ResponseEntity<?> createLeaveRequest(@RequestBody LeaveRequest leaveRequest) {
        logger.info("Received leave request: {}", leaveRequest);

        if (leaveRequest.getEmplnom() == null || leaveRequest.getStartDate() == null || leaveRequest.getEndDate() == null) {
            logger.error("Invalid leave request data: {}", leaveRequest);
            return ResponseEntity.badRequest().body("Invalid leave request data");
        }
        // Validate that the start date is at least 10 days after today
        LocalDate today = LocalDate.now();
        LocalDate startDate = leaveRequest.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (ChronoUnit.DAYS.between(today, startDate) < 10) {
            logger.error("Start date must be at least 10 days from today: {}", leaveRequest);
            return ResponseEntity.badRequest().body("Start date must be at least 10 days from today");
        }
        if (leaveRequest.getEndDate().before(leaveRequest.getStartDate())) {
            logger.error("End date must be after start date: {}", leaveRequest);
            return ResponseEntity.badRequest().body("End date must be after start date");
        }


        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);
        logger.info("Saved leave request with ID: {}", savedRequest.getId());
        return ResponseEntity.ok(savedRequest);
    }

    @GetMapping("/leave-requests")
    public List<LeaveRequest> getAllLeaveRequests() {
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAll();
        leaveRequests.forEach(request -> logger.info("LeaveRequest: {}", request));
        return leaveRequests;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/leave-requests/{id}/pdf")
    public ResponseEntity<byte[]> generateLeaveRequestPDF(@PathVariable String id) {
        try {
            LeaveRequest leaveRequest = leaveRequestRepository.findById(id).orElse(null);

            if (leaveRequest == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(("Leave request not found for ID: " + id).getBytes());
            }

            byte[] pdfBytes = pdfService.generateLeaveRequestPDF(leaveRequest);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=leave_request_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating PDF: " + e.getMessage()).getBytes());
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/test-cors")
    public ResponseEntity<String> testCors() {
        return ResponseEntity.ok("CORS is working!");
    }

}

