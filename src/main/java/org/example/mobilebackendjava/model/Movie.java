package org.example.mobilebackendjava.model;

public class Movie {
    private String name;
    private String slug;
    private String poster_url;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getPoster_url() { return poster_url; }
    public void setPoster_url(String poster_url) { this.poster_url = poster_url; }
}
