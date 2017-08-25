package xyz.cybersapien.amdb;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import xyz.cybersapien.amdb.movie.Movie;
import xyz.cybersapien.amdb.movie.MovieListAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private MovieListAdapter moviesAdapter;
    private LinearLayout progressBar;
    private TextView errorView;
    private RecyclerView gridView;
    private Integer selectedId;
    private String sortOrder;
    private ArrayList<Movie> movieList;

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
        GetListASyncTask getMovies = new GetListASyncTask();
        getMovies.execute();
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
                break;
            case R.id.sort_order_rating:
                param = getString(R.string.sort_order_rating);
                break;
        }
        if (param != null) {
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
        GetListASyncTask getMovies = new GetListASyncTask();
        getMovies.execute();
    }

    /**
     * Function to check if network connectivity is available
     */
    private boolean isInternetConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager != null ? manager.getActiveNetworkInfo() : null;

        return info != null && info.isConnected();
    }

    private class GetListASyncTask extends AsyncTask<Void, Void, ArrayList<Movie>> {

        boolean performTask;

        @Override
        protected void onPreExecute() {
            performTask = isInternetConnected();
            if (performTask) {
                errorView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                gridView.setVisibility(View.GONE);
            } else {
                Toast.makeText(MainActivity.this, "Error! No Internet!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                gridView.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected ArrayList<Movie> doInBackground(Void... voids) {
            if (!performTask) {
                return null;
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            String sortOrder = prefs.getString(getString(R.string.prefs_sort_order), getString(R.string.sort_order_popularity));
            Uri.Builder discoverBuilder = Uri.parse(HelperUtils.BASE_URL).buildUpon();
            discoverBuilder
                    .appendPath("movie")
                    .appendPath(sortOrder)
                    .appendQueryParameter("api_key", HelperUtils.API_KEY)
                    .appendQueryParameter("page", "1");
            String jsonResponse = null;
            try {
                URL url = new URL(discoverBuilder.build().toString());
                jsonResponse = HelperUtils.makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return HelperUtils.parseJSONList(jsonResponse);
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            // TODO Handle null
            if (!performTask)
                return;

            progressBar.setVisibility(View.GONE);
            movieList.clear();
            movieList.addAll(movies);
            moviesAdapter.notifyDataSetChanged();
            if (movies.size() > 0) {
                gridView.setVisibility(View.VISIBLE);
            } else {
                errorView.setVisibility(View.VISIBLE);
            }
        }
    }
}
