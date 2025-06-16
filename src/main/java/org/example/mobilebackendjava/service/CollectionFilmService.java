package org.example.mobilebackendjava.service;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.example.mobilebackendjava.model.CollectionFilm;
import org.example.mobilebackendjava.model.Movie;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    public String addFilmToCollection(String collectionId, Movie film) throws ExecutionException, InterruptedException {
        // Tham chiếu tới collection con 'list_film' trong document 'collections/collectionId'
        CollectionReference filmsRef = db.collection("collections")
                .document(collectionId)
                .collection("list_film");

        // Kiểm tra slug phim đã tồn tại trong bộ sưu tập chưa
        Query query = filmsRef.whereEqualTo("slug", film.getSlug());
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        if (!querySnapshot.get().isEmpty()) {
            return "Phim đã tồn tại trong bộ sưu tập.";
        }

        // Thêm phim mới vào bộ sưu tập
        filmsRef.add(film);

        return "Phim đã được thêm vào bộ sưu tập.";
    }


    public boolean isFilmInCollection(String collectionId, String slug) {
        CollectionReference filmsRef = db.collection("collections")
                .document(collectionId)
                .collection("list_film");

        try {
            Query query = filmsRef.whereEqualTo("slug", slug);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();

            return !querySnapshot.get().isEmpty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Lỗi khi kiểm tra phim trong bộ sưu tập: " + e.getMessage());
        }
    }

    public boolean isFilmInAnyCollectionOfUser(String userId, String slug) {
        try {
            List<CollectionFilm> userCollections = getCollectionsByUserId(userId);
            System.out.println("✅ Tổng số bộ sưu tập của user " + userId + ": " + userCollections.size());

            for (CollectionFilm collection : userCollections) {
                String collectionId = collection.getId();
                System.out.println("🔍 Đang kiểm tra collection: " + collectionId);

                // Truy vấn subcollection list_film trong từng collection
                CollectionReference filmsRef = db.collection("collections")
                        .document(collectionId)
                        .collection("list_film");

                Query query = filmsRef.whereEqualTo("slug", slug);
                ApiFuture<QuerySnapshot> future = query.get(); // Bước 1: Lấy Future
                QuerySnapshot querySnapshot = future.get(10, TimeUnit.SECONDS); // Bước 2: Chờ kết quả có timeout

                List<QueryDocumentSnapshot> docs = querySnapshot.getDocuments();
                System.out.println("🔎 Số phim có slug '" + slug + "' trong collection " + collectionId + ": " + docs.size());

                if (!docs.isEmpty()) {
                    System.out.println("✅ Tìm thấy phim có slug '" + slug + "' trong collection " + collectionId);
                    return true;
                }
            }

            System.out.println("❌ Không tìm thấy phim '" + slug + "' trong bất kỳ bộ sưu tập nào của user.");
            return false;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("💥 Lỗi khi kiểm tra phim trong bộ sưu tập: " + e.getMessage(), e);
        } catch (TimeoutException e) {
            throw new RuntimeException("⏰ Truy vấn bị timeout! Có thể Firestore quá tải hoặc quá nhiều dữ liệu.", e);
        }
    }

    public boolean isCollectionExists(String collectionName, String userId) {
        try {
            CollectionReference collectionRef = db.collection("collections");
            Query query = collectionRef
                    .whereEqualTo("collection_name", collectionName)
                    .whereEqualTo("userId", userId);

            ApiFuture<QuerySnapshot> future = query.get();
            QuerySnapshot querySnapshot = future.get();

            return !querySnapshot.isEmpty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Lỗi khi kiểm tra collection tồn tại: " + e.getMessage(), e);
        }
    }


    public String addCollection(CollectionFilm collectionFilm, String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("UserId không được để trống");
        }
        collectionFilm.setUserId(userId);  // đảm bảo gán userId
        try {
            ApiFuture<DocumentReference> future = db.collection("collections").add(collectionFilm);
            String newId = future.get().getId();
            collectionFilm.setId(newId);
            return "Thêm bộ sưu tập thành công";
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Lỗi khi thêm bộ sưu tập: " + e.getMessage(), e);
        }
    }

    public String deleteCollection(String collectionId) {
        try {
            System.out.println("🔥 Đang xóa collectionId: " + collectionId);

            // Xoá toàn bộ phim trong subcollection list_film trước
            CollectionReference filmsRef = db.collection("collections")
                    .document(collectionId)
                    .collection("list_film");

            ApiFuture<QuerySnapshot> filmSnapshot = filmsRef.get();
            List<QueryDocumentSnapshot> filmDocs = filmSnapshot.get().getDocuments();

            for (QueryDocumentSnapshot doc : filmDocs) {
                System.out.println("➡️ Xoá phim: " + doc.getId());
                filmsRef.document(doc.getId()).delete();
            }

            // Xóa collection chính
            ApiFuture<WriteResult> writeResult = db.collection("collections")
                    .document(collectionId)
                    .delete();

            System.out.println("✅ Đã xóa collectionId: " + collectionId);
            return "Xóa bộ sưu tập thành công";
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("💥 Lỗi khi xóa bộ sưu tập: " + e.getMessage());
            throw new RuntimeException("Lỗi khi xóa bộ sưu tập: " + e.getMessage(), e);
        }
    }


    public String deleteFilmFromCollection(String collectionId, String slug) {
        try {
            System.out.println("🛠 Đang xóa phim với slug = " + slug + " trong collectionId = " + collectionId);

            if (slug == null || slug.trim().isEmpty()) {
                return "Slug không hợp lệ.";
            }

            CollectionReference filmsRef = db.collection("collections")
                    .document(collectionId)
                    .collection("list_film");

            // Truy vấn phim theo slug
            Query query = filmsRef.whereEqualTo("slug", slug);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            List<QueryDocumentSnapshot> docs = querySnapshot.get().getDocuments();

            if (docs.isEmpty()) {
                System.out.println("❌ Không tìm thấy slug: " + slug);
                return "Không tìm thấy phim trong bộ sưu tập.";
            }

            for (QueryDocumentSnapshot doc : docs) {
                System.out.println("✅ Xóa phim documentId: " + doc.getId());
                filmsRef.document(doc.getId()).delete(); // không cần đợi, async là đủ
            }

            return "Xóa phim khỏi bộ sưu tập thành công";
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("💥 Lỗi Firestore khi xóa phim: " + e.getMessage());
            throw new RuntimeException("Lỗi khi xóa phim khỏi bộ sưu tập: " + e.getMessage(), e);
        }
    }
}
