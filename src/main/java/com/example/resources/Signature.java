package com.example.resources;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "signatures")
public class Signature {
    @Id
    private String id;
    private String userId;
    private String signatureUrl; // URL de l'image de la signature

    public Signature() {
    }

    public Signature(String signatureUrl,String id,String userId) {
        this.id = id;
        this.userId = userId;
        this.signatureUrl = signatureUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSignatureUrl() {
        return signatureUrl;
    }

    public void setSignatureUrl(String signatureUrl) {
        this.signatureUrl = signatureUrl;
    }
}