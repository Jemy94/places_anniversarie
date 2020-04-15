package com.jemy.placesanniversarie.ui.map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.jemy.placesanniversarie.R;
import com.jemy.placesanniversarie.ui.addplace.AddPlaceActivity;
import com.jemy.placesanniversarie.utils.Constants;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private MapView mapView;
    private MaterialButton doneButton;
    private Double latitude , longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        instantiateViews();
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        setupDoneButtonClickListener();

    }

    private void instantiateViews() {
        mapView = findViewById(R.id.mapView);
        doneButton = findViewById(R.id.doneButton);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                map.clear();
                latitude = latLng.latitude;
                longitude=latLng.longitude;
                map.addMarker(markerOptions);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7f));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void setupDoneButtonClickListener(){
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, AddPlaceActivity.class);
                intent.putExtra(Constants.FROM_WHERE,Constants.FROM_MAP);
                intent.putExtra(Constants.LATITUDE,latitude);
                intent.putExtra(Constants.LONGITUDE,longitude);
                finish();
                startActivity(intent);

            }
        });
    }
}
