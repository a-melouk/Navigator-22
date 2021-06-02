package com.esi.navigator_22;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StationNameAdapter extends ArrayAdapter<Station> {

    public StationNameAdapter(Context context, int resource, List<Station> stations) {
        super(context,resource,stations);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Station station = getItem(position);
        if (convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.station_cell,parent,false);
        }

        TextView tv = convertView.findViewById(R.id.stationName);
        tv.setText(station.nomFr);

        return convertView;
    }
}
