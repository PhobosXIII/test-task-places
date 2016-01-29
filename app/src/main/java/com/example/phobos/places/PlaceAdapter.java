package com.example.phobos.places;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {
    private final List<Place> places;
    private ItemClickListener itemClickListener;

    public PlaceAdapter(List<Place> places, ItemClickListener itemClickListener) {
        this.places = places;
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void itemClicked(Place place);
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Place place = places.get(position);
        holder.textView.setText(places.get(position).getText());
        holder.lastVisitedView.setText(places.get(position).getLastVisited().toString());
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(place);
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }
}
