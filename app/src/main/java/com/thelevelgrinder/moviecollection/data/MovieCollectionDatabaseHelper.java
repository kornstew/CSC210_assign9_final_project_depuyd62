package com.thelevelgrinder.moviecollection.data;

/**
 * Created by Damian Depuy on 11/30/2016.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.thelevelgrinder.moviecollection.data.DatabaseDescription.Movie;

public class MovieCollectionDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MovieCollection.db";
    private static final int DATABASE_VERSION = 1;

    // constructor
    public MovieCollectionDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // creates the movies table when the database is created
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL for creating the contacts table
        final String CREATE_MOVIES_TABLE =
                "CREATE TABLE " + Movie.TABLE_NAME + "(" +
                        Movie._ID + " integer primary key, " +
                        Movie.COLUMN_TITLE + " TEXT, " +
                        Movie.COLUMN_YEAR + " TEXT, " +
                        Movie.COLUMN_DIRECTOR + " TEXT, " +
                        Movie.COLUMN_GENRE + " TEXT, " +
                        Movie.COLUMN_RATED + " TEXT);";

        db.execSQL(CREATE_MOVIES_TABLE); // create the movies table
    }

    // normally defines how to upgrade the database when the schema changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) { }
}