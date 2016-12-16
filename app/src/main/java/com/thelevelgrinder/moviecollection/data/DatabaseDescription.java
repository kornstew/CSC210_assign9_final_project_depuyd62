package com.thelevelgrinder.moviecollection.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Damian Depuy on 11/30/2016.
 */
public class DatabaseDescription {
    // ContentProvider's name: typically the package name
    public static final String AUTHORITY =
            "com.thelevelgrinder.moviecollection.data";

    // base URI used to interact with the ContentProvider
    private static final Uri BASE_CONTENT_URI =
            Uri.parse("content://" + AUTHORITY);

    // nested class defines contents of the movies table
    public static final class Movie implements BaseColumns {
        public static final String TABLE_NAME = "movies"; // table's name

        // Uri for the movies table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        // column names for movies table's columns
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_YEAR= "year";
        public static final String COLUMN_DIRECTOR = "director";
        public static final String COLUMN_GENRE = "genre";
        public static final String COLUMN_RATED = "rated";


        // creates a Uri for a specific movie
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
