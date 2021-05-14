package com.esi.navigator_22;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class StationAdapter extends ArrayAdapter<Station> {
    private Context mContext;
    private int mResource;

    public StationAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Station> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);
        ImageView subway = convertView.findViewById(R.id.imageSubway);
        TextView nomAr = convertView.findViewById(R.id.nomAr);
        TextView nomFr = convertView.findViewById(R.id.nomFr);
        TextView duration = convertView.findViewById(R.id.duration);
        TextView distance = convertView.findViewById(R.id.distance);

        subway.setImageResource(R.drawable.ic_tramway);
        nomAr.setText(getItem(position).nomAr);
        nomFr.setText(getItem(position).nomFr);
        duration.setText(String.valueOf(getItem(position).coordonnees.getLatitude()));
        distance.setText(String.valueOf(getItem(position).coordonnees.getLongitude()));



        return convertView;
    }
}
