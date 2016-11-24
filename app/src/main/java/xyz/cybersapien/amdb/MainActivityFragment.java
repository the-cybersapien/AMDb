package xyz.cybersapien.amdb;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import xyz.cybersapien.amdb.movie.Movie;
import xyz.cybersapien.amdb.movie.MovieListAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String TAG = MainActivityFragment.class.getName();

    private MovieListAdapter moviesAdapter;
    private ProgressBar progressBar;
    private RecyclerView gridView;
    private Integer selectedId;
    private String sortOrder;
    private ArrayList<Movie> movieList;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        progressBar = (ProgressBar) root.findViewById(R.id.progress_bar);
        movieList = new ArrayList<>();
        gridView = root.findViewById(R.id.movie_list);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        gridView.setLayoutManager(layoutManager);

        moviesAdapter = new MovieListAdapter(getContext(), movieList);
        gridView.setAdapter(moviesAdapter);
        gridView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        GetListASyncTask getMovies = new GetListASyncTask();
        getMovies.execute();
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_list, menu);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortParam = prefs.getString(getString(R.string.prefs_sort_order), getString(R.string.sort_order_popularity));

        if (getString(R.string.sort_order_rating).equals(sortParam)) {
            selectedId = R.id.sort_order_rating;
        } else if (getString(R.string.sort_order_popularity).equals(sortParam)) {
            selectedId = R.id.sort_order_popularity;
        }
        menu.findItem(selectedId).setChecked(true);
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getString(R.string.prefs_sort_order), this.sortOrder);
        editor.apply();
        GetListASyncTask getMovies = new GetListASyncTask();
        getMovies.execute();
    }

    private class GetListASyncTask extends AsyncTask<Void, Void, ArrayList<Movie>> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
        }

        @Override
        protected ArrayList<Movie> doInBackground(Void... voids) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
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
            progressBar.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            movieList.clear();
            movieList.addAll(movies);
            moviesAdapter.notifyDataSetChanged();
        }
    }
}
