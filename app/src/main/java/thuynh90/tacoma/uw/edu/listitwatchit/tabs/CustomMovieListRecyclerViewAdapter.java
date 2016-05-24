package thuynh90.tacoma.uw.edu.listitwatchit.tabs;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import thuynh90.tacoma.uw.edu.listitwatchit.R;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.CustomMovieListFragment.CustomMovieListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Movie} and makes a call to the
 * specified {@link CustomMovieListFragmentInteractionListener}.
 */
public class CustomMovieListRecyclerViewAdapter extends RecyclerView.Adapter<CustomMovieListRecyclerViewAdapter.ViewHolder> {

    private final List<Movie> mValues;
    private final CustomMovieListFragmentInteractionListener mListener;

    public CustomMovieListRecyclerViewAdapter(List<Movie> items, CustomMovieListFragmentInteractionListener listener) {
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
                    mListener.onCustomMovieListFragmentInteraction(holder.mItem);
                    // viewDetails
                }
            }
        });

        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCustomMovieListFragmentInteraction(holder.mItem);
                //deleteMovie
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
