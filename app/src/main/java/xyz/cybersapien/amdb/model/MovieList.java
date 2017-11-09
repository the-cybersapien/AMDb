package xyz.cybersapien.amdb.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ogcybersapien on 9/11/17.
 */

public class MovieList {

    @SerializedName("results")
    private List<Movie> movies;
    @SerializedName("page")
    private int page;
    @SerializedName("total_pages")
    private int totalPages;
    @SerializedName("total_results")
    private int totalResults;

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MoviesList{");
        sb.append("movies=").append(movies);
        sb.append(", page=").append(page);
        sb.append(", totalPages=").append(totalPages);
        sb.append(", totalResults=").append(totalResults);
        sb.append('}');
        return sb.toString();
    }
}
