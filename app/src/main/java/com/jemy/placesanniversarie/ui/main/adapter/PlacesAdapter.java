package com.jemy.placesanniversarie.ui.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jemy.placesanniversarie.R;
import com.jemy.placesanniversarie.model.Place;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder> {
    private Context context;
    private List<Place> places;
    private OnItemClickListener mListener;

    public PlacesAdapter(Context context, List<Place> places) {
        this.context = context;
        this.places = places;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_place, parent, false);
        return new PlaceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        Place currentPlace = places.get(position);
        holder.placeName.setText(currentPlace.getPlaceName());
        Glide.with(context)
                .load(currentPlace.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .centerCrop()
                .into(holder.placeImage);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView placeName;
        private ImageButton deleteIcon;
        private ImageView placeImage;

        private PlaceViewHolder(View itemView) {
            super(itemView);

            placeName = itemView.findViewById(R.id.placeName);
            deleteIcon = itemView.findViewById(R.id.deleteMarker);
            placeImage = itemView.findViewById(R.id.placeImage);

            itemView.setOnClickListener(this);
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onDeleteClick(places.get(position));
                        }
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(places.get(position));
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Place place);

        void onDeleteClick(Place place);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}