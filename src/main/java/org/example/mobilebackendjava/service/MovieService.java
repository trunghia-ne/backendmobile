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
import java.util.List;

@Service
public class MovieService {
    private final Firestore db;

    public MovieService(Firestore db) {
        this.db = db;
    }

    private static final String COLLECTION_NAME = "watch_history";

    public List<WatchHistory> getWatchHistoryByUserId(String userId) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        List<WatchHistory> watchHistoryList = new ArrayList<>();

        try {
            ApiFuture<QuerySnapshot> future = dbFirestore.collection(COLLECTION_NAME)
                    .whereEqualTo("userId", userId)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot document : documents) {
                WatchHistory watchHistory = document.toObject(WatchHistory.class);
                watchHistoryList.add(watchHistory);
            }

            return watchHistoryList;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy lịch sử xem: " + e.getMessage());
        }
    }

    public String addWatchHistory(String userId, String movieId, String movieTitle, String trailerUrl) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        WatchHistory watchHistory = new WatchHistory(userId, movieId, movieTitle, trailerUrl, System.currentTimeMillis());

        try {
            ApiFuture<WriteResult> future = dbFirestore.collection(COLLECTION_NAME).document().set(watchHistory);
            return future.get().getUpdateTime().toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu lịch sử xem: " + e.getMessage());
        }
    }
}
