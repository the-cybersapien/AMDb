package xyz.cybersapien.amdb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import xyz.cybersapien.amdb.adapter.TrailerListAdapter;
import xyz.cybersapien.amdb.model.Movie;
import xyz.cybersapien.amdb.network.MovieService;
import xyz.cybersapien.amdb.network.MoviesClient;

public class DetailActivity extends AppCompatActivity {

    public static final String TAG = DetailActivity.class.getName();

    @BindView(R.id.progress_bar)
    public View progressView;
    @BindView(R.id.error_text)
    public TextView errorTextView;
    @BindView(R.id.movie_detail)
    public View detailsRootView;
    @BindView(R.id.detail_backdrop_image)
    public ImageView backdropImageView;
    @BindView(R.id.detail_poster_image)
    public ImageView posterImageView;
    @BindView(R.id.detail_movie_title)
    public TextView titleTextView;
    @BindView(R.id.detail_movie_desc)
    public TextView descTextView;
    @BindView(R.id.detail_movie_rating)
    public TextView movieRating;
    @BindView(R.id.detail_movie_release)
    public TextView movieRelease;
    //    @BindView(R.id.reviews_list_view)
//    public RecyclerView reviewsRecyclerView;
    @BindView(R.id.trailers_list_view)
    public RecyclerView trailersRecyclerView;

    private Retrofit retroClient;
    private MovieService movieService;

    private ArrayList<Movie.VideoResults> trailerVideos;
    private TrailerListAdapter trailersAdapter;

    private ArrayList<Movie.ReviewResults> movieReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        String movieId = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        retroClient = MoviesClient.getClient();
        movieService = retroClient.create(MovieService.class);

        movieReviews = new ArrayList<>();
        trailerVideos = new ArrayList<>();

        //reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        trailersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        trailersAdapter = new TrailerListAdapter(trailerVideos, this);
        trailersRecyclerView.setAdapter(trailersAdapter);

        getMovieDetails(movieId);
    }

    public void getMovieDetails(String movieId) {
        Call<Movie> movieCall = movieService.getMovie(movieId);
        showLoading();
        movieCall.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful()) {
                    Movie movie = response.body();
                    showData(movie);
                } else {
                    showError("Unable to show results!");
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                showError(t.getLocalizedMessage());
            }
        });
    }

    public void getMovieTrailers(String movieId) {
        Call<Movie.MovieVideos> videosCall = movieService.getMovieVideos(movieId);
        videosCall.enqueue(new Callback<Movie.MovieVideos>() {
            @Override
            public void onResponse(Call<Movie.MovieVideos> call, Response<Movie.MovieVideos> response) {
                if (response.isSuccessful()) {
                    Movie.MovieVideos videos = response.body();
                    Log.d(TAG, "onResponse: " + (videos != null ? videos.toString() : null));
                    if (videos != null) {
                        trailerVideos.clear();
                        trailerVideos.addAll(videos.getVideos());
                        Log.d(TAG, "onResponse: " + trailerVideos);
                        trailersAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<Movie.MovieVideos> call, Throwable t) {
            }
        });
    }

    public void getMovieReviews(String movieId) {
        Call<Movie.MovieReviews> reviewsCall = movieService.getMovieReviews(movieId);
        reviewsCall.enqueue(new Callback<Movie.MovieReviews>() {
            @Override
            public void onResponse(Call<Movie.MovieReviews> call, Response<Movie.MovieReviews> response) {
                if (response.isSuccessful()) {
                    Movie.MovieReviews reviews = response.body();
                    if (reviews != null) {
                        movieReviews.clear();
                        movieReviews.addAll(reviews.getReviews());
                        //TODO: reviewsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<Movie.MovieReviews> call, Throwable t) {
            }
        });
    }

    private void showData(Movie movie) {
        if (movie == null) {
            showError("Unable to Load data!");
            return;
        }

        // Now that we have the movie data, show it, and load the reviews and trailers
        getMovieTrailers(movie.getMovieId());
        // TODO: getMovieReviews(movie.getMovieId());

        Integer densityDpi = getResources().getDisplayMetrics().densityDpi;
        Picasso.with(getBaseContext())
                .load(movie.getBackDropPath(densityDpi))
                .fit()
                .placeholder(R.drawable.movie_placeholder)
                .into(backdropImageView);
        Picasso.with(getBaseContext())
                .load(movie.getPosterPath(densityDpi))
                .fit()
                .placeholder(R.drawable.movie_placeholder)
                .into(posterImageView);
        titleTextView.setText(movie.getTitle());
        descTextView.setText(movie.getOverview());
        movieRating.setText(String.valueOf(movie.getVoteAvg()));
        movieRelease.setText(movie.getReleaseDate());

        detailsRootView.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.GONE);
        errorTextView.setVisibility(View.GONE);
    }

    private void showError(String message) {
        errorTextView.setText(message);
        errorTextView.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.GONE);
        detailsRootView.setVisibility(View.GONE);
    }

    private void showLoading() {
        progressView.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);
        detailsRootView.setVisibility(View.GONE);
    }
}
