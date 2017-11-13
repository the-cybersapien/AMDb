package xyz.cybersapien.amdb.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import xyz.cybersapien.amdb.BuildConfig;
import xyz.cybersapien.amdb.model.Movie;
import xyz.cybersapien.amdb.model.MovieList;

/**
 * Created by ogcybersapien on 9/11/17.
 */

public interface MovieService {
    @GET("movie/{movie_id}?api_key=" + BuildConfig.MY_TMDB_KEY)
    Call<Movie> getMovie(@Path("movie_id") String movieId);

    @GET("movie/{movie_id}/videos?api_key=" + BuildConfig.MY_TMDB_KEY)
    Call<Movie.MovieVideos> getMovieVideos(@Path("movie_id") String movieId);

    @GET("movie/{movie_id}/reviews?api_key=" + BuildConfig.MY_TMDB_KEY)
    Call<Movie.MovieReviews> getMovieReviews(@Path("movie_id") String movieId);

    @GET("movie/{sort_order}?api_key=" + BuildConfig.MY_TMDB_KEY)
    Call<MovieList> listMovies(@Path("sort_order") String sortOrder);

}
