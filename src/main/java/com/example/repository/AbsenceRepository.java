package com.example.repository;

import com.example.resources.Absence;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AbsenceRepository extends MongoRepository<Absence, String> {
    List<Absence> findByEmployeeId(String employeeId);

    List<Absence> findByJustificationAccepted(String enAttente);

    List<Absence> findByEmployeeIdAndJustificationAccepted(String employeeId, String justificationAccepted);
}
