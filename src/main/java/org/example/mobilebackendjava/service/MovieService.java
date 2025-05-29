package org.example.mobilebackendjava.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.example.mobilebackendjava.model.FavoriteMovie;
import org.example.mobilebackendjava.model.WatchHistory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class MovieService {
    private final Firestore db;

    public MovieService(Firestore db) {
        this.db = db;
    }

    // Lấy danh sách phim yêu thích của 1 user
    public List<FavoriteMovie> getAllFavoriteMovies(String userId) {
        List<FavoriteMovie> favoriteMovies = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection("favoriteMovies")
                    .whereEqualTo("userId", userId)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot doc : documents) {
                FavoriteMovie favoriteMovie = doc.toObject(FavoriteMovie.class);
                favoriteMovies.add(favoriteMovie);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Query interrupted. Please try again!");
        } catch (ExecutionException e) {
            throw new RuntimeException("Error while querying data from Firestore: " + e.getMessage());
        }

        return favoriteMovies;
    }
    // Lấy danh sách lịch sử xem phim của 1 user
    public List<WatchHistory> getAllWatchHistories(String userId) {
        List<WatchHistory> watchHistories = new ArrayList<>();
        try{
           ApiFuture<QuerySnapshot> future = db.collection("watchHistories")
                   .whereEqualTo("userId",userId)
                   .get();
           List<QueryDocumentSnapshot> document = future.get().getDocuments();
           for (QueryDocumentSnapshot doc : document) {
               WatchHistory watchHistory = doc.toObject(WatchHistory.class);
               watchHistories.add(watchHistory);
           }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Query interrupted. Please try again!");
        } catch (ExecutionException e) {
            throw new RuntimeException("Error while querying data from Firestore: " + e.getMessage());
        }
        return watchHistories;
    }

    // Thêm 1 bộ phim yêu thích
    public void addFavoriteMovie(FavoriteMovie movie) {
        try {
            ApiFuture<DocumentReference> future = db.collection("favoriteMovies").add(movie);
            future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Movie adding process interrupted!");
        } catch (ExecutionException e) {
            throw new RuntimeException("Error adding movie to favorites list: " + e.getCause().getMessage());
        }
    }
    // Thêm 1 lịch sử xem phim
    public void addWatchHistory(WatchHistory history) {
        try{
            ApiFuture<DocumentReference> future = db.collection("watchHistories").add(history);
            future.get();
        } catch (ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("History adding process interrupted!");
        } catch (InterruptedException e) {
            throw new RuntimeException("Error adding movie viewing history: " + e.getCause().getMessage());
        }
    }
}
