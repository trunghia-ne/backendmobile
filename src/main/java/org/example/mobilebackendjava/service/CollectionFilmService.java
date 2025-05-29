package org.example.mobilebackendjava.service;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.example.mobilebackendjava.model.CollectionFilm;
import org.example.mobilebackendjava.model.Movie;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class CollectionFilmService {
    private final Firestore db;

    public CollectionFilmService(Firestore db) {
        this.db = db;
    }

    public List<CollectionFilm> getAllCollections() {
        try {
            CollectionReference collectionRef = db.collection("collections");
            ApiFuture<QuerySnapshot> querySnapshot = collectionRef.get();

            List<CollectionFilm> result = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                CollectionFilm collection = document.toObject(CollectionFilm.class);
                // Nếu bạn muốn set ID thủ công từ document ID:
                collection.setId(document.getId());
                result.add(collection);
            }
            return result;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error while getting list of collections from Firestore!", e);
        }
    }

    public List<CollectionFilm> getCollectionsByUserId(String userId) {
        // Giả sử bạn có firestore db ở đây
        CollectionReference collectionReference = db.collection("collections");

        // Lọc theo userId
        Query query = collectionReference.whereEqualTo("userId", userId);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        List<CollectionFilm> result = new ArrayList<>();
        try {
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                CollectionFilm collectionFilm = document.toObject(CollectionFilm.class);
                collectionFilm.setId(document.getId());  // Gán document ID
                result.add(collectionFilm);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public List<Movie> getFilmsByCollectionId(String collectionId) {
        CollectionReference filmsRef = db.collection("collections")
                .document(collectionId)
                .collection("list_film");

        ApiFuture<QuerySnapshot> querySnapshot = filmsRef.get();

        List<Movie> result = new ArrayList<>();
        try {
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                Movie film = document.toObject(Movie.class);
                result.add(film);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public String addFilmToCollection(String collectionId, Movie film) {
        CollectionReference filmsRef = db.collection("collections")
                .document(collectionId)
                .collection("list_film");

        try {
            // Check nếu slug đã tồn tại
            Query query = filmsRef.whereEqualTo("slug", film.getSlug());
            ApiFuture<QuerySnapshot> querySnapshot = query.get();

            if (!querySnapshot.get().isEmpty()) {
                return "Phim đã tồn tại trong bộ sưu tập.";
            }

            // Thêm phim mới
            filmsRef.add(film);
            return "Phim đã được thêm vào bộ sưu tập.";
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Lỗi khi thêm phim: " + e.getMessage());
        }
    }

}
