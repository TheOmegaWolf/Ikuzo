package com.example.ikuzo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class ItineraryList extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private List<List<LatLng>> dailyItineraries;
    private int days = 1; // Example: You can change based on user selection
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_list);

        // Retrieve selected locations and number of days from intent
        ArrayList<LatLng> locations = getIntent().getParcelableArrayListExtra("LOCATIONS");
        days = getIntent().getIntExtra("DURATION",0);
        Log.d("ItineraryList", "Days: " + days);
        Log.d("ItineraryList", "locations: " + locations);

        if (locations == null || locations.isEmpty() || days <= 0) {
            Toast.makeText(this, "Invalid itinerary data!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Split locations into daily itineraries
        dailyItineraries = splitLocationsByDays(locations, days);

        // Initialize MapView
        mapView = findViewById(R.id.mapView);
        Bundle mapViewBundle = savedInstanceState != null ? savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY) : null;
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    // Method to split locations into daily groups based on the number of days
    private List<List<LatLng>> splitLocationsByDays(List<LatLng> locations, int days) {
        List<List<LatLng>> dailyItineraries = new ArrayList<>();
        int locationsPerDay = locations.size() / days;
        int extra = locations.size() % days;

        int start = 0;
        for (int i = 0; i < days; i++) {
            int end = start + locationsPerDay + (i < extra ? 1 : 0);
            dailyItineraries.add(new ArrayList<>(locations.subList(start, end)));
            start = end;
        }
        return dailyItineraries;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        // Loop through each day’s itinerary to add markers and polylines
        for (int day = 0; day < dailyItineraries.size(); day++) {
            List<LatLng> dayLocations = dailyItineraries.get(day);

            if (!dayLocations.isEmpty()) {
                // Add a marker for each stop in the day's itinerary
                for (int i = 0; i < dayLocations.size(); i++) {
                    LatLng location = dayLocations.get(i);
                    String title = "Day " + (day + 1) + " - Stop " + (i + 1);
                    googleMap.addMarker(new MarkerOptions().position(location).title(title));
                }

                // Add a polyline to connect all stops for the day
                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(dayLocations)
                        .clickable(true);

                googleMap.addPolyline(polylineOptions);

                // Move the camera to the first stop of the first day
                if (day == 0) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dayLocations.get(0), 12));
                }
            }
        }
    }

    // MapView lifecycle methods
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }
}
