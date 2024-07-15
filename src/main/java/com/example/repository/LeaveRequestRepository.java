package com.example.repository;

import com.example.resources.LeaveRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRequestRepository extends MongoRepository<LeaveRequest, String> {
}
