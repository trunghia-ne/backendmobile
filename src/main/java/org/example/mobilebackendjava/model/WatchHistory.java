package org.example.mobilebackendjava.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatchHistory {
    private String userId;
    private String movieId;
    private String movieTitle;
    private String trailerUrl;
    private long watchTime;
}