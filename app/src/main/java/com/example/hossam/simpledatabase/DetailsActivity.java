package com.example.hossam.simpledatabase;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hossam.simpledatabase.data.TaskContract;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private static final int REVIEW_LOADER = 2;
    private static final int TRAILER_LOADER = 3;

    private Movie movie;

    Button insert;
    String title;
    int movieID;
    String movieUrl;
    TextView userReviews;
    String reviewUrl;
    String trailerUrl;

    private android.support.v4.app.LoaderManager.LoaderCallbacks<List<String>> reviewMovieLoader
            = new android.support.v4.app.LoaderManager.LoaderCallbacks<List<String>>() {

        @Override
        public android.support.v4.content.Loader<List<String>> onCreateLoader(int i, Bundle bundle) {
            // Create a new loader for the given URL
            return new ReviewLoader(getApplicationContext(), reviewUrl);
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<List<String>> loader, List<String> reviews) {
            StringBuilder builder = new StringBuilder();
            for (String review : reviews) {
                builder.append(review + "\n");
            }

            userReviews.setText(builder.toString());
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<List<String>> loader) {
            userReviews.setText("");
        }
    };
    private LinearLayout root;
    private android.support.v4.app.LoaderManager.LoaderCallbacks<List<String>> trailerMovieLoader
            = new android.support.v4.app.LoaderManager.LoaderCallbacks<List<String>>() {

        @Override
        public android.support.v4.content.Loader<List<String>> onCreateLoader(int i, Bundle bundle) {
            // Create a new loader for the given URL
            return new TrailerLoader(getApplicationContext(), trailerUrl);
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<List<String>> loader, List<String> trailers) {

            root = findViewById(R.id.root_view);
            for(final String trailer : trailers) {
                ImageView image = new ImageView(getApplicationContext());
                LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                vp.setMargins(0, 30, 0, 0);
                image.setLayoutParams(vp);
                image.setMaxHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                image.setMaxWidth(root.getWidth());
                Picasso.with(getApplicationContext()).load(movie.getURL()).into(image);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri youtubeAddress = Uri.parse(trailer);
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, youtubeAddress);
                        String trailerTitle = getResources().getString(R.string.chooser_title);
                        Intent chooser = Intent.createChooser(webIntent,trailerTitle);
                        if (webIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(chooser);
                        }
                    }
                });
                // Adds the view to the layout
                root.addView(image);
            }
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<List<String>> loader) {
        }
    };
    String selection;
    String[] selectionArgs;
    private String releaseDate;
    private Number voteAverage;
    private String plotSynopsis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        userReviews = findViewById(R.id.user_review);

        insert = findViewById(R.id.fav_button);


        final Intent intentThatStartedThisActivity = getIntent();
        boolean isFavorite = intentThatStartedThisActivity.getBooleanExtra("isFavoriteChoosing", false);
        if (isFavorite){
            movie = intentThatStartedThisActivity.getParcelableExtra("MovieDBDetails");
        } else {
            movie = intentThatStartedThisActivity.getParcelableExtra("MovieDetails");
        }

        movieID = movie.getMovieID();
        movieUrl = movie.getURL();
        title = movie.getTitle();
        releaseDate = movie.getReleaseDate();
        voteAverage = movie.getVoteAverage();
        plotSynopsis = movie.getOverview();

        final ImageView moviePoster = findViewById(R.id.movie_poster);
        TextView movieTitle = findViewById(R.id.movie_title);
        TextView movieReleaseDate = findViewById(R.id.release_date);
        TextView movieVoteAverage = findViewById(R.id.vote_average);
        TextView overview = findViewById(R.id.plot_synopsis);

        Picasso.with(this).load(movieUrl).into(moviePoster);
        movieTitle.setText(title);
        movieReleaseDate.setText(releaseDate);
        movieVoteAverage.setText(String.valueOf(voteAverage));
        overview.setText(plotSynopsis);


        reviewUrl =  movie.getReviewsUrl();
        trailerUrl = movie.getTrailerUrl();
        getSupportLoaderManager().initLoader(REVIEW_LOADER, null, reviewMovieLoader);
        getSupportLoaderManager().initLoader(TRAILER_LOADER, null, trailerMovieLoader);

        selection = TaskContract.TaskEntry.COLUMN_TITLE + " =?";

        selectionArgs = new String[]{title};

       insert.setBackgroundResource(CheckIsDataAlreadyInDBorNot(TaskContract.TaskEntry.TABLE_NAME,selection,selectionArgs) ? R.drawable.greenstar : R.drawable.whitestar);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.fav_button) {
                    if (!CheckIsDataAlreadyInDBorNot(TaskContract.TaskEntry.TABLE_NAME, selection, selectionArgs)){
                        insertToDB(view);
                    } else {
                        deleteFromDB(view);
                    }

                }
            }
        });

    }
    public boolean CheckIsDataAlreadyInDBorNot(String TableName,
                                               String dbfield, String[] fieldValue) {
        String[] projection = {
                TaskContract.TaskEntry.COLUMN_ID,
                TaskContract.TaskEntry.COLUMN_POSTER,
                TaskContract.TaskEntry.COLUMN_TITLE,
                TaskContract.TaskEntry.COLUMN_RELEASE_DATE,
                TaskContract.TaskEntry.COLUMN_VOTES,
                TaskContract.TaskEntry.COLUMN_OVERVIEW,
                TaskContract.TaskEntry.COLUMN_REVIEW_URL,
                TaskContract.TaskEntry.COLUMN_TRAILER_URL
        };

        Cursor cursor = getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI, projection, dbfield, fieldValue, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    private void insertToDB(View view) {
            ContentValues values = new ContentValues();
            values.put(TaskContract.TaskEntry.COLUMN_ID, movieID);
            values.put(TaskContract.TaskEntry.COLUMN_POSTER, movieUrl);
            values.put(TaskContract.TaskEntry.COLUMN_TITLE, title);
        values.put(TaskContract.TaskEntry.COLUMN_RELEASE_DATE, releaseDate);
        values.put(TaskContract.TaskEntry.COLUMN_VOTES, String.valueOf(voteAverage));
        values.put(TaskContract.TaskEntry.COLUMN_OVERVIEW, plotSynopsis);
        values.put(TaskContract.TaskEntry.COLUMN_REVIEW_URL, reviewUrl);
        values.put(TaskContract.TaskEntry.COLUMN_TRAILER_URL, trailerUrl);

            Uri newUri = getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.insert_movie_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.insert_movie_successful),
                        Toast.LENGTH_SHORT).show();
                view.setBackgroundResource(R.drawable.greenstar);
            }
    }

    private void deleteFromDB(View view) {
        int rowsDeleted = getContentResolver().delete(TaskContract.TaskEntry.CONTENT_URI, selection, selectionArgs);
        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, getString(R.string.delete_movie_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.delete_movie_successful),
                    Toast.LENGTH_SHORT).show();
            view.setBackgroundResource(R.drawable.whitestar);
        }
    }
}

