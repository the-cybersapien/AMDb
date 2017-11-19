package xyz.cybersapien.amdb.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ogcybersapien on 19/11/17.
 */

public class MovieContract {

    /* Content Authority for the Content Providers Uri */
    public static final String CONTENT_AUTHORITY = "xyz.cybersapien.amdb";

    /* Base URI for Content Providers */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = MovieEntry.TABLE_NAME;

    public static abstract class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movies";

        public static final Uri MOVIES_CONTENT_URI = Uri
                .withAppendedPath(BASE_CONTENT_URI, PATH_MOVIES);

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";

        /* MIME type for getting list */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        /* MIME type for getting a single movie */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
    }
}
