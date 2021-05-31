package com.esi.navigator_22;

import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

public class CustomOverlay extends Overlay {

    String name;
    Overlay overlayItem;

    public CustomOverlay() {
        super();
    }

    public CustomOverlay(String name, Overlay overlayItem) {
        this.name = name;
        this.overlayItem = overlayItem;
    }

    @Override
    public String toString() {
        return "CustomOverlay{" +
                "name='" + name + '\'' +
                ", overlayItem=" + overlayItem +
                '}';
    }
}
