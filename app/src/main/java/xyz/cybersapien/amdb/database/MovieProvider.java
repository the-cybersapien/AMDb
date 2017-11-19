package xyz.cybersapien.amdb.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import xyz.cybersapien.amdb.database.MovieContract.MovieEntry;

/**
 * Created by ogcybersapien on 19/11/17.
 */

public class MovieProvider extends ContentProvider {

    private static final String LOG_TAG = MovieProvider.class.getSimpleName();

    private MovieDbHelper dbHelper;

    public static final int MOVIES = 100;
    public static final int MOVIE_ID = 101;

    public static final UriMatcher movieUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        movieUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        movieUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIE_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MovieDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor;
        int match = movieUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                cursor = database.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_ID:
                selection = MovieEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot Find a match for the specified URI.");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = movieUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return MovieEntry.CONTENT_LIST_TYPE;
            case MOVIE_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("No Match found for the URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = movieUriMatcher.match(uri);
        Long id;
        switch (match) {
            case MOVIES:
                id = addMovie(contentValues);
                break;
            default:
                throw new IllegalArgumentException("No table for uri " + uri);
        }
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = movieUriMatcher.match(uri);
        int rowsDeleted;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_ID:
                selection = MovieEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("No match found for Uri " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = movieUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case MOVIES:
                rowsUpdated = updateMovie(contentValues, selection, selectionArgs);
                break;
            case MOVIE_ID:
                selection = MovieEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = updateMovie(contentValues, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("No valid match for URI: " + uri);
        }
        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    private int updateMovie(ContentValues contentValues, String selection, String[] selectionArgs) {

        if (contentValues.size() == 0)
            return 0;

        //Title Validation
        if (contentValues.containsKey(MovieEntry.COLUMN_MOVIE_TITLE)
                && contentValues.getAsString(MovieEntry.COLUMN_MOVIE_TITLE) == null) {
            throw new IllegalArgumentException("Movie requires a valid name");
        }
        // Movie Id Validation
        if (contentValues.containsKey(MovieEntry.COLUMN_MOVIE_ID)
                && contentValues.getAsString(MovieEntry.COLUMN_MOVIE_ID) == null) {
            throw new IllegalArgumentException("Movie requires a valid ID");
        }
        // Movie Post Validation
        if (contentValues.containsKey(MovieEntry.COLUMN_POSTER_PATH)
                && contentValues.getAsString(MovieEntry.COLUMN_POSTER_PATH) == null) {
            throw new IllegalArgumentException("Movie requires a valid poster path");
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.update(MovieEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }

    private long addMovie(ContentValues contentValues) {
        String title = contentValues.getAsString(MovieEntry.COLUMN_MOVIE_TITLE);
        String movieId = contentValues.getAsString(MovieEntry.COLUMN_MOVIE_ID);
        String poster = contentValues.getAsString(MovieEntry.COLUMN_POSTER_PATH);
        if (TextUtils.isEmpty(title)) {
            throw new IllegalArgumentException("Movie requires a valid name");
        }
        if (TextUtils.isEmpty(movieId)) {
            throw new IllegalArgumentException("Movie requires a valid ID");
        }
        if (TextUtils.isEmpty(poster)) {
            throw new IllegalArgumentException("Movie requires a valid poster path");
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.insert(MovieEntry.TABLE_NAME, null, contentValues);
    }

    public static Uri getUriFromID(String movieId) {
        return Uri.parse(MovieEntry.MOVIES_CONTENT_URI.toString()).buildUpon()
                .appendPath(movieId)
                .build();
    }
}
