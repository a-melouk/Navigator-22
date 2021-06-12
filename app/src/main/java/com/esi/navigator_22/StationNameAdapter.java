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

        TextView tv = convertView.findViewById(R.id.stationName);
        TextView number = convertView.findViewById(R.id.stationNumber);
        tv.setText(station.nomFr);
        if (station.type.equals("bus")) number.setText(station.numero);
        else number.setText("Tramway");

        return convertView;
    }
}
