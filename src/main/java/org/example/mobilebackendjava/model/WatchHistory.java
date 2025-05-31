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
    private String title;
    private String posterUrl;
    private String watchedAt;       // ISO format: yyyy-MM-ddTHH:mm:ssZ
    private int progress;           // % đã xem
    private int duration;           // tổng thời lượng (giây hoặc phút)
    private int currentPosition;    // vị trí đang xem đến (giây hoặc phút)
}