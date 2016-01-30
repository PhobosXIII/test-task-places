package com.example.phobos.places.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.phobos.places.R;
import com.example.phobos.places.Utils;

import static com.example.phobos.places.data.PlacesContract.PlaceEntry;

public class PlaceAdapter extends RecyclerViewCursorAdapter<PlaceAdapter.ViewHolder> {
    private ItemClickListener itemClickListener;

    public PlaceAdapter(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void itemClicked(Cursor cursor, int position, long id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final ImageView imageView;
        public final TextView textView;
        public final TextView lastVisitedView;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            imageView = (ImageView) view.findViewById(R.id.image);
            textView = (TextView) view.findViewById(R.id.text);
            lastVisitedView = (TextView) view.findViewById(R.id.last_visited);
        }

        public void setOnClickListener(View.OnClickListener listener) {
            view.setOnClickListener(listener);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        final String text = cursor.getString(cursor.getColumnIndex(PlaceEntry.COLUMN_TEXT));
        final String lastVisited = cursor.getString(cursor.getColumnIndex(PlaceEntry.COLUMN_LAST_VISITED));
        holder.textView.setText(text);
        holder.lastVisitedView.setText(Utils.formatDate(lastVisited));
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RecyclerView recyclerView = (RecyclerView) v.getParent();
                final int position = recyclerView.getChildLayoutPosition(v);

                if (position != RecyclerView.NO_POSITION) {
                    final Cursor cursor = getItem(position);
                    int columnIdIndex = cursor.getColumnIndex(PlaceEntry._ID);
                    long id = cursor.getLong(columnIdIndex);
                    itemClickListener.itemClicked(cursor, position, id);
                }
            }
        });
    }
}
