package com.example.hossam.simpledatabase;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hossam on 3/22/2018.
 * The RecyclerView needs an adapter to populate the views in each cell with the data.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<Movie> mMovies;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<Movie> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mMovies = data;
        this.mContext = context;
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycled_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the imageview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie currentMovie = mMovies.get(position);
        Picasso.with(mContext).load(currentMovie.getURL()).into(holder.movieView);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        if (mMovies != null){
            return mMovies.size();
        }
        return 0;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView movieView;

        ViewHolder(View itemView) {
            super(itemView);
            movieView = itemView.findViewById(R.id.single_movie);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting movie at click position
    Movie getItem(int id) {
        return mMovies.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    /**
     * This method is used to set the movie on a MyRecyclerViewAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new MyRecyclerViewAdapter to display it.
     *
     * @param movies The new movie data to be displayed.
     */
    public void setMovieData(List<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }
}
