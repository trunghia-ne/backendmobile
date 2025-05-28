package org.example.mobilebackendjava.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    @Autowired
    private Firestore firestore;

    // Phương thức tạo user
    public void saveUser(String id, String name, String email) {
        Map<String, Object> user = Map.of(
                "id", id,
                "name", name,
                "email", email
        );
        firestore.collection("users").document(id).set(user);
    }

    // Phương thức lấy user bằng id
    public Map<String, Object> getUserById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("users").document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return document.getData();
        } else {
            throw new RuntimeException("User not found!");
        }
    }
}
