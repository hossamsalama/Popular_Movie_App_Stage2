package com.example.hossam.simpledatabase;

import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hossam.simpledatabase.data.TaskContract;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener, SimpleAdapter.ItemClickListener{

    /**
     * URL to query the The Movie DB api dataset for movies information
     */
    private static String mMovieRequestUrl;

    private static final String Movies_POPULAR_URL = "https://api.themoviedb.org/3/movie/popular?page=1&language=en-US&api_key=";

    private static final String Movies_TOP_RTED_URL = "https://api.themoviedb.org/3/movie/top_rated?page=1&language=en-US&api_key=";



    public static String API_KEY;

    public static final String LOG_TAG = MainActivity.class.getName();

    private SimpleAdapter simpleAdapter;
    private MyRecyclerViewAdapter mAdapter;

    private static final int FAVORITE_LOADER_ID = 0;
    private static final int Movies_LOADER_ID = 1;

    private View circularProgressBar;
    private TextView errorMessage;

    private android.support.v4.app.LoaderManager.LoaderCallbacks<List<Movie>> movieLoader
            = new android.support.v4.app.LoaderManager.LoaderCallbacks<List<Movie>>() {

        @Override
        public android.support.v4.content.Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
            // Create a new loader for the given URL
            return new ImageLoader(getApplicationContext(), mMovieRequestUrl);
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<List<Movie>> loader, List<Movie> movies) {
            circularProgressBar.setVisibility(View.GONE);
            // Clear the adapter of previous movie data
            mAdapter.setMovieData(movies);
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<List<Movie>> loader) {
            mAdapter.setMovieData(null);
        }
    };

    private android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> favoriteMoviesLoader
            = new android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            // Since the details shows all movies attributes, define a projection that contains
            // all columns from the movies table
            String[] projection = {
                    TaskContract.TaskEntry.COLUMN_ID,
                    TaskContract.TaskEntry.COLUMN_POSTER,
                    TaskContract.TaskEntry.COLUMN_TITLE,
                    TaskContract.TaskEntry.COLUMN_RELEASE_DATE,
                    TaskContract.TaskEntry.COLUMN_VOTES,
                    TaskContract.TaskEntry.COLUMN_OVERVIEW,
                    TaskContract.TaskEntry.COLUMN_REVIEW_URL,
                    TaskContract.TaskEntry.COLUMN_TRAILER_URL};

            // This loader will execute the ContentProvider's query method on a background thread
            return new android.support.v4.content.CursorLoader(getApplicationContext(),   // Parent activity context
                    TaskContract.TaskEntry.CONTENT_URI,         // Query the content URI for the current pet
                    projection,             // Columns to include in the resulting Cursor
                    null,                   // No selection clause
                    null,                   // No selection arguments
                    null);
        }

        /**
         * Called when a previously created loader has finished its load.
         *
         * @param loader The Loader that has finished.
         * @param data The data generated by the Loader.
         */
        @Override
        public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
            circularProgressBar.setVisibility(View.GONE);
            // Update the data that the adapter uses to create ViewHolders
            simpleAdapter.swapCursor(data);
        }

        /**
         * Called when a previously created loader is being reset, and thus
         * making its data unavailable.
         * onLoaderReset removes any references this activity had to the loader's data.
         *
         * @param loader The Loader that is being reset.
         */
        @Override
        public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
            simpleAdapter.swapCursor(null);
        }

    };
    private static boolean isFavoriteChoosing;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPrefs = getSharedPreferences("FavoriteMovie", MODE_PRIVATE);
        isFavoriteChoosing = sharedPrefs.getBoolean("isFavoriteChoosing", isFavoriteChoosing);

        //Get Api key from gradle
        API_KEY = BuildConfig.MY_API_KEY;;
        //Initialize the app with popular movies
        mMovieRequestUrl = Movies_POPULAR_URL + API_KEY;

        ArrayList<Movie> movieList = new ArrayList<>();

        recyclerView = findViewById(R.id.rv_simple);

        simpleAdapter = new SimpleAdapter(this, null);
        mAdapter = new MyRecyclerViewAdapter(getApplicationContext(), movieList);
        mAdapter.setClickListener(this);
        simpleAdapter.setClickListener(this);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        recyclerView.setHasFixedSize(true);
        errorMessage = findViewById(R.id.empty_view) ;
        circularProgressBar = findViewById(R.id.loading_spinner);
        // Get a reference to the LoaderManager, in order to interact with loaders.
        android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(FAVORITE_LOADER_ID, null, favoriteMoviesLoader);

        if (isFavoriteChoosing){
            recyclerView.setAdapter(simpleAdapter);
            circularProgressBar.setVisibility(View.GONE);
            errorMessage.setVisibility(View.GONE);
        } else {
            if (isOnline()){
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(Movies_LOADER_ID, null, movieLoader);
            recyclerView.setAdapter(mAdapter);
            } else {
                circularProgressBar.setVisibility(View.GONE);
                errorMessage.setText(R.string.no_internet_connection);
            }
        }
    }

    //Check Network connection
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popular:
                isFavoriteChoosing = false;
                updateMovieList(Movies_POPULAR_URL);
                return true;
            case R.id.top_rated:
                isFavoriteChoosing = false;
                updateMovieList(Movies_TOP_RTED_URL);
                return true;
            case R.id.favorite:
                isFavoriteChoosing = true;
                updateMovieList(Movies_TOP_RTED_URL);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateMovieList(String movieRequestUrl) {
            mMovieRequestUrl = movieRequestUrl + API_KEY;
            if (isFavoriteChoosing){
                recyclerView.setAdapter(simpleAdapter);
                getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, favoriteMoviesLoader);
                circularProgressBar.setVisibility(View.GONE);
                errorMessage.setVisibility(View.GONE);
            } else {
                if (isOnline()){
                    recyclerView.setAdapter(mAdapter);
                    getSupportLoaderManager().restartLoader(Movies_LOADER_ID, null, movieLoader);
                } else {
                    circularProgressBar.setVisibility(View.GONE);
                    errorMessage.setText(R.string.no_internet_connection);
                }
            }
            SharedPreferences.Editor editor = getSharedPreferences("FavoriteMovie", MODE_PRIVATE).edit();
            editor.putBoolean("isFavoriteChoosing", isFavoriteChoosing);
            editor.commit();

        }

    @Override
    public void onItemClick(View view, int position) {
        Movie currentMovie = mAdapter.getItem(position);
        Intent showMovieDetailsIntent = new Intent(this, DetailsActivity.class);
        showMovieDetailsIntent.putExtra("MovieDetails", currentMovie);
        showMovieDetailsIntent.putExtra("isFavoriteChoosing", isFavoriteChoosing);
        startActivity(showMovieDetailsIntent);
    }

    @Override
    public void onItemDBBClick(View view, int position) {
        Movie currentMovie = simpleAdapter.getDBItem(position);
        Intent showMovieDetailsIntent = new Intent(this, DetailsActivity.class);
        showMovieDetailsIntent.putExtra("MovieDBDetails", currentMovie);
        showMovieDetailsIntent.putExtra("isFavoriteChoosing", isFavoriteChoosing);
        startActivity(showMovieDetailsIntent);

    }
}
