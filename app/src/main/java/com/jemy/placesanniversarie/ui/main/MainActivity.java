package com.jemy.placesanniversarie.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jemy.placesanniversarie.R;
import com.jemy.placesanniversarie.model.Place;
import com.jemy.placesanniversarie.ui.addplace.AddPlaceActivity;
import com.jemy.placesanniversarie.ui.main.adapter.PlacesAdapter;
import com.jemy.placesanniversarie.ui.map.MapActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlacesAdapter adapter;
    private ProgressBar progressCircle;

    private DatabaseReference databaseRef;
    private List<Place> places;
    private TextView noData;
    private FloatingActionButton addPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instantiateViews();
        navigateToAddPlaceActivity();
        setupDataBaseReference();
        loadPlaces();
    }

    private void setupDataBaseReference() {
        databaseRef = FirebaseDatabase.getInstance().getReference("places");
    }

    private void navigateToAddPlaceActivity() {
        addPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddPlaceActivity.class);
                startActivity(intent);
            }
        });
    }

    private void instantiateViews() {
        addPlace = findViewById(R.id.addPlaceButton);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressCircle = findViewById(R.id.homeProgressBar);
        noData = findViewById(R.id.noDataTextView);
       places= new ArrayList<>();
    }

    private void loadPlaces(){
        progressCircle.setVisibility(View.VISIBLE);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Place place = postSnapshot.getValue(Place.class);
                    places.add(place);
                }

                adapter = new PlacesAdapter(MainActivity.this, places);
                recyclerView.setAdapter(adapter);
                progressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }


}
