package com.example.controller;

import com.example.config.jwt.JwtUtils;
import com.example.repository.EmployeRepository;
import com.example.repository.LeaveRequestRepository;
import com.example.resources.Employe;
import com.example.resources.LeaveRequest;
import com.example.service.PDFService;
import com.example.service.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private PDFService pdfService;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private JwtUtils jwtUtils;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();


    private static final Logger logger = LoggerFactory.getLogger(LeaveRequestController.class);

    @PutMapping("/leave-requests/{id}/accept")
    public ResponseEntity<String> acceptLeaveRequest(@PathVariable String id) {
        Optional<LeaveRequest> optionalLeaveRequest = leaveRequestRepository.findById(id);
        if (!optionalLeaveRequest.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        LeaveRequest leaveRequest = optionalLeaveRequest.get();
        leaveRequest.setStatus("approuvé");
        leaveRequest.setDateValidation(new Date());
        leaveRequestRepository.save(leaveRequest);

        // Envoyer une réponse avec un message de notification
        String notificationMessage = "Votre demande de congé a été approuvée.";
        return ResponseEntity.ok(notificationMessage);
    }

    @PutMapping("/leave-requests/{id}/refuse")
    public ResponseEntity<String> refuseLeaveRequest(@PathVariable String id) {
        Optional<LeaveRequest> optionalLeaveRequest = leaveRequestRepository.findById(id);
        if (!optionalLeaveRequest.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        LeaveRequest leaveRequest = optionalLeaveRequest.get();
        leaveRequest.setStatus("refusé");
        leaveRequest.setDateValidation(new Date());
        leaveRequestRepository.save(leaveRequest);

        // Envoyer une réponse avec un message de notification
        String notificationMessage = "Votre demande de congé a été refusée.";
        return ResponseEntity.ok(notificationMessage);
    }

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
        LocalDate endDate = leaveRequest.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (ChronoUnit.DAYS.between(today, startDate) < 10) {
            logger.error("Start date must be at least 10 days from today: {}", leaveRequest);
            return ResponseEntity.badRequest().body("Start date must be at least 10 days from today");
        }
        if (leaveRequest.getEndDate().before(leaveRequest.getStartDate())) {
            logger.error("End date must be after start date: {}", leaveRequest);
            return ResponseEntity.badRequest().body("End date must be after start date");
        }
// Fetch user from database
        Optional<Employe> optionalEmploye = employeRepository.findByUsername(leaveRequest.getUsername());
        if (!optionalEmploye.isPresent()) {
            logger.error("Employe not found: {}", leaveRequest.getUsername());
            return ResponseEntity.badRequest().body("Employe not found");
        }

        Employe employe = optionalEmploye.get();
        int daysRequested = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

        if ("payé".equals(leaveRequest.getType()) && daysRequested > employe.getSolde_conges().getPayes()) {
            logger.error("Requested paid leave days exceed available balance: {}", leaveRequest);
            return ResponseEntity.badRequest().body("Requested paid leave days exceed available balance");
        } else if ("non_payé".equals(leaveRequest.getType()) && daysRequested > employe.getSolde_conges().getNon_payes()) {
            logger.error("Requested unpaid leave days exceed available balance: {}", leaveRequest);
            return ResponseEntity.badRequest().body("Requested unpaid leave days exceed available balance");
        } else if ("maladie".equals(leaveRequest.getType()) && daysRequested > employe.getSolde_conges().getMaladie()) {
            logger.error("Requested sick leave days exceed available balance: {}", leaveRequest);
            return ResponseEntity.badRequest().body("Requested sick leave days exceed available balance");
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

    @GetMapping("/leave-request")
    public List<LeaveRequest> getCurrentLeaveRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        String username;
        if (principal instanceof UserDetailsImpl) {
            username = ((UserDetailsImpl) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
        }

        // Log the username of the authenticated user
        logger.info("Authenticated user username: {}", username);

        // Retrieve the leave request details from the database
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findByUsername(username);
        logger.info("LeaveRequests found: {}", leaveRequests);

        return leaveRequests;
    }

    @PostMapping("/signout")
    public ResponseEntity<Void> signout(HttpServletRequest request) {
        // Invalidate the token or perform server-side cleanup if necessary
        return ResponseEntity.ok().build();
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

    @PutMapping("/leave-requests/{id}/approve")
    public ResponseEntity<?> approveLeaveRequest(@PathVariable String id) {
        System.out.println("Début de l'approbation de la demande de congé avec ID : " + id);

        Optional<LeaveRequest> optionalLeaveRequest = leaveRequestRepository.findById(id);
        if (!optionalLeaveRequest.isPresent()) {
            System.out.println("Demande de congé non trouvée avec ID : " + id);
            return ResponseEntity.notFound().build();
        }

        LeaveRequest leaveRequest = optionalLeaveRequest.get();
        leaveRequest.setStatus("approuvé");
        leaveRequest.setDateValidation(new Date());
        System.out.println("Statut de la demande de congé mis à jour à 'approuvé'");

        Optional<Employe> optionalEmployee = employeRepository.findByUsername(leaveRequest.getUsername());
        if (optionalEmployee.isPresent()) {
            Employe employee = optionalEmployee.get();
            System.out.println("Employé trouvé : " + employee.getUsername());

            // Vérification de l'objet SoldeConges
            if (employee.getSolde_conges() == null) {
                System.out.println("Solde des congés est nul pour l'employé : " + employee.getUsername());
                // Initialiser l'objet SoldeConges si nécessaire
                employee.setSolde_conges(new Employe.SoldeConges());
            }

            // Log des soldes avant mise à jour
            System.out.println("Solde payé actuel : " + employee.getSolde_conges().getPayes());
            System.out.println("Solde non payé actuel : " + employee.getSolde_conges().getNon_payes());
            System.out.println("Solde maladie actuel : " + employee.getSolde_conges().getMaladie());

            switch (leaveRequest.getType()) {
                case "payé":
                    employee.getSolde_conges().setPayes(employee.getSolde_conges().getPayes() - leaveRequest.getNbrJourCong());
                    System.out.println("Solde payé mis à jour à : " + employee.getSolde_conges().getPayes());
                    break;
                case "non_payé":
                    employee.getSolde_conges().setNon_payes(employee.getSolde_conges().getNon_payes() - leaveRequest.getNbrJourCong());
                    System.out.println("Solde non payé mis à jour à : " + employee.getSolde_conges().getNon_payes());
                    break;
                case "maladie":
                    employee.getSolde_conges().setMaladie(employee.getSolde_conges().getMaladie() - leaveRequest.getNbrJourCong());
                    System.out.println("Solde maladie mis à jour à : " + employee.getSolde_conges().getMaladie());
                    break;
                default:
                    System.out.println("Type de congé non reconnu : " + leaveRequest.getType());
                    break;
            }
            employeRepository.save(employee);
            System.out.println("Solde des congés mis à jour et sauvegardé pour l'employé : " + employee.getUsername());
        } else {
            System.out.println("Employé non trouvé pour le username : " + leaveRequest.getUsername());
        }

        leaveRequestRepository.save(leaveRequest);
        System.out.println("Demande de congé mise à jour et sauvegardée");

        return ResponseEntity.ok().build();
    }

    @PutMapping("/leave-requests/{id}/reject")
    public ResponseEntity<?> rejectLeaveRequest(@PathVariable String id) {
        System.out.println("Début du rejet de la demande de congé avec ID : " + id);

        Optional<LeaveRequest> optionalLeaveRequest = leaveRequestRepository.findById(id);
        if (!optionalLeaveRequest.isPresent()) {
            System.out.println("Demande de congé non trouvée avec ID : " + id);
            return ResponseEntity.notFound().build();
        }

        LeaveRequest leaveRequest = optionalLeaveRequest.get();
        leaveRequest.setStatus("refusé");
        leaveRequest.setDateValidation(new Date());
        System.out.println("Statut de la demande de congé mis à jour à 'refusé'");

        leaveRequestRepository.save(leaveRequest);
        System.out.println("Demande de congé mise à jour et sauvegardée");

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/events")
    public SseEmitter streamEvents(@RequestParam String token) {
        if (!jwtUtils.validateJwtToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalide");
        }

        SseEmitter emitter = new SseEmitter();
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    @GetMapping("/dashboard/leave-request-count")
    public ResponseEntity<?> getLeaveRequestCounts() {
        long countPaid = leaveRequestRepository.countByType("payé");
        long countNonPaid = leaveRequestRepository.countByType("non_payé");
        long countSick = leaveRequestRepository.countByType("maladie");

        Map<String, Long> counts = new HashMap<>();
        counts.put("payé", countPaid);
        counts.put("non_payé", countNonPaid);
        counts.put("maladie", countSick);

        return ResponseEntity.ok(counts);
    }



}

