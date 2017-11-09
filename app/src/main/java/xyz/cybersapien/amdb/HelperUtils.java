package xyz.cybersapien.amdb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import xyz.cybersapien.amdb.model.Movie;

/**
 * Helper methods for Parsing and getting data
 */

public class HelperUtils {

    /* Log Tag*/
    private static final String LOG_TAG = HelperUtils.class.getName();

    public static final String BASE_URL = "https://api.themoviedb.org/3/";
    public static final String API_KEY = BuildConfig.MY_TMDB_KEY;

    public static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse;
        HttpsURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000/* milliseconds */);
            urlConnection.setConnectTimeout(10000/* milliseconds */);
            urlConnection.connect();
            if (urlConnection.getResponseCode() != 200) {
                jsonResponse = "";
            } else {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
            if (inputStream != null) inputStream.close();
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder buffer = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader streamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(streamReader);
            String line = reader.readLine();
            while (line != null) {
                buffer.append(line);
                line = reader.readLine();
            }
        }
        return buffer.toString();
    }

    public static ArrayList<Movie> parseJSONList(String JSONString) {

        if (JSONString == null)
            return null;

        ArrayList<Movie> movieList = new ArrayList<>();

        try {
            //Create root
            JSONObject root = new JSONObject(JSONString);
            //Get the results array
            JSONArray results = root.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject currentObject = results.getJSONObject(i);
                String id = currentObject.getString("id");
                String title = currentObject.getString("title");
                String posterPath = currentObject.getString("poster_path");
                movieList.add(new Movie(title, id, posterPath));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return movieList;
    }
}
