package xyz.cybersapien.amdb;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import xyz.cybersapien.amdb.adapter.MovieListAdapter;
import xyz.cybersapien.amdb.model.Movie;
import xyz.cybersapien.amdb.model.MovieList;
import xyz.cybersapien.amdb.network.MovieService;
import xyz.cybersapien.amdb.network.MoviesClient;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private MovieListAdapter moviesAdapter;
    private LinearLayout progressBar;
    private TextView errorView;
    private RecyclerView gridView;
    private Integer selectedId;
    private String sortOrder;
    private ArrayList<Movie> movieList;
    private Retrofit retroClient;
    private MovieService serviceInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progress_bar);
        gridView = findViewById(R.id.movie_list);
        errorView = findViewById(R.id.error_text);

        movieList = new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        gridView.setLayoutManager(layoutManager);

        moviesAdapter = new MovieListAdapter(this, movieList);
        gridView.setAdapter(moviesAdapter);
        retroClient = MoviesClient.getClient();
        serviceInterface = retroClient.create(MovieService.class);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.sortOrder = prefs.getString(getString(R.string.prefs_sort_order), getString(R.string.sort_order_popularity));
        performMovieSync();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sortParam = prefs.getString(getString(R.string.prefs_sort_order), getString(R.string.sort_order_popularity));

        if (getString(R.string.sort_order_rating).equals(sortParam)) {
            selectedId = R.id.sort_order_rating;
        } else if (getString(R.string.sort_order_popularity).equals(sortParam)) {
            selectedId = R.id.sort_order_popularity;
        }
        menu.findItem(selectedId).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String param = null;
        switch (item.getItemId()) {
            case R.id.sort_order_popularity:
                param = getString(R.string.sort_order_popularity);
                item.setChecked(true);
                setSortOrder(param);
                return true;
            case R.id.sort_order_rating:
                param = getString(R.string.sort_order_rating);
                item.setChecked(true);
                setSortOrder(param);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getString(R.string.prefs_sort_order), this.sortOrder);
        editor.apply();
        performMovieSync();
    }

    /**
     * Function to check if network connectivity is available
     */
    private boolean isInternetConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager != null ? manager.getActiveNetworkInfo() : null;

        return info != null && info.isConnected();
    }

    private void performMovieSync() {

        boolean performTask = isInternetConnected();
        if (performTask) {
            setLoadingView();
            makeRequest();
        } else {
            setErrorView("Error! No internet!");
        }
    }

    private void setLoadingView() {
        errorView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.GONE);
    }

    private void setErrorView(String errorMessage) {
        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
        gridView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
    }


    private void makeRequest() {

        Log.d(LOG_TAG, "makeRequest: Starting request");
        Call<MovieList> movies = serviceInterface.listMovies(sortOrder, BuildConfig.MY_TMDB_KEY);
        movies.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call,
                                   Response<MovieList> response) {
                MovieList list = response.body();
                if (list != null) {
                    List<Movie> movieList = list.getMovies();
                    refreshView(movieList);
                }
            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                Log.d(LOG_TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void refreshView(List<Movie> movies) {
        if (movies == null || movies.isEmpty())
            return;

        progressBar.setVisibility(View.GONE);
        movieList.clear();
        movieList.addAll(movies);
        moviesAdapter.notifyDataSetChanged();
        if (movies.size() > 0) {
            errorView.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
        } else {
            errorView.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
        }
    }
}
