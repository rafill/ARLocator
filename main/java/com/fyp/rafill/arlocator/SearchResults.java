package com.fyp.rafill.arlocator;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchResults extends AppCompatActivity {

    ListView listView;
    ArrayList<Places> places = new ArrayList<>();
    PlacesAdapter adapter;

    Double lat, lng, alt;

    ImageButton arView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        listView = (ListView) findViewById(R.id.listView);
        arView = (ImageButton) findViewById(R.id.arView);

        Intent i = getIntent();

        places.clear();
        places = i.getParcelableArrayListExtra("places");
        lat = i.getDoubleExtra("lat", 0);
        lng = i.getDoubleExtra("lng", 0);
        alt = i.getDoubleExtra("alt", 0);

        adapter = new PlacesAdapter(this, places);
        listView.setAdapter(adapter);

        arView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CameraActivity.class);
                i.putParcelableArrayListExtra("places", places);
                i.putExtra("lat", lat);
                i.putExtra("lng", lng);
                i.putExtra("alt", alt);
                startActivity(i);
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String lat = adapter.getItem(position).getIcon();

                Toast.makeText(getApplicationContext(), lat, Toast.LENGTH_SHORT).show();
            }
        });

        //Toast.makeText(getApplicationContext(), places.get(0).getName(), Toast.LENGTH_LONG).show();
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

}
