package thuynh90.tacoma.uw.edu.listitwatchit.tabs;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import thuynh90.tacoma.uw.edu.listitwatchit.R;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.ToWatchFragment.toWatchFragmentInteractionListener;


/**
 * Provides layout for "To Watch" list in tabs.
 */
public class ToWatchRecyclerViewAdapter extends RecyclerView.Adapter<ToWatchRecyclerViewAdapter.ViewHolder> {

    private final List<Movie> mValues;
    private final toWatchFragmentInteractionListener mListener;

    /**
     * Creates a RecyclerViewAdapter for movie lists
     * @param items List of movies
     * @param listener toWatchFragmentInteractionListener
     */
    public ToWatchRecyclerViewAdapter(List<Movie> items, toWatchFragmentInteractionListener listener) {
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
        //holder.mIdView.setText(mValues.get(position).getMovieReleaseDate());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.toWatchFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        // public final TextView mIdView;
        public final TextView mContentView;
        public final ImageButton watched;
       // public final ImageButton delete;
        public Movie mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            //mIdView = (TextView) view.findViewById(R.id.title);
            mContentView = (TextView) view.findViewById(R.id.title);
            watched = (ImageButton) view.findViewById(R.id.watched_to_watch);
            //delete = (ImageButton) view.findViewById(R.id.delete_to_watch);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
