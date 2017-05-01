package com.fyp.rafill.arlocator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Search extends AppCompatActivity implements SensorEventListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    final String PLACES_API_KEY = "AIzaSyCcGh7QO2bJkVhA5nkoPblw1duCsqUkug8";
    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    Location location;

    ArrayList<Places> places = new ArrayList<>();

    EditText searchQuery;
    String query;
    Double lat, lng, alt;

    SeekBar seekBar;
    TextView seekBarValue;
    Double miles = 0.0;

    RadioGroup radioGroup;
    Boolean typeSelected = false;
    String type;

    ImageView go;
    RelativeLayout progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(1000);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        searchQuery = (EditText) findViewById(R.id.search_query);
        seekBarValue = (TextView) findViewById(R.id.seekBarValue);
        progress = (RelativeLayout) findViewById(R.id.loadingPanel);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                miles = progress / 1609.344;

                String currentProgress = String.format("%.2f", miles);
                seekBarValue.setText(currentProgress + " miles");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        go = (ImageView) findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = searchQuery.getText().toString();
                if (query.matches("")) {
                    new AlertDialog.Builder(Search.this)
                            .setTitle("Query Required")
                            .setMessage("Please do not leave search bar blank")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // close dialog
                                }
                            }).show();
                } else {
                    new ExecuteSearch().execute();
                }

            }
        });

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch(checkedId) {
                    case R.id.all:
                        typeSelected = false;
                        break;
                    case R.id.restaurants:
                        typeSelected = true;
                        type = "restaurant";
                        break;
                    case R.id.hospitals:
                        typeSelected = true;
                        type = "hospital";
                        break;
                    case R.id.banks:
                        typeSelected = true;
                        type = "bank";
                        break;
                    case R.id.mall:
                        typeSelected = true;
                        type = "shopping_mall";
                        break;
                    case R.id.taxi:
                        typeSelected = true;
                        type = "taxi_stand";
                        break;
                    case R.id.train:
                        typeSelected = true;
                        type = "train_station";
                        break;
                    case R.id.gym:
                        typeSelected = true;
                        type = "gym";
                        break;
                }
            }
        });

        places.clear();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();

        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();

        if (hasFocus) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                         int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            alt = location.getAltitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    public class ExecuteSearch extends AsyncTask<Void, Void, Void> {

        String url = completeURL(query);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            findViewById(R.id.loadingPanel).bringToFront();
        }

        @Override
        protected Void doInBackground(Void... params) {

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {

                            places.clear();
                            for (int i = 0; i < response.getJSONArray("results").length(); i++) {
                                places.add(new Places(response.getJSONArray("results").getJSONObject(i).get("place_id").toString(),
                                        response.getJSONArray("results").getJSONObject(i).get("name").toString(),
                                        response.getJSONArray("results").getJSONObject(i).get("formatted_address").toString(),
                                        response.getJSONArray("results").getJSONObject(i).getJSONObject("geometry").getJSONObject("location").get("lat").toString(),
                                        response.getJSONArray("results").getJSONObject(i).getJSONObject("geometry").getJSONObject("location").get("lng").toString(),
                                        response.getJSONArray("results").getJSONObject(i).get("icon").toString()));
                            }

                            progress.setVisibility(View.GONE);

                            if (places.size() == 1) {
                                new AlertDialog.Builder(Search.this)
                                        .setTitle("No results")
                                        .setMessage("Search came back empty. Try changing the search criteria.")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // close dialog
                                            }
                                        }).show();
                            } else {
                                Intent i = new Intent(getApplicationContext(), SearchResults.class);
                                i.putParcelableArrayListExtra("places", places);
                                i.putExtra("lat", lat);
                                i.putExtra("lng", lng);
                                i.putExtra("alt", alt);
                                startActivity(i);
                                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Null", Toast.LENGTH_LONG).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            queue.add(jsonObjectRequest);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }

        private String completeURL(String query) {
            StringBuilder completeURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?");
            int radius;

            if (Double.valueOf(miles) == 0 || Double.valueOf(miles) == 0.0 || Double.valueOf(miles) == null) {
                radius = 10000;
            } else {
                radius = miles.intValue();
            }

            query = query.replace(" ", "");

            completeURL.append("query=" + query);
            completeURL.append("&location=" + lat + "," + lng);
            completeURL.append("&radius=" + radius);
            if (typeSelected) {
                completeURL.append("&type=" + type);
            }
            completeURL.append("&key=" + PLACES_API_KEY);

            Toast.makeText(getApplicationContext(), completeURL.toString(), Toast.LENGTH_LONG).show();

            return completeURL.toString();
        }

    }
}
