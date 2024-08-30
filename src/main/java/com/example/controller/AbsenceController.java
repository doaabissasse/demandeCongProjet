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

    // Chemin pour le répertoire de téléchargement des fichiers (utilisé si nécessaire pour gérer les fichiers associés aux absences)
    private final Path rootLocation = Paths.get("upload-dir");

    // Ajouter une nouvelle absence
    // Cette méthode reçoit une demande POST avec les détails de l'absence, la sauvegarde dans la base de données,
    // et retourne l'absence sauvegardée avec un code de statut HTTP 200.
    @PostMapping
    public ResponseEntity<Absence> createAbsence(@RequestBody Absence absence) {
        // Ajouter les dates de création et de dernière modification avant de sauvegarder
        absence.setCreationDate(new Date());
        absence.setLastModifiedDate(new Date());
        Absence savedAbsence = absenceRepository.save(absence);
        return ResponseEntity.ok(savedAbsence);
    }

    // Obtenir toutes les absences
    // Cette méthode retourne une liste de toutes les absences enregistrées dans la base de données.
    @GetMapping
    public List<Absence> getAllAbsences() {
        return absenceRepository.findAll();
    }

    // Obtenir les absences d'un employé spécifique
    // Cette méthode retourne toutes les absences d'un employé en particulier en utilisant son identifiant.
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Absence>> getAbsencesByEmployeeId(@PathVariable String employeeId) {
        List<Absence> absences = absenceRepository.findByEmployeeId(employeeId);
        return ResponseEntity.ok(absences);
    }

    // Obtenir une absence par ID
    // Cette méthode permet de récupérer les détails d'une absence spécifique à l'aide de son identifiant unique.
    @GetMapping("/{id}")
    public ResponseEntity<Absence> getAbsenceById(@PathVariable String id) {
        Optional<Absence> absence = absenceRepository.findById(id);
        return absence.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Mettre à jour une absence
    // Cette méthode permet de mettre à jour les détails d'une absence existante.
    @PutMapping("/{id}")
    public ResponseEntity<Absence> updateAbsence(@PathVariable String id, @RequestBody Absence absenceDetails) {
        Optional<Absence> optionalAbsence = absenceRepository.findById(id);

        if (optionalAbsence.isPresent()) {
            Absence absence = optionalAbsence.get();
            // Mise à jour des informations de l'absence avec les nouvelles données
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
    // Cette méthode permet de supprimer une absence de la base de données à l'aide de son identifiant.
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

    // Accepter une absence
    // Cette méthode permet de marquer une absence comme acceptée.
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
    // Cette méthode permet de marquer une absence comme refusée.
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
    // Cette méthode retourne toutes les absences qui sont encore en attente de décision.
    @GetMapping("/pending")
    public ResponseEntity<List<Absence>> getPendingAbsences() {
        List<Absence> absences = absenceRepository.findByJustificationAccepted("En attente");
        return ResponseEntity.ok(absences);
    }

    // Approbation par l'administrateur
    // Cette méthode permet à un administrateur de valider une absence.
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

    // Obtenir les absences d'un employé avec filtrage par statut de justification
    // Cette méthode permet de filtrer les absences d'un employé par leur statut de justification ("accepté", "refusé", etc.).
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


