package com.example.hossam.simpledatabase;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hossam.simpledatabase.data.TaskContract;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hossam on 8/10/2018.
 *
 */

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.TaskViewHolder>{

    private Context mContext;
    private Cursor mCursor;
    private ItemClickListener mClickListener;
    String mID;
    String posterUrl;
    String title;
    String releaseDate;
    String votes;
    String overview;
    String reviewUrl;
    String trailerUrl;
    private int idColumnIndex;
    private int posterColumnIndex;
    private int titleColumnIndex;
    private int dateColumnIndex;
    private int voteColumnIndex;
    private int overviewColumnIndex;
    private int reviewColumnIndex;
    private int trailerColumnIndex;


    public SimpleAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    @Override
    public SimpleAdapter.TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycled_item, parent, false);

        return new TaskViewHolder(view);

    }

    @Override
    public void onBindViewHolder(SimpleAdapter.TaskViewHolder holder, int position) {

        idColumnIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_ID);
        posterColumnIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_POSTER);
        titleColumnIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TITLE);
        dateColumnIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_RELEASE_DATE);
        voteColumnIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_VOTES);
        overviewColumnIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_OVERVIEW);
        reviewColumnIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_REVIEW_URL);
        trailerColumnIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TRAILER_URL);

        mCursor.moveToPosition(position);

        mID = mCursor.getString(idColumnIndex);
        posterUrl = mCursor.getString(posterColumnIndex);
        title = mCursor.getString(titleColumnIndex);
        releaseDate = mCursor.getString(dateColumnIndex);
        votes = mCursor.getString(voteColumnIndex);
        overview = mCursor.getString(overviewColumnIndex);
        reviewUrl = mCursor.getString(reviewColumnIndex);
        trailerUrl = mCursor.getString(trailerColumnIndex);

        holder.movieID.setText(mID);
        Picasso.with(mContext).load(posterUrl).into(holder.movieView);
        holder.movieTitle.setText(title);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView movieID;
        TextView movieTitle;
        ImageView movieView;
        public TaskViewHolder(View view) {
            super(view);
            movieView = view.findViewById(R.id.single_movie);
            movieID = view.findViewById(R.id.movie_id);
            movieTitle = view.findViewById(R.id.movie_favorit_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemDBBClick(view, getAdapterPosition());
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemDBBClick(View view, int position);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
    // convenience method for getting movie at click position
    Movie getDBItem(int position) {
        mCursor.moveToPosition(position);
        //onBindViewHolder(holder, position);
        mID = mCursor.getString(idColumnIndex);
        posterUrl = mCursor.getString(posterColumnIndex);
        title = mCursor.getString(titleColumnIndex);
        releaseDate = mCursor.getString(dateColumnIndex);
        votes = mCursor.getString(voteColumnIndex);
        overview = mCursor.getString(overviewColumnIndex);
        reviewUrl = mCursor.getString(reviewColumnIndex);
        trailerUrl = mCursor.getString(trailerColumnIndex);
        return new Movie(Integer.parseInt(mID), posterUrl, title, releaseDate, Integer.parseInt(votes), overview, reviewUrl, trailerUrl);
    }
}



