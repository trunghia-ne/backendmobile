package org.example.mobilebackendjava.controller;

import org.example.mobilebackendjava.model.CollectionFilm;
import org.example.mobilebackendjava.model.Movie;
import org.example.mobilebackendjava.service.CollectionFilmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<String> addFilmToCollection(
            @RequestParam String collectionId,
            @RequestBody Movie film) {

        String result = collectionFilmService.addFilmToCollection(collectionId, film);

        if (result.contains("đã tồn tại")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }

        return ResponseEntity.ok(result);
    }
}
