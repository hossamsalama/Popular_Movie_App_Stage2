package com.example.hossam.simpledatabase;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import java.util.List;

/**
 * Created by hossam on 8/12/2018.
 *
 */

public class ImageLoader extends AsyncTaskLoader<List<Movie>> {
    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link ImageLoader}.
     *
     * @param context of the activity
     * @param url of the activity
     */
    public ImageLoader(Context context, String url){
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of movies.
        List<Movie> movies = QueryUtils.fetchMovieData(mUrl);
        return movies;
    }
}
