package org.example.mobilebackendjava.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteMovie {
    private String userId;
    private String movieId;
    private String title;
    private String posterUrl;
    private String addedDate;

}

