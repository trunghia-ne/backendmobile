package org.example.mobilebackendjava.controller;

import org.example.mobilebackendjava.model.CollectionFilm;
import org.example.mobilebackendjava.model.Movie;
import org.example.mobilebackendjava.service.CollectionFilmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FavoritesController {
    CollectionFilmService collectionFilmService;

    public FavoritesController(CollectionFilmService collectionFilmService) {
        this.collectionFilmService = collectionFilmService;
    }

    @GetMapping("/getAllCollections")
    public List<CollectionFilm> getAllUsers()  {
        return collectionFilmService.getAllCollections();
    }

    // Lấy collections theo userId (dùng query param)
    @GetMapping("/getCollectionsByUser")
    public List<CollectionFilm> getCollectionsByUserId(@RequestParam String userId) {
        return collectionFilmService.getCollectionsByUserId(userId);
    }

    // API lấy phim theo collectionId
    @GetMapping("/getFilmsByCollectionId")
    public List<Movie> getFilmsByCollectionId(@RequestParam String collectionId) {
        return collectionFilmService.getFilmsByCollectionId(collectionId);
    }

    @PostMapping("/addFilmToCollection")
    public ResponseEntity<Map<String, String>> addFilmToCollection(
            @RequestParam String collectionId,
            @RequestBody Movie film) {
        Map<String, String> response = new HashMap<>();
        try {
            String result = collectionFilmService.addFilmToCollection(collectionId, film);

            if (result.contains("đã tồn tại")) {
                response.put("message", result);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Lỗi khi thêm phim vào bộ sưu tập: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    @GetMapping("/checkFilmInCollection")
    public ResponseEntity<Boolean> isFilmInCollection(
            @RequestParam String collectionId,
            @RequestParam String slug) {
        boolean exists = collectionFilmService.isFilmInCollection(collectionId, slug);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/checkFilmInUserCollections")
    public ResponseEntity<Boolean> isFilmInUserCollections(
            @RequestParam String userId,
            @RequestParam String slug) {
        boolean exists = collectionFilmService.isFilmInAnyCollectionOfUser(userId, slug);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/addCollection")
    public ResponseEntity<Map<String, String>> addCollection(@RequestParam String userId, @RequestBody CollectionFilm collectionFilm) {
        Map<String, String> response = new HashMap<>();

        try {
            if (collectionFilmService.isCollectionExists(collectionFilm.getCollection_name(),userId)) {
                response.put("message", "Bộ sưu tập đã tồn tại!");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            collectionFilmService.addCollection(collectionFilm, userId);
            response.put("message", "Thêm bộ sưu tập thành công");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Lỗi server: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
