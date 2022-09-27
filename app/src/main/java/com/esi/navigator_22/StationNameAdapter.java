package com.esi.navigator_22;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class StationNameAdapter extends ArrayAdapter<Station> {

    public StationNameAdapter(Context context, int resource, List<Station> stations) {
        super(context, resource, stations);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Station station = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_cell, parent, false);
        }

        TextView name = convertView.findViewById(R.id.stationName);
        TextView number = convertView.findViewById(R.id.stationNumber);
        name.setText(station.name);
        if (station.type.equals("bus")) number.setText(removeFromStart(station.line));
        else if (station.type.equals("tramway")) number.setText("Tramway");
        else number.setText(" ");

        return convertView;
    }

    public String removeFromStart(String a) {
        return a.substring(0, 3);
    }
}
