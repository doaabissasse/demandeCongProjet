package com.example;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBTest {

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            // Accéder à la base de données
            MongoDatabase database = mongoClient.getDatabase("dem_cong");
            System.out.println("Connexion réussie à la base de données: " + database.getName());

            // Accéder à la collection 'salarier'
            MongoCollection<Document> collection = database.getCollection("salarier");

            // Afficher tous les documents dans la collection
            long count = collection.countDocuments();
            System.out.println("Nombre de documents dans la collection 'salarier': " + count);

            for (Document doc : collection.find()) {
                System.out.println(doc.toJson()); // Convertir le document en JSON et l'afficher
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}