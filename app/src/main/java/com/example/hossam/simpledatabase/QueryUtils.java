package com.example.hossam.simpledatabase;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.hossam.simpledatabase.MainActivity.LOG_TAG;

/**
 * Helper methods related to requesting and receiving movies data from Movies_URL.
 */

public class QueryUtils {
    /**
     * Query the Movies_URL dataset and return an {@link ArrayList} object to represent a single Movie.
     */
    public static List<Movie> fetchMovieData(String requestUrl) {

        String jsonResponse = prepareURL(requestUrl);

        //Log.e(LOG_TAG, "Test:fetchMoviesData");
        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<Movie> movies = extractMovies(jsonResponse);

        // Return the {@link Event}
        return movies;
    }

    static List<String> fetchReviewsData(String requestUrl) {

        String jsonResponse = prepareURL(requestUrl);

        //Log.e(LOG_TAG, "Test:fetchMoviesData");
        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<String> reviews = extractReviews(jsonResponse);

        // Return the {@link Event}
        return reviews;
    }

    static List<String> fetchTrailersData(String requestUrl) {

        String jsonResponse = prepareURL(requestUrl);

        //Log.e(LOG_TAG, "Test:fetchMoviesData");
        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<String> trailers = extractTrailers(jsonResponse);

        // Return the {@link Event}
        return trailers;
    }

    private static String prepareURL(String requiredUrl){
        // Create URL object
        URL url = createUrl(requiredUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        return jsonResponse;
    }
    /**
     * Return a list of {@link Movie} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Movie> extractMovies(String jsonResponse) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding movies to
        List<Movie> movies = new ArrayList<>();

        String movieImageUrl = "";

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Parse the response given by the String jsonResponse string and
            // build up a list of Movies objects with the corresponding data.
            JSONObject moviesData = new JSONObject(jsonResponse);

            JSONArray results = moviesData.getJSONArray("results");

            //Iterate through each movie in the result array
            for (int i = 0; i < results.length(); i++) {

                JSONObject movieObject = results.getJSONObject(i);

                String posterPath = movieObject.getString("poster_path");
                //Get the movie ID
                int movieID = movieObject.getInt("id");
                //Get movie's title
                String title = movieObject.getString("title");
                //Get movie's popularity
                Number popularity = movieObject.getInt("popularity");
                //Get number of votes for a movie
                int votes = movieObject.getInt("vote_count");
                //Get the average number of votes for a movie
                Number averageVote = movieObject.getInt("vote_average");

                String releaseDate = movieObject.getString("release_date");

                String overview = movieObject.getString("overview");

                movieImageUrl = Movie.BASE_IMAGE_URL + posterPath;

                Movie movie = new Movie(posterPath, movieID, title, popularity, votes, averageVote, movieImageUrl, releaseDate, overview);

                movies.add(movie);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the movies JSON results", e);
        }
        //return list of movies
        return movies;
    }
    /**
     * Return a list of {@link Movie} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<String> extractReviews(String jsonResponse) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding movies to
        List<String> moviesReviews = new ArrayList<>();

        //String movieImageUrl = "";

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Parse the response given by the String jsonResponse string and
            // build up a list of Movies objects with the corresponding data.
            JSONObject moviesData = new JSONObject(jsonResponse);

            JSONArray results = moviesData.getJSONArray("results");

            //Iterate through each movie in the result array
            for (int i = 0; i < results.length(); i++) {

                JSONObject movieObject = results.getJSONObject(i);

                //Reviewer name
                String author = movieObject.getString("author");

                //Reviews for specific author
                String reviews = movieObject.getString("content");

                moviesReviews.add(author);
                //Movie movie = new Movie(posterPath, movieID, title, popularity, votes, averageVote, movieImageUrl, releaseDate, overview);m
                moviesReviews.add(reviews);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the movies JSON results", e);
        }
        //return list of movies's reviews
        return moviesReviews;
    }

    /**
     * Return a list of {@link Movie} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<String> extractTrailers(String jsonResponse) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding movies to
        List<String> moviesTrailers = new ArrayList<>();

        String movieTrailerUrl = "";

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Parse the response given by the String jsonResponse string and
            // build up a list of Movies objects with the corresponding data.
            JSONObject moviesData = new JSONObject(jsonResponse);

            JSONArray results = moviesData.getJSONArray("results");

            //Iterate through each movie in the result array
            for (int i = 0; i < results.length(); i++) {

                JSONObject movieObject = results.getJSONObject(i);

                //Trailer's key
                String key = movieObject.getString("key");

                movieTrailerUrl = "https://www.youtube.com/watch?v=" + key;
                moviesTrailers.add(movieTrailerUrl);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the movies JSON results", e);
        }
        //return list of movies's reviews
        return moviesTrailers;
    }
    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the movies JSON results.", e);
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }
    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }
}


