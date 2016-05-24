package thuynh90.tacoma.uw.edu.listitwatchit.tabs;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import thuynh90.tacoma.uw.edu.listitwatchit.R;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.MyListsFragment.MyListsFragmentInteractionListener;

/**
 * Provides layout for My Lists tab
 */
public class MyListsRecyclerViewAdapter extends RecyclerView.Adapter<MyListsRecyclerViewAdapter.ViewHolder> {

    private final List<MyList> mValues;
    private final MyListsFragmentInteractionListener mListener;

    public MyListsRecyclerViewAdapter(List<MyList> items, MyListsFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        //holder.mIdView.setText(mValues.get(position).getListID());
        holder.mContentView.setText(mValues.get(position).getListName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.myListFragmentInteraction(holder.mItem, "viewList");
                }
            }
        });

        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.myListFragmentInteraction(holder.mItem, "deleteList");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //public final TextView mIdView;
        public final TextView mContentView;
        public final ImageButton mDelete;
        public MyList mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            //mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mDelete = (ImageButton) view.findViewById(R.id.delete_my_lists);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
