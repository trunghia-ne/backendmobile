package org.example.mobilebackendjava.controller;

import org.example.mobilebackendjava.model.WatchHistory;
import org.example.mobilebackendjava.model.WatchHistoryRequest;
import org.example.mobilebackendjava.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movie")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/getAllWatchHistories")
    public List<WatchHistory> getAllWatchHistories(String userId){
        return movieService.getWatchHistory(userId);
    }


    @PostMapping("/addWatchHistory")
    public ResponseEntity<String> saveWatchHistory(@RequestBody WatchHistoryRequest request) {
        try {
            String result = movieService.saveWatchHistory(
                    request.getUserId(),
                    request.getMovieId(),
                    request.getMovieTitle(),
                    request.getTrailerUrl()
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving watch history: " + e.getMessage());
        }
    }


}
