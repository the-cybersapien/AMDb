package xyz.cybersapien.amdb;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import xyz.cybersapien.amdb.movie.Movie;

public class DetailActivity extends AppCompatActivity {

    public static final String TAG = DetailActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        String movieId = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        GetMovieASyncTask movieDetailGetter = new GetMovieASyncTask();
        movieDetailGetter.execute(movieId);
    }

    private class GetMovieASyncTask extends AsyncTask<String, Void, Movie> {

        @Override
        protected Movie doInBackground(String... strings) {
            if (strings.length == 0) {
                return null;
            }
            String jsonResponse = null;
            Uri.Builder uriBuilder = Uri.parse(HelperUtils.BASE_URL).buildUpon();
            uriBuilder.appendPath("movie");
            uriBuilder.appendPath(strings[0]);
            uriBuilder.appendQueryParameter("api_key", HelperUtils.API_KEY);
            try {
                jsonResponse = HelperUtils.makeHttpRequest(new URL(uriBuilder.build().toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return parseMovieFromJSON(jsonResponse);
        }

        @Override
        protected void onPostExecute(Movie movie) {

            findViewById(R.id.progress_bar).setVisibility(View.GONE);

            if (movie != null) {

                findViewById(R.id.movie_detail).setVisibility(View.VISIBLE);

                Integer densityDpi = getResources().getDisplayMetrics().densityDpi;
                ImageView backDropImageView = findViewById(R.id.detail_backdrop_image);
                Picasso.with(getBaseContext()).load(movie.getBackDropPath(densityDpi)).fit().placeholder(R.drawable.movie_placeholder).into(backDropImageView);

                ImageView posterImageView = findViewById(R.id.detail_poster_image);
                Picasso.with(getBaseContext()).load(movie.getPosterPath(densityDpi)).fit().placeholder(R.drawable.movie_placeholder).into(posterImageView);

                TextView titleTextView = findViewById(R.id.detail_movie_title);
                titleTextView.setText(movie.getTitle());

                TextView descTextView = findViewById(R.id.detail_movie_desc);
                descTextView.setText(movie.getOverview());

                TextView ratingTextView = findViewById(R.id.detail_movie_rating);
                ratingTextView.setText(String.valueOf(movie.getVoteAvg()));

                TextView releaseTextView = findViewById(R.id.detail_movie_release);
                releaseTextView.setText(movie.getReleaseDate());
            } else {
                findViewById(R.id.error_text).setVisibility(View.VISIBLE);
            }
        }

        private Movie parseMovieFromJSON(String jsonResponse) {
            if (jsonResponse == null) {
                return null;
            }
            Movie movie = null;
            try {
                JSONObject movieObject = new JSONObject(jsonResponse);
                String backdrop = movieObject.optString("backdrop_path");
                String title = movieObject.getString("title");
                String desc = movieObject.getString("overview");
                String release = movieObject.getString("release_date");
                Double voteAvg = movieObject.getDouble("vote_average");
                String posterPath = movieObject.getString("poster_path");
                String movieId = movieObject.getString("id");
                movie = new Movie(title, movieId, desc, release, posterPath, backdrop, voteAvg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return movie;
        }
    }
}
