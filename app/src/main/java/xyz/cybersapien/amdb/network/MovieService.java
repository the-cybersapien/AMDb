package xyz.cybersapien.amdb.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import xyz.cybersapien.amdb.model.Movie;
import xyz.cybersapien.amdb.model.MovieList;

/**
 * Created by ogcybersapien on 9/11/17.
 */

public interface MovieService {
    @GET("movie/{movie_id}")
    Call<Movie> getMovie(@Path("movie_id") String movieId, @Query("api_key") String apiKey);

    @GET("movie/{sort_order}")
    Call<MovieList> listMovies(@Path("sort_order") String sortOrder, @Query("api_key") String apiKey);

}
