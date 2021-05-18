package com.esi.navigator_22.utils;

import org.osmdroid.views.MapView;

public interface MapListener {
    void mapLoadSuccess(MapView mapView, MapUtils mapUtils);

    void mapLoadFailed(String ex);
}
