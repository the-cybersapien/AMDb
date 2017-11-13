package xyz.cybersapien.amdb.model;

import android.net.Uri;
import android.util.DisplayMetrics;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Wrapper Class for Movie object.
 */

public class Movie {
    private static final String IMG_BASE_URL = "http://image.tmdb.org/t/p";

    @SerializedName("title")
    private String title;
    @SerializedName("id")
    private String movieId;
    @SerializedName("overview")
    private String overview;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("backdrop_path")
    private String backDropPath;
    @SerializedName("vote_average")
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
            DateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date;
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

    @Override
    public String toString() {
        return "Movie{" + "title='" + title + '\'' +
                ", movieId='" + movieId + '\'' +
                ", overview='" + overview + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", backDropPath='" + backDropPath + '\'' +
                ", voteAvg=" + voteAvg +
                '}';
    }

    public static class MovieVideos {
        @SerializedName("results")
        private List<VideoResults> videos;

        public List<VideoResults> getVideos() {
            return videos;
        }

        public void setVideos(List<VideoResults> videos) {
            this.videos = videos;
        }
    }

    public static class VideoResults {
        @SerializedName("key")
        private String videoKey;
        @SerializedName("site")
        private String website;
        @SerializedName("name")
        private String title;
        @SerializedName("size")
        private String size;
        @SerializedName("type")
        private String type;

        public String getVideoKey() {
            return videoKey;
        }

        public void setVideoKey(String videoKey) {
            this.videoKey = videoKey;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "VideoResults{" + "videoKey='" + videoKey + '\'' +
                    ", website='" + website + '\'' +
                    ", title='" + title + '\'' +
                    ", size='" + size + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    public static class MovieReviews {
        @SerializedName("results")
        private List<ReviewResults> reviews;

        public List<ReviewResults> getReviews() {
            return reviews;
        }

        public void setReviews(List<ReviewResults> reviews) {
            this.reviews = reviews;
        }
    }

    public static class ReviewResults {
        @SerializedName("author")
        private String author;

        @SerializedName("content")
        private String content;

        @SerializedName("url")
        private String url;

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
