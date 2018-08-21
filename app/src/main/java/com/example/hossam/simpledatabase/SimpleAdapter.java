package com.example.hossam.simpledatabase;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hossam.simpledatabase.data.TaskContract;

/**
 * Created by hossam on 8/10/2018.
 *
 */

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.TaskViewHolder>{

    private Context mContext;
    private Cursor mCursor;

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

        int idColumnIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_ID);
        int titleColumnIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TITLE);

        mCursor.moveToPosition(position);

        String id = mCursor.getString(idColumnIndex);
        String title = mCursor.getString(titleColumnIndex);

        holder.movieID.setText(id);
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
        public TaskViewHolder(View view) {
            super(view);

            movieID = view.findViewById(R.id.movie_id);
            movieTitle = view.findViewById(R.id.movie_favorit_title);
        }

        @Override
        public void onClick(View view) {

        }
    }
}



