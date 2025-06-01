package org.example.mobilebackendjava.controller;

import org.example.mobilebackendjava.model.WatchHistory;
import org.example.mobilebackendjava.service.MovieService;
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
        return movieService.getAllWatchHistories(userId);
    }

    @PostMapping("/addWatchHistory")
    public void addWatchHistorie(@RequestBody WatchHistory history){
        movieService.addWatchHistory(history);
    }
}
