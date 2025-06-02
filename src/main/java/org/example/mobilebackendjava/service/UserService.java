package org.example.mobilebackendjava.service;

import ch.qos.logback.classic.Logger;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import io.grpc.StatusRuntimeException;
import org.example.mobilebackendjava.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {
    private final Firestore db;

    public UserService(Firestore db) {
        this.db = db;
    }

    // Phương thức lấy danh sách User từ Cloude Firestore
    public List<User> getAllUsers() {
        try {

            CollectionReference collectionReference = db.collection("users");

            ApiFuture<QuerySnapshot> querySnapshot = collectionReference.get();

            List<User> result = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                User user = document.toObject(User.class);
                result.add(user);
            }
            return result;
        } catch (ExecutionException | InterruptedException e) {
            Throwable cause = e.getCause();
            if (cause instanceof StatusRuntimeException) {
                StatusRuntimeException statusEx = (StatusRuntimeException) cause;
                // Log thêm thông tin về status code
                Logger logger = null;
                logger.error("Firestore connection failed: {}", statusEx.getStatus(), statusEx);
            }
            throw new RuntimeException("ExecutionException while retrieving users from Firestore: " + cause, e);
        }


    }

    // Phương thức lấy User từ Cloude Firestore bằng id
    public User getUserById(String id) {
        try {

            DocumentSnapshot document = db.collection("users").document(id).get().get();
            if (document.exists()) {
                return document.toObject(User.class);
            } else {
                throw new RuntimeException("User with id " + id + " not found!");
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    // Cập nhật thông tin User
    public void updateUser(String id, User updatedUser) {
        try {
            db.collection("users").document(id).set(updatedUser).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error while updating user with id " + id, e);
        }
    }


}



