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
    private static final String USERS_COLLECTION_NAME = "users";

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

        System.out.println("🔥 Lấy " + reviews.size() + " đánh giá cho phim slug: " + slug + " lúc " + new Date());
        return ResponseEntity.ok(reviews);
    }

    // ✅ Tính trung bình rating theo slug
    @GetMapping("/{slug}/average")
    public ResponseEntity<Map<String, Object>> getAverageRating(@PathVariable String slug) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                .whereEqualTo("slug", slug)
                .get();

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
            Object ratingObj = doc.get("rating");
            if (ratingObj instanceof Number) {
                double rating = ((Number) ratingObj).doubleValue();
                total += rating;
                count++;
            }
        }

        if (count > 0) {
            double average = total / count;
            result.put("average", Math.round(average * 10.0) / 10.0);
            result.put("count", count);
        }

        System.out.println("📊 Rating trung bình cho slug " + slug + ": " + result.get("average") + " (" + count + " đánh giá) lúc " + new Date());
        return ResponseEntity.ok(result);
    }

    // ✅ Gửi đánh giá mới hoặc cập nhật đánh giá hiện có
    @PostMapping
    public ResponseEntity<?> submitReview(@RequestBody Comment comment) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        if (comment.getRating() <= 0 || comment.getSlug() == null || comment.getUserId() == null) {
            return ResponseEntity.badRequest().body("Rating must be positive, and slug/userId are required");
        }

        // Lấy tên người dùng từ collection users
        DocumentSnapshot userDoc = db.collection(USERS_COLLECTION_NAME).document(comment.getUserId()).get().get();
        String username = userDoc.exists() ? userDoc.getString("name") : "Người dùng";
        comment.setUsername(username != null ? username : "Người dùng");

        // Gán thời gian tạo nếu chưa có
        if (comment.getTimestamp() == null) {
            comment.setTimestamp(new Date());
        }

        // Kiểm tra đã đánh giá chưa dựa trên userId và slug
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                .whereEqualTo("userId", comment.getUserId())
                .whereEqualTo("slug", comment.getSlug())
                .limit(1)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        if (!documents.isEmpty()) {
            // Cập nhật đánh giá hiện có
            String existingId = documents.get(0).getId();
            comment.setId(existingId);

            Map<String, Object> updates = new HashMap<>();
            updates.put("comment", comment.getComment());
            updates.put("rating", comment.getRating());
            updates.put("timestamp", comment.getTimestamp());
            updates.put("username", comment.getUsername());

            db.collection(COLLECTION_NAME).document(existingId).update(updates).get();

            System.out.println("🔄 Cập nhật đánh giá cho slug: " + comment.getSlug() + " bởi user: " + comment.getUserId() + " lúc " + new Date());
            return ResponseEntity.ok(comment);
        } else {
            // Tạo đánh giá mới
            ApiFuture<DocumentReference> result = db.collection(COLLECTION_NAME).add(comment);
            String id = result.get().getId();
            comment.setId(id);

            System.out.println("✅ Tạo đánh giá mới cho slug: " + comment.getSlug() + " bởi user: " + comment.getUserId() + " lúc " + new Date());
            return ResponseEntity.ok(comment);
        }
    }

    // ✅ Lấy đánh giá của 1 người dùng cho 1 bộ phim theo slug
    @GetMapping("/user/{userId}/movie/{slug}")
    public ResponseEntity<?> getUserReview(@PathVariable String userId, @PathVariable String slug) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .whereEqualTo("slug", slug)
                .limit(1)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        if (documents.isEmpty()) {
            System.out.println("⚠️ Không tìm thấy đánh giá cho userId: " + userId + " và slug: " + slug + " lúc " + new Date());
            return ResponseEntity.notFound().build();
        }

        Comment comment = documents.get(0).toObject(Comment.class);
        comment.setId(documents.get(0).getId());
        return ResponseEntity.ok(comment);
    }

    // ✅ Xóa đánh giá theo ID
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable String reviewId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        try {
            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME).document(reviewId).delete();
            future.get();

            System.out.println("🗑️ Đã xóa đánh giá ID: " + reviewId + " lúc " + new Date());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi xóa đánh giá: " + e.getMessage() + " lúc " + new Date());
            return ResponseEntity.badRequest().body("Không thể xóa đánh giá");
        }
    }

    // ✅ Thêm phản hồi cho bình luận
    @PostMapping("/reply")
    public ResponseEntity<?> addReply(@RequestBody Comment reply) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        if (reply.getParentId() == null || reply.getUserId() == null || reply.getComment() == null) {
            return ResponseEntity.badRequest().body("ParentId, userId, and comment are required");
        }

        // Lấy tên người dùng từ collection users
        DocumentSnapshot userDoc = db.collection(USERS_COLLECTION_NAME).document(reply.getUserId()).get().get();
        String username = userDoc.exists() ? userDoc.getString("name") : "Người dùng";
        reply.setUsername(username != null ? username : "Người dùng");

        // Gán thời gian tạo nếu chưa có
        if (reply.getTimestamp() == null) {
            reply.setTimestamp(new Date());
        }

        ApiFuture<DocumentReference> result = db.collection(COLLECTION_NAME).add(reply);
        String id = result.get().getId();
        reply.setId(id);

        System.out.println("✅ Thêm phản hồi cho parentId: " + reply.getParentId() + " bởi user: " + reply.getUserId() + " lúc " + new Date());
        return ResponseEntity.ok(reply);
    }

    // ✅ Lấy danh sách phản hồi cho bình luận
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<Comment>> getReplies(@PathVariable String commentId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                .whereEqualTo("parentId", commentId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Comment> replies = new ArrayList<>();
        for (DocumentSnapshot doc : documents) {
            Comment comment = doc.toObject(Comment.class);
            comment.setId(doc.getId());
            replies.add(comment);
        }

        System.out.println("🔥 Lấy " + replies.size() + " phản hồi cho commentId: " + commentId + " lúc " + new Date());
        return ResponseEntity.ok(replies);
    }

    // ✅ Cập nhật phản hồi
    @PutMapping("/{reviewId}/reply")
    public ResponseEntity<?> updateReply(@PathVariable String reviewId, @RequestBody Comment updatedReply) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        if (updatedReply.getComment() == null || updatedReply.getUserId() == null) {
            return ResponseEntity.badRequest().body("Comment and userId are required");
        }

        DocumentSnapshot reviewDoc = db.collection(COLLECTION_NAME).document(reviewId).get().get();
        if (!reviewDoc.exists()) {
            return ResponseEntity.notFound().build();
        }

        Comment existingReply = reviewDoc.toObject(Comment.class);
        if (!existingReply.getUserId().equals(updatedReply.getUserId())) {
            return ResponseEntity.status(403).body("You can only edit your own reply");
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("comment", updatedReply.getComment());
        updates.put("timestamp", new Date());

        db.collection(COLLECTION_NAME).document(reviewId).update(updates).get();

        System.out.println("🔄 Cập nhật phản hồi ID: " + reviewId + " bởi user: " + updatedReply.getUserId() + " lúc " + new Date());
        return ResponseEntity.ok(updatedReply);
    }
}