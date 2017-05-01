package com.fyp.rafill.arlocator;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by rafill on 17/04/2017.
 */

public class PlacesAdapter extends ArrayAdapter<Places> {

    public PlacesAdapter(Context context, ArrayList<Places> places) {
        super(context, 0, places);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Places place = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_place, parent, false);
        }

        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView address = (TextView) convertView.findViewById(R.id.address);
        TextView id = (TextView) convertView.findViewById(R.id.place_id);
        TextView lat = (TextView) convertView.findViewById(R.id.lat);
        TextView lng = (TextView) convertView.findViewById(R.id.lng);

        // Populate the data into the template view using the data object
        name.setText(place.getName());
        address.setText(place.getAddress());
        id.setText(place.getID());
        lat.setText(place.getLat());
        lng.setText(place.getLng());

        // Return the completed view to render on screen
        return convertView;
    }
}
