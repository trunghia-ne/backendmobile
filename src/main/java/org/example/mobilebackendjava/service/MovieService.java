package org.example.mobilebackendjava.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.example.mobilebackendjava.model.WatchHistory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MovieService {
    private final Firestore db;

    public MovieService(Firestore db) {
        this.db = db;
    }

    private static final String COLLECTION_NAME = "watch_history";

    // Lấy danh sách lịch sử xem phim của 1 user
    public List<WatchHistory> getWatchHistory(String userId) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        List<WatchHistory> watchHistoryList = new ArrayList<>();

        try {
            // Query the watch_history collection where userId matches
            ApiFuture<QuerySnapshot> future = dbFirestore.collection(COLLECTION_NAME)
                    .whereEqualTo("userId", userId)
                    .get();

            // Get the query results
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            // Convert each document to WatchHistory object
            for (QueryDocumentSnapshot document : documents) {
                WatchHistory history = document.toObject(WatchHistory.class);
                watchHistoryList.add(history);
            }

            return watchHistoryList;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList(); // Return empty list in case of error
        }
    }

    // Thêm 1 lịch sử xem phim
    public String saveWatchHistory(String userId, String movieId, String movieTitle, String trailerUrl) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        WatchHistory history = new WatchHistory(userId, movieId, movieTitle, trailerUrl, System.currentTimeMillis());

        ApiFuture<WriteResult> future = dbFirestore.collection(COLLECTION_NAME).document().set(history);
        try {
            return future.get().getUpdateTime().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
