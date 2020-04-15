package com.jemy.placesanniversarie.ui.main;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jemy.placesanniversarie.R;
import com.jemy.placesanniversarie.model.Place;
import com.jemy.placesanniversarie.ui.addplace.AddPlaceActivity;
import com.jemy.placesanniversarie.ui.main.adapter.PlacesAdapter;
import com.jemy.placesanniversarie.ui.map.MapActivity;
import com.jemy.placesanniversarie.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PlacesAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private PlacesAdapter adapter;
    private ProgressBar progressCircle;
    private FirebaseStorage storage;
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
        storage = FirebaseStorage.getInstance();
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
        places = new ArrayList<>();
        adapter = new PlacesAdapter(MainActivity.this, places);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(MainActivity.this);
    }

    private void loadPlaces() {
        progressCircle.setVisibility(View.VISIBLE);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                places.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Place place = postSnapshot.getValue(Place.class);
                    places.add(place);
                }
                adapter.notifyDataSetChanged();
                if (places.isEmpty()) {
                    noData.setVisibility(View.VISIBLE);
                } else {
                    noData.setVisibility(View.INVISIBLE);
                }
                progressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onItemClick(Place place) {
        Log.d("MainActivity", "item clicked");
        Intent intent = new Intent(MainActivity.this, AddPlaceActivity.class);
        intent.putExtra(Constants.PLACE, place);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(final Place place) {
        Log.d("MainActivity", "delete clicked");
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.message)
                .setMessage(R.string.delete_confirmation_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StorageReference imageRef = storage.getReferenceFromUrl(place.getImageUrl());
                        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                databaseRef.child(place.getId()).removeValue();
                                Toast.makeText(MainActivity.this, "Place deleted", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }
}
