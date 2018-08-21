package com.example.hossam.simpledatabase;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

/**
 * Created by hossam on 8/20/2018.
 */

public class TrailerLoader extends AsyncTaskLoader<List<String>> {
    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link ReviewLoader}.
     *
     * @param context of the activity
     * @param url of the activity
     */
    public TrailerLoader(Context context, String url){
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<String> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of movies.
        List<String> moviesTrailers = QueryUtils.fetchTrailersData(mUrl);
        return moviesTrailers;
    }
}

