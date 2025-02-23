package com.example.watchly;

import java.util.List;

public class Genres {
    private List<Genre> genres;

    public List<Genre> getGenres() {
        return genres;
    }

    public class Genre {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
