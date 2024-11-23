package com.example.ikuzo;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.LocationViewHolder> {
    private List<LatLng> locations;
    private final OnStartDragListener dragListener;

    public interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    public LocationsAdapter(List<LatLng> locations, OnStartDragListener dragListener) {
        this.locations = new ArrayList<>(locations);
        this.dragListener = dragListener;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itinerary_location_item, parent, false);
        return new LocationViewHolder(view);
    }
    // Add this method to update locations
    public void updateLocations(List<LatLng> newLocations) {
        this.locations = newLocations;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        LatLng location = locations.get(position);
        // Set location details
        holder.itemView.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                dragListener.onStartDrag(holder);
            }
            return false;
        }); // Assuming TextViews are defined in your ViewHolder
        TextView nameTextView = holder.itemView.findViewById(R.id.locationTitle);
        TextView addressTextView = holder.itemView.findViewById(R.id.locationAddress);

        // Example: Populate TextViews (You can add more details if needed)
        nameTextView.setText(String.format(Locale.getDefault(), "Location %d", position + 1));
        addressTextView.setText(String.format(Locale.getDefault(), "Lat: %.2f, Lng: %.2f",
                location.latitude, location.longitude));

        holder.itemView.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                dragListener.onStartDrag(holder);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(locations, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public List<LatLng> getLocations() {
        return new ArrayList<>(locations);
    }

    static class LocationViewHolder extends RecyclerView.ViewHolder {
        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}