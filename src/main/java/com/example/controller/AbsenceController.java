package com.example.controller;

import com.example.resources.Absence;
import com.example.repository.AbsenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/absences")
public class AbsenceController {

    @Autowired
    private AbsenceRepository absenceRepository;

    private final Path rootLocation = Paths.get("upload-dir");

    // Ajouter une nouvelle absence
    @PostMapping
    public ResponseEntity<Absence> createAbsence(@RequestBody Absence absence) {
        absence.setCreationDate(new Date());
        absence.setLastModifiedDate(new Date());
        Absence savedAbsence = absenceRepository.save(absence);
        return ResponseEntity.ok(savedAbsence);
    }

    // Obtenir toutes les absences
    @GetMapping
    public List<Absence> getAllAbsences() {
        return absenceRepository.findAll();
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Absence>> getAbsencesByEmployeeId(@PathVariable String employeeId) {
        List<Absence> absences = absenceRepository.findByEmployeeId(employeeId);
        return ResponseEntity.ok(absences);
    }

    // Obtenir une absence par ID
    @GetMapping("/{id}")
    public ResponseEntity<Absence> getAbsenceById(@PathVariable String id) {
        Optional<Absence> absence = absenceRepository.findById(id);
        return absence.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Mettre à jour une absence
    @PutMapping("/{id}")
    public ResponseEntity<Absence> updateAbsence(@PathVariable String id, @RequestBody Absence absenceDetails) {
        Optional<Absence> optionalAbsence = absenceRepository.findById(id);

        if (optionalAbsence.isPresent()) {
            Absence absence = optionalAbsence.get();
            absence.setEmployeeId(absenceDetails.getEmployeeId());
            absence.setDate(absenceDetails.getDate());
            absence.setType(absenceDetails.getType());
            absence.setJustification(absenceDetails.getJustification());
            absence.setAuthorized(absenceDetails.getAuthorized());
            absence.setJustificationAccepted(absenceDetails.getJustificationAccepted());
            absence.setLastModifiedDate(new Date());

            Absence updatedAbsence = absenceRepository.save(absence);
            return ResponseEntity.ok(updatedAbsence);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Supprimer une absence
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAbsence(@PathVariable String id) {
        Optional<Absence> optionalAbsence = absenceRepository.findById(id);

        if (optionalAbsence.isPresent()) {
            absenceRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/{id}/accept")
    public ResponseEntity<Absence> acceptAbsence(@PathVariable String id) {
        Optional<Absence> optionalAbsence = absenceRepository.findById(id);
        if (optionalAbsence.isPresent()) {
            Absence absence = optionalAbsence.get();
            absence.setJustificationAccepted("accepté");
            absence.setLastModifiedDate(new Date());
            Absence updatedAbsence = absenceRepository.save(absence);
            return ResponseEntity.ok(updatedAbsence);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Refuser une absence
    @PutMapping("/{id}/reject")
    public ResponseEntity<Absence> rejectAbsence(@PathVariable String id) {
        Optional<Absence> optionalAbsence = absenceRepository.findById(id);
        if (optionalAbsence.isPresent()) {
            Absence absence = optionalAbsence.get();
            absence.setJustificationAccepted("refusé");
            absence.setLastModifiedDate(new Date());
            Absence updatedAbsence = absenceRepository.save(absence);
            return ResponseEntity.ok(updatedAbsence);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Obtenir les absences en attente
    @GetMapping("/pending")
    public ResponseEntity<List<Absence>> getPendingAbsences() {
        List<Absence> absences = absenceRepository.findByJustificationAccepted("En attente");
        return ResponseEntity.ok(absences);
    }
    @PutMapping("/{id}/admin-approve")
    public ResponseEntity<?> supervisorApproveAbsence(@PathVariable String id) {
        Optional<Absence> absenceOptional = absenceRepository.findById(id);
        if (!absenceOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Absence absence = absenceOptional.get();
        absence.setSupervisorApproved(true);
        absenceRepository.save(absence);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/employee/{employeeId}/absences")
    public ResponseEntity<List<Absence>> getAbsencesByEmployeeIdAndStatus(
            @PathVariable String employeeId,
            @RequestParam(required = false) String justificationAccepted) {

        List<Absence> absences;

        if (justificationAccepted != null && !justificationAccepted.isEmpty()) {
            absences = absenceRepository.findByEmployeeIdAndJustificationAccepted(employeeId, justificationAccepted);
        } else {
            absences = absenceRepository.findByEmployeeId(employeeId);
        }

        return ResponseEntity.ok(absences);
    }

}

