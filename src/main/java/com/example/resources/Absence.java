package com.example.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "Absences")
@JsonIgnoreProperties(value = {"_class"})
public class Absence {
    @Id
    private String id;
    private String employeeId;
    private Date date;
    private String type;
    private String authorized;
    private String justification;
    private String justificationType;
    private String justificationAccepted;
    private Date creationDate;
    private Date lastModifiedDate;
    private boolean supervisorApproved;


    public Absence() {
    }

    // Constructeur
    public Absence(String id, String employeeId,String justificationType, Date date,String authorized,String justificationAccepted,String type,String justification,Date creationDate,Date lastModifiedDate) {
        this.id = id;
        this.employeeId = employeeId;
        this.date = date;
        this.type = type;
        this.justificationType=justificationType;
        this.authorized = authorized; // Initialement non justifié
        this.justification = justification; // Justification initialement nulle
        this.justificationAccepted = justificationAccepted; // Justification non encore acceptée
        this.creationDate = creationDate;
        this.lastModifiedDate = lastModifiedDate;
        this. supervisorApproved =  false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthorized() {
        return authorized;
    }

    public void setAuthorized(String authorized) {
        this.authorized = authorized;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getJustificationAccepted() {
        return justificationAccepted;
    }

    public void setJustificationAccepted(String justificationAccepted) {
        this.justificationAccepted = justificationAccepted;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getJustificationType() {
        return justificationType;
    }

    public void setJustificationType(String justificationType) {
        this.justificationType = justificationType;
    }
    public boolean isSupervisorApproved() {
        return supervisorApproved;
    }

    public void setSupervisorApproved(boolean supervisorApproved) {
        this.supervisorApproved = supervisorApproved;
    }
}