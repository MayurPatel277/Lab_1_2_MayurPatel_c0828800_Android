package com.example.lab_1_2_mayurpatel_c0828800_android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.lab_1_2_mayurpatel_c0828800_android.constants.Constants;
import com.example.lab_1_2_mayurpatel_c0828800_android.database.AppDatabase;
import com.example.lab_1_2_mayurpatel_c0828800_android.database.AppExecutors;
import com.example.lab_1_2_mayurpatel_c0828800_android.model.Product;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditActivity extends AppCompatActivity {

    public static final String TAG = "EditProduct";
    public static final int REQUEST_CODE = 1;
    public static final int UPDATE_INTERVAL = 5000; // 5 secs
    public static final int FASTEST_INTERVAL = 3000; // 3secs
    public static String lat;

    //3rd
    private FusedLocationProviderClient fusedLocationProviderClient;
    private com.google.android.gms.location.LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private List<String> permissionToRequest;
    private List<String> permission = new ArrayList<>();
    private List<String> permissionsRejected = new ArrayList<>();


    EditText name, description, price, latitude, longitude;
    Button button;
    int mProductId;
    Intent intent;
    private AppDatabase mDb;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initViews();
        mDb = AppDatabase.getInstance(getApplicationContext());
        intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.UPDATE_Product_Id)) {
            button.setText("Update");

            mProductId = intent.getIntExtra(Constants.UPDATE_Product_Id, -1);

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    Product product = mDb.productDao().loadProductById(mProductId);
                    populateUI(product);
                }
            });


        }

    }

    private void populateUI(Product product) {

        if (product == null) {
            return;
        }

        name.setText(product.getName());
        description.setText(product.getDescription());
        price.setText(product.getPrice());
        latitude.setText(product.getLatitude());
        longitude.setText(product.getLongitude());
    }

    private void initViews() {
        name = findViewById(R.id.edit_name);
        description = findViewById(R.id.edit_description);
        price = findViewById(R.id.edit_price);
        latitude = findViewById(R.id.edit_Lat);
        longitude = findViewById(R.id.edit_Lon);
        button = findViewById(R.id.button);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        permission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissionToRequest = permissionToRequest(permission);
        if (permissionToRequest.size() > 0) {
            requestPermissions(permissionToRequest.toArray(new String[permissionToRequest.size()]), REQUEST_CODE);
        }

        latitude = findViewById(R.id.edit_Lat);



        // location
        longitude = findViewById(R.id.edit_Lon);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonClicked();
            }
        });
    }

    public void onSaveButtonClicked() {
        final Product productt = new Product(
                name.getText().toString(),
                description.getText().toString(),
                price.getText().toString(),
                latitude.getText().toString(),
                longitude.getText().toString());

    AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (!intent.hasExtra(Constants.UPDATE_Product_Id)) {
                    mDb.productDao().insertProduct(productt);
                } else {
                    productt.setId(mProductId);
                    mDb.productDao().updateProduct(productt);
                }
                finish();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(this,
                    REQUEST_CODE, REQUEST_CODE, dialog -> {
                        Toast.makeText(this, "The service is not available",
                                Toast.LENGTH_SHORT).show();
                    });
            errorDialog.show();
        } else {
            findLocation();
        }
    }

    private void findLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d(TAG, "onSuccess" + location.getLatitude() + " " + location.getLongitude());
                }
            }
        });
        startUpdatingLocation();


    }
    @SuppressLint("Issue with Permissions") // COMPLATE ONLY
    private void startUpdatingLocation() {


        locationRequest = locationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                Log.d(TAG, "onLocationResult: " + location.getLatitude() + " " + location.getLongitude());

                lat = Double.toString(location.getLatitude());
                latitude.setText(lat);
                longitude.setText(Double.toString(location.getLongitude()));

                String address = "";
                Geocoder gg = new Geocoder(EditActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = gg.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                    if(address!=null)
                    {
                        Address returnAddress = addresses.get(0);
                        StringBuilder stringBuilderReturnAddress = new StringBuilder("");

                        for (int i= 0; i <= returnAddress.getMaxAddressLineIndex();i++)
                        {
                            stringBuilderReturnAddress.append(returnAddress.getAddressLine(i)).append("\n");
                        }


                    }
                }

                catch(Exception e)
                {
                    Toast.makeText(EditActivity.this, "invalid address", Toast.LENGTH_SHORT).show();
                }


            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Void> voidTask = fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private List<String> permissionToRequest(List<String> permission)
    {
        ArrayList<String> results = new ArrayList<>();
        for (String permissio : permission) {
            if(!isGranted(permissio)) {
                results.add(permissio);
            }
        }
        return results;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isGranted(String permission) {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(REQUEST_CODE == requestCode) {
            for(String permission : permissions){
                if(!isGranted(permission)){
                    permissionsRejected.add(permission);
                }
            }
        }

        if(permissionsRejected.size() > 0 ) {

            if(shouldShowRequestPermissionRationale(permissionsRejected.get(0))){
                new AlertDialog.Builder(this).setMessage("Accessing the location is Mandatory").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(permissionsRejected.toArray(new String[permissionToRequest.size()]),REQUEST_CODE);
                    }
                }).setNegativeButton("Cancel", null).create().show();
            }
        }
    }



}