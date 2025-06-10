package org.example.mobilebackendjava.model;

import java.util.Date;

public class Comment {
    private String id;           // ID trên server, cần thiết cho update/delete
    private String username;
    private String comment;
    private Date timestamp;     // Thay createdAt để khớp với server
    private double rating;
    private String userId;
    private String slug;        // Dùng slug thay cho movieId
    private String movieTitle;
    private String parentId;    // Thêm trường để hỗ trợ phản hồi (replies)

    // Constructors
    public Comment() {}

    public Comment(String username, String comment, double rating, Date timestamp) {
        this.username = username;
        this.comment = comment;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    public Comment(String username, String comment, double rating, Date timestamp, String userId, String slug, String movieTitle) {
        this.username = username;
        this.comment = comment;
        this.rating = rating;
        this.timestamp = timestamp;
        this.userId = userId;
        this.slug = slug;
        this.movieTitle = movieTitle;
    }

    public Comment(String username, String comment, double rating, Date timestamp, String userId, String slug, String movieTitle, String parentId) {
        this.username = username;
        this.comment = comment;
        this.rating = rating;
        this.timestamp = timestamp;
        this.userId = userId;
        this.slug = slug;
        this.movieTitle = movieTitle;
        this.parentId = parentId;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    // Generate avatar URL based on username
    public String getAvatarUrl() {
        if (username != null && !username.trim().isEmpty()) {
            try {
                return "https://api.pravatar.cc/150?u=" + username.hashCode();
            } catch (Exception e) {
                return "https://api.pravatar.cc/150?u=default";
            }
        }
        return "https://api.pravatar.cc/150?u=default";
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", comment='" + comment + '\'' +
                ", rating=" + rating +
                ", userId='" + userId + '\'' +
                ", slug='" + slug + '\'' +
                ", movieTitle='" + movieTitle + '\'' +
                ", parentId='" + parentId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}