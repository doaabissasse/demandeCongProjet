package com.example.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "LeaveRequest")
@JsonIgnoreProperties(value = {"_class"})
public class LeaveRequest {
    @Id
    private String id;
    private String username;
    private String emplnom;
    private String emplprenom;
    private String emplCIN;
    private String departemant;
    private String Email;
    private String Tele;
    private String type;
    private Date startDate;
    private Date endDate;
    private String status;
    private String Remarque;
    private int NbrJourCong;
    private Date DateDemande;
    private Date DateValidation;

    public LeaveRequest() {}

    public LeaveRequest(String id,String username,String emplnom,String emplprenom, String emplCIN,String departemant,
                        String Email,String Tele,String type, Date startDate
            , Date endDate, String status,String Remarque,int NbrJourCong,Date DateDemande,Date DateValidation) {
        this.id = id;
        this.username = username;
        this.emplnom = emplnom;
        this.emplprenom = emplprenom;
        this.emplCIN = emplCIN;
        this.departemant = departemant;
        this.Email = Email;
        this.Tele = Tele;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.Remarque = Remarque;
        this.status = status;
        this.NbrJourCong = NbrJourCong;
        this.DateDemande = DateDemande;
        this.DateValidation = DateValidation;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmplnom() {
        return emplnom;
    }

    public void setEmplnom(String emplnom) {
        this.emplnom = emplnom;
    }

    public String getEmplprenom() {
        return emplprenom;
    }

    public void setEmplprenom(String emplprenom) {
        this.emplprenom = emplprenom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmplCIN() {
        return emplCIN;
    }

    public void setEmplCIN(String emplCIN) {
        this.emplCIN = emplCIN;
    }

    public String getDepartemant() {
        return departemant;
    }

    public void setDepartemant(String departemant) {
        this.departemant = departemant;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getTele() {
        return Tele;
    }

    public void setTele(String tele) {
        Tele = tele;
    }

    public String getRemarque() {
        return Remarque;
    }

    public void setRemarque(String remarque) {
        Remarque = remarque;
    }

    public int getNbrJourCong() {
        return NbrJourCong;
    }

    public void setNbrJourCong(int nbrJourCong) {
        NbrJourCong = nbrJourCong;
    }

    public Date getDateDemande() {
        return DateDemande;
    }

    public void setDateDemande(Date dateDemande) {
        DateDemande = dateDemande;
    }

    public Date getDateValidation() {
        return DateValidation;
    }

    public void setDateValidation(Date dateValidation) {
        DateValidation = dateValidation;
    }
}
