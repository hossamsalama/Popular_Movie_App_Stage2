package com.example.hossam.simpledatabase.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hossam on 5/25/2018.
 */

public class TaskDbHelper extends SQLiteOpenHelper {
    // The name of the database
    private static final String DATABASE_NAME = "tasksDb.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 1;

    private static final String DATABASE_ALTER_MOVIE_1 = "ALTER TABLE "
            + TaskContract.TaskEntry.TABLE_NAME + " ADD COLUMN " + TaskContract.TaskEntry.COLUMN_FAVORITE + " string;";

    // Constructor
    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     * Called when the tasks database is created for the first time.
     */

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE = "CREATE TABLE "  + TaskContract.TaskEntry.TABLE_NAME + " (" +
                TaskContract.TaskEntry.COLUMN_ID    + " INTEGER, " +
                TaskContract.TaskEntry.COLUMN_POSTER + " TEXT, " +
                TaskContract.TaskEntry.COLUMN_TITLE    + " TEXT NOT NULL, " +
                TaskContract.TaskEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                TaskContract.TaskEntry.COLUMN_VOTES + " INTEGER, " +
                TaskContract.TaskEntry.COLUMN_OVERVIEW + " TEXT, " +
                TaskContract.TaskEntry.COLUMN_REVIEW_URL + " TEXT, " +
                TaskContract.TaskEntry.COLUMN_TRAILER_URL + " TEXT, " +
                TaskContract.TaskEntry.COLUMN_FAVORITE + " TEXT);";


        db.execSQL(CREATE_TABLE);
    }


    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME);
        if (oldVersion < 2) {
            db.execSQL(DATABASE_ALTER_MOVIE_1);
        }

        onCreate(db);
    }
}
