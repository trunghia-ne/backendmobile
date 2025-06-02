package org.example.mobilebackendjava.controller;

import org.example.mobilebackendjava.model.WatchHistory;
import org.example.mobilebackendjava.model.WatchHistoryRequest;
import org.example.mobilebackendjava.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // GET /api/movies/history?userId=xxxxx
    @GetMapping("/history")
    public List<WatchHistory> getWatchHistoryByUser(@RequestParam String userId) {
        return movieService.getWatchHistoryByUserId(userId);
    }

    // POST /api/movies/history
    @PostMapping("/history")
    public ResponseEntity<String> addWatchHistory(@RequestBody WatchHistoryRequest request) {
        String result = movieService.addWatchHistory(
                request.getUserId(),
                request.getMovieId(),
                request.getMovieTitle(),
                request.getTrailerUrl()
        );
        return ResponseEntity.ok(result);
    }
}
