package com.ewintory.udacity.popularmovies.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class Review implements Serializable {

    @Expose
    private String id;
    @Expose
    private String author;
    @Expose
    private String content;
    @Expose
    private String url;

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author The author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return The content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content The content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    public static final class Response {

        @Expose
        private Integer id;
        @Expose
        private Integer page;
        @Expose
        private List<Review> reviews = new ArrayList<>();
        @SerializedName("total_pages")
        @Expose
        private Integer totalPages;
        @SerializedName("total_results")
        @Expose
        private Integer totalResults;

        /**
         * @return The id
         */
        public Integer getId() {
            return id;
        }

        /**
         * @param id The id
         */
        public void setId(Integer id) {
            this.id = id;
        }

        /**
         * @return The page
         */
        public Integer getPage() {
            return page;
        }

        /**
         * @param page The page
         */
        public void setPage(Integer page) {
            this.page = page;
        }

        /**
         * @return The reviews
         */
        public List<Review> getReviews() {
            return reviews;
        }

        /**
         * @param reviews The reviews
         */
        public void setReviews(List<Review> reviews) {
            this.reviews = this.reviews;
        }

        /**
         * @return The totalPages
         */
        public Integer getTotalPages() {
            return totalPages;
        }

        /**
         * @param totalPages The total_pages
         */
        public void setTotalPages(Integer totalPages) {
            this.totalPages = totalPages;
        }

        /**
         * @return The totalResults
         */
        public Integer getTotalResults() {
            return totalResults;
        }

        /**
         * @param totalResults The total_results
         */
        public void setTotalResults(Integer totalResults) {
            this.totalResults = totalResults;
        }

    }
}