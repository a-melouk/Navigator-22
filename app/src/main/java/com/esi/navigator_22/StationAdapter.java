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

import java.util.ArrayList;

public class StationAdapter extends ArrayAdapter<StationDetails> {
    private final Context mContext;
    private final int mResource;

    public StationAdapter(@NonNull Context context, int resource, @NonNull ArrayList<StationDetails> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);
        ImageView tramway = convertView.findViewById(R.id.imageTramway);
//        TextView nomAr = convertView.findViewById(R.id.nomAr);
        TextView nomFr = convertView.findViewById(R.id.nomFr);
        TextView distance = convertView.findViewById(R.id.distance);
        TextView time = convertView.findViewById(R.id.time);


        tramway.setImageResource(R.drawable.icon_tram);
//        nomAr.setText(getItem(position).numero);
        nomFr.setText(getItem(position).name);
        distance.setText(String.valueOf(getItem(position).distanceTo));
        time.setText(String.valueOf((int) getItem(position).timeTo));

        return convertView;
    }
}
