package com.jemy.placesanniversarie.ui.addplace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.jemy.placesanniversarie.R;
import com.jemy.placesanniversarie.model.Place;
import com.jemy.placesanniversarie.ui.map.MapActivity;
import com.jemy.placesanniversarie.utils.Constants;

public class AddPlaceActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private MaterialButton saveButton;
    private MaterialButton pickImageButton;
    private TextInputEditText placeNameEditText;
    private TextView latitudeTextView, longitudeTextView, pickLocationTexView;
    private ImageView placeImage;
    private ProgressBar progressBar;
    private DatabaseReference dbReference;
    private StorageReference storageReference;
    private Uri imageUri;
    private StorageTask uploadTask;
    private Double latitude = 0.0, longitude = 0.0;
    private String name, placeId, imageUrl;
    private ViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        setTitle("Add Place");
        instantiateViews();
        getDataFromIntent();
        instantiateDatabase();
        setupSaveButtonClickListener();
        setupPickImageClickListener();
        setupPickLocationTexViewClickListener();
    }

    private void instantiateViews() {
        saveButton = findViewById(R.id.saveButton);
        pickImageButton = findViewById(R.id.pickImageButton);
        latitudeTextView = findViewById(R.id.latitude);
        longitudeTextView = findViewById(R.id.longitude);
        placeImage = findViewById(R.id.placeImage);
        placeNameEditText = findViewById(R.id.placeNameEditText);
        progressBar = findViewById(R.id.addPlaceProgressBar);
        pickLocationTexView = findViewById(R.id.locationMarker);
    }

    private void instantiateDatabase() {
        dbReference = FirebaseDatabase.getInstance().getReference("places");
        storageReference = FirebaseStorage.getInstance().getReference("places");
    }

    private void savePlace() {
        progressBar.setVisibility(View.VISIBLE);
        name = placeNameEditText.getText().toString().trim();
        if (imageUri == null || TextUtils.isEmpty(name) || latitude == 0.0 || longitude == 0.0) {
            Toast.makeText(AddPlaceActivity.this, "Please enter the missing data", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();
                            placeId = dbReference.push().getKey();
                            imageUrl = downloadUrl.toString();
                            Place place = new Place(placeId, name, latitude, longitude,
                                    imageUrl);
                            if (placeId != null) {
                                dbReference.child(placeId).setValue(place).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(AddPlaceActivity.this, "Place added successfully", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddPlaceActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddPlaceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void setupSaveButtonClickListener() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(AddPlaceActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    savePlace();
                }
            }
        });
    }

    private void setupPickImageClickListener() {
        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
    }

    private void setupPickLocationTexViewClickListener() {
        pickLocationTexView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddPlaceActivity.this, MapActivity.class);
                intent.putExtra(Constants.LATITUDE, latitude);
                intent.putExtra(Constants.LONGITUDE, longitude);
                finish();
                startActivity(intent);
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        String fromWhere = intent.getStringExtra(Constants.FROM_WHERE);
        latitude = intent.getDoubleExtra(Constants.LATITUDE, 0.0);
        longitude = intent.getDoubleExtra(Constants.LONGITUDE, 0.0);
        latitudeTextView.setText(latitude.toString());
        longitudeTextView.setText(longitude.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(placeImage);
        }
    }
}
