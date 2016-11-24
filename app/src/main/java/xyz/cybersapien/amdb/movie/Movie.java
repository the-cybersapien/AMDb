package xyz.cybersapien.amdb.movie;

import android.net.Uri;
import android.util.DisplayMetrics;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ogcybersapien on 22/11/16.
 * Wrapper Class for Movie object.
 */

public class Movie {
    private static final String LOG_TAG = Movie.class.getName();
    private static final String IMG_BASE_URL = "http://image.tmdb.org/t/p";

    private String title;
    private String movieId;
    private String overview;
    private String releaseDate;
    private String posterPath;
    private String backDropPath;
    private Double voteAvg;

    public Movie(String title, String movieId, String overview, String releaseDate, String posterPath, String backDropPath, Double voteAvg) {
        this.title = title;
        this.movieId = movieId;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.backDropPath = backDropPath;
        this.voteAvg = voteAvg;
    }

    public Movie(String title, String movieId, String posterPath) {
        this.title = title;
        this.movieId = movieId;
        this.posterPath = posterPath;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        String formattedRelease;
        try {
            DateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            date = originalDateFormat.parse(this.releaseDate);
            DateFormat dateFormat = DateFormat.getDateInstance();
            formattedRelease = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            formattedRelease = releaseDate;
        }
        return formattedRelease;
    }

    public String getMovieId() {
        return movieId;
    }

    public Uri getPosterPath(Integer densityDpi) {
        String size;
        switch (densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                size = "w92";
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                size = "w154";
                break;
            case DisplayMetrics.DENSITY_HIGH:
                size = "w185";
                break;
            //Rest all the cases are for larger screens and screen densities so we use a default case for all the cases
            default:
                size = "w342";
                break;
        }
        return Uri.parse(IMG_BASE_URL).buildUpon().appendPath(size).appendEncodedPath(posterPath).build();
    }

    public Uri getBackDropPath(Integer densityDpi) {

        String size;
        switch (densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
            case DisplayMetrics.DENSITY_MEDIUM:
            case DisplayMetrics.DENSITY_HIGH:
            default:
                size = "w300";
                break;
            case DisplayMetrics.DENSITY_260:
            case DisplayMetrics.DENSITY_280:
            case DisplayMetrics.DENSITY_300:
            case DisplayMetrics.DENSITY_XHIGH:
                size = "w780";
                break;
            case DisplayMetrics.DENSITY_340:
            case DisplayMetrics.DENSITY_360:
            case DisplayMetrics.DENSITY_400:
            case DisplayMetrics.DENSITY_420:
            case DisplayMetrics.DENSITY_XXHIGH:
            case DisplayMetrics.DENSITY_XXXHIGH:
                size = "w1280";
                break;
        }
        return Uri.parse(IMG_BASE_URL).buildUpon().appendPath(size).appendEncodedPath(backDropPath).build();
    }

    public Double getVoteAvg() {
        return voteAvg;
    }

}
