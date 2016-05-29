package thuynh90.tacoma.uw.edu.listitwatchit.tabs;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import thuynh90.tacoma.uw.edu.listitwatchit.R;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.WatchedFragment.WatchedListFragmentInteractionListener;


/**
 * Provides layout for movie lists in tabs.
 */
public class WatchedRecyclerViewAdapter extends RecyclerView.Adapter<WatchedRecyclerViewAdapter.ViewHolder> {

    private final List<Movie> mValues;
    private final WatchedListFragmentInteractionListener mListener;

    /**
     * Creates a RecyclerViewAdapter for movie lists
     * @param items List of movies
     * @param listener toWatchFragmentInteractionListener
     */
    public WatchedRecyclerViewAdapter(List<Movie> items, WatchedListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).getMovieTitle());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.watchedFragmentInteraction(holder.mItem, "viewDetails");
                }
            }
        });

        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.watchedFragmentInteraction(holder.mItem, "deleteMovie");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final ImageButton mDelete;
        public Movie mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.title);
            mDelete = (ImageButton) view.findViewById(R.id.delete_watched);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
