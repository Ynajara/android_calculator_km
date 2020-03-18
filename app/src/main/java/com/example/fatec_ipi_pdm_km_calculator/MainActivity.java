package com.example.fatec_ipi_pdm_km_calculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private static final int GPS_REQUEST_CODE = 1001;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location locationA = new Location("point A");
    private Location locationB = new Location("point B");

    private Switch permissionGPS;
    private Switch turnOnGPS;
    private EditText locationEditText;
    private Button locationButton;
    private Button startButton;
    private Button finishButton;

    private double latitude;
    private double longitude;
    private double latitudeStart;
    private double latitudeFinish;
    private double longitudeStart;
    private double longitudeFinish;
    private double kilometragem;
    private double distance;
    private long seconds;

    private void configurarGPS() {

        locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getAltitude();
                longitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

    }
    private void solicitarPermissao(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                GPS_REQUEST_CODE);
        }
        else{
            //retirar permissão
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionGPS = findViewById(R.id.permissionGPS);
        turnOnGPS = findViewById(R.id.turnOnGPS);
        locationEditText = findViewById(R.id.locationEditText);
        locationButton = findViewById(R.id.locationButton);
        startButton = findViewById(R.id.startButton);
        finishButton = findViewById(R.id.finishButton);
        locationButton.setEnabled(false);
        final Chronometer myChronometer = findViewById(R.id.chronometer);

        permissionGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    solicitarPermissao();
                    String text = "GPS Autorizado!";
                    Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
                }
                else {
                    solicitarPermissao();
                    locationButton.setEnabled(false);
                    turnOnGPS.setChecked(false);
                    String text = "GPS Desativado!";
                    Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
                }
            }
        });

        turnOnGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    configurarGPS();
                    locationButton.setEnabled(true);
                    String text = "GPS Ligado!";
                    Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
                }
                else {
                    locationButton.setEnabled(false);
                    locationManager.removeUpdates(locationListener);
                    String text = "GPS Desligado!";
                    Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
                }
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationInput = locationEditText.getText().toString();
                Uri uri = Uri.parse(String.format("geo:%f,%f?q=%s", latitude, longitude, locationInput));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
                Log.d("log_latitude", String.format("%f",latitude));
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latitudeStart = latitude;
                longitudeStart = longitude;
                locationA.setLatitude(latitude);
                locationA.setLongitude(longitude);
                myChronometer.setBase(SystemClock.elapsedRealtime());
                myChronometer.start();
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latitudeFinish = latitude;
                longitudeFinish = longitude;
                locationB.setLatitude(latitude);
                locationB.setLongitude(longitude);
                distance = locationA.distanceTo(locationB);
                myChronometer.stop();
                seconds = SystemClock.elapsedRealtime() - myChronometer.getBase()/60000 ;
                Toast.makeText(getBaseContext(), "Distancia Percorrida: " + distance + " Tempo: " + seconds+"milsegundos", Toast.LENGTH_LONG).show();
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GPS_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                    locationButton.setEnabled(true);
                }
                else{
                    Toast.makeText(this, getString(R.string.no_gps_no_app),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationEditText.setText("");
    }
}
