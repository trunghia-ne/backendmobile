package org.example.mobilebackendjava.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.example.mobilebackendjava.model.Comment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    private static final String COLLECTION_NAME = "reviews";

    // ✅ Lấy đánh giá của 1 người dùng cho 1 bộ phim theo slug
    @GetMapping("/user/{userId}/movie/{slug}")
    public ResponseEntity<?> getUserReviewForMovie(
            @PathVariable String userId,
            @PathVariable String slug
    ) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .whereEqualTo("slug", slug)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        if (documents.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Comment comment = documents.get(0).toObject(Comment.class);
        comment.setId(documents.get(0).getId());
        return ResponseEntity.ok(comment);
    }

    // ✅ Gửi đánh giá mới hoặc cập nhật đánh giá hiện có
    @PostMapping
    public ResponseEntity<?> submitReview(@RequestBody Comment comment) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        // Kiểm tra đã đánh giá chưa dựa trên userId và slug
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                .whereEqualTo("userId", comment.getUserId())
                .whereEqualTo("slug", comment.getSlug())
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        // ✅ Gán thời gian tạo nếu chưa có
        if (comment.getTimestamp() == null) {
            comment.setTimestamp(new Date());
        }

        if (!documents.isEmpty()) {
            // Cập nhật đánh giá hiện có
            String existingId = documents.get(0).getId();
            comment.setId(existingId);

            Map<String, Object> updates = new HashMap<>();
            updates.put("comment", comment.getComment());
            updates.put("rating", comment.getRating());
            updates.put("timestamp", comment.getTimestamp());

            db.collection(COLLECTION_NAME).document(existingId).update(updates);

            System.out.println("🔄 Cập nhật đánh giá cho slug: " + comment.getSlug() + " bởi user: " + comment.getUserId());
            return ResponseEntity.ok(comment);
        } else {
            // Tạo đánh giá mới
            ApiFuture<DocumentReference> result = db.collection(COLLECTION_NAME).add(comment);
            String id = result.get().getId();
            comment.setId(id);

            System.out.println("✅ Tạo đánh giá mới cho slug: " + comment.getSlug() + " bởi user: " + comment.getUserId());
            return ResponseEntity.ok(comment);
        }
    }

    // ✅ Lấy danh sách đánh giá theo slug
    @GetMapping("/{slug}")
    public ResponseEntity<List<Comment>> getReviews(@PathVariable String slug) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                .whereEqualTo("slug", slug)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Comment> reviews = new ArrayList<>();
        for (DocumentSnapshot doc : documents) {
            Comment comment = doc.toObject(Comment.class);
            comment.setId(doc.getId());
            reviews.add(comment);
        }

        System.out.println("🔥 Lấy " + reviews.size() + " đánh giá cho phim slug: " + slug);
        return ResponseEntity.ok(reviews);
    }

    // ✅ Tính trung bình rating theo slug
    @GetMapping("/{slug}/average")
    public ResponseEntity<Map<String, Object>> getAverageRating(@PathVariable String slug) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                .whereEqualTo("slug", slug).get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        Map<String, Object> result = new HashMap<>();
        result.put("average", 0.0);
        result.put("count", 0);

        if (documents.isEmpty()) {
            return ResponseEntity.ok(result);
        }

        double total = 0;
        int count = 0;
        for (DocumentSnapshot doc : documents) {
            Double rating = doc.getDouble("rating");
            if (rating != null) {
                total += rating;
                count++;
            }
        }

        if (count > 0) {
            double average = total / count;
            result.put("average", Math.round(average * 10.0) / 10.0); // Làm tròn 1 chữ số thập phân
            result.put("count", count);
        }

        System.out.println("📊 Rating trung bình cho slug " + slug + ": " + result.get("average") + " (" + count + " đánh giá)");
        return ResponseEntity.ok(result);
    }

    // ✅ Xóa đánh giá theo ID
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable String reviewId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        try {
            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME).document(reviewId).delete();
            future.get();

            System.out.println("🗑️ Đã xóa đánh giá ID: " + reviewId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi xóa đánh giá: " + e.getMessage());
            return ResponseEntity.badRequest().body("Không thể xóa đánh giá");
        }
    }
}
