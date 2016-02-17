package com.sergio2.auxiliares;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sergio2 on 14/02/2016.
 */
public class UtilMap {
    public static void circulosElevation(GoogleMap mapaSBS, LatLng center) {
        long RADIUS_ELEVATION_0 = 8468000; // km
        long RADIUS_ELEVATION_5 = 7918000; // km
        long RADIUS_ELEVATION_10 = 7383000; // km
        long RADIUS_ELEVATION_20 = 6341000; // km
        long RADIUS_ELEVATION_30 = 5343000; // km
        long RADIUS_ELEVATION_40 = 4386000; // km
        long RADIUS_ELEVATION_45 = 3921000; // km
        long RADIUS_ELEVATION_50 = 3464000; // km
        long RADIUS_ELEVATION_55 = 3015000; // km
        long RADIUS_ELEVATION_60 = 2572000; // km
        long RADIUS_ELEVATION_65 = 2134000; // km
        long RADIUS_ELEVATION_70 = 1702000; // km
        long RADIUS_ELEVATION_75 = 1273000; // km
        long RADIUS_ELEVATION_80 = 847000; // km
        long RADIUS_ELEVATION_85 = 423000; // km

        Circle circle_elevation_0 = mapaSBS.addCircle(new CircleOptions()
                .center(center).radius(RADIUS_ELEVATION_0)
                .strokeWidth(4)
                .strokeColor(Color.RED));
        Circle circle_elevation_10 = mapaSBS.addCircle(new CircleOptions()
                .center(center).radius(RADIUS_ELEVATION_10)
                .strokeWidth(2)
                .strokeColor(Color.BLUE));
        Circle circle_elevation_20 = mapaSBS.addCircle(new CircleOptions()
                .center(center).radius(RADIUS_ELEVATION_20)
                .strokeWidth(2)
                .strokeColor(Color.BLUE));
        Circle circle_elevation_30 = mapaSBS.addCircle(new CircleOptions()
                .center(center).radius(RADIUS_ELEVATION_30)
                .strokeWidth(2)
                .strokeColor(Color.BLUE));
        Circle circle_elevation_40 = mapaSBS.addCircle(new CircleOptions()
                .center(center).radius(RADIUS_ELEVATION_40)
                .strokeWidth(2)
                .strokeColor(Color.BLUE));
        Circle circle_elevation_45 = mapaSBS.addCircle(new CircleOptions()
                .center(center).radius(RADIUS_ELEVATION_45)
                .strokeWidth(4)
                .strokeColor(Color.BLUE));
        Circle circle_elevation_50 = mapaSBS.addCircle(new CircleOptions()
                .center(center).radius(RADIUS_ELEVATION_50)
                .strokeWidth(2)
                .strokeColor(Color.BLUE));
        Circle circle_elevation_55 = mapaSBS.addCircle(new CircleOptions()
                .center(center).radius(RADIUS_ELEVATION_55)
                .strokeWidth(1)
                .strokeColor(Color.BLUE));
        Circle circle_elevation_60 = mapaSBS.addCircle(new CircleOptions()
                .center(center).radius(RADIUS_ELEVATION_60)
                .strokeWidth(2)
                .strokeColor(Color.BLUE));
        Circle circle_elevation_65 = mapaSBS.addCircle(new CircleOptions()
                .center(center).radius(RADIUS_ELEVATION_65)
                .strokeWidth(1)
                .strokeColor(Color.BLUE));
        Circle circle_elevation_70 = mapaSBS.addCircle(new CircleOptions()
                .center(center).radius(RADIUS_ELEVATION_70)
                .strokeWidth(2)
                .strokeColor(Color.BLUE));
        Circle circle_elevation_75 = mapaSBS.addCircle(new CircleOptions()
                .center(center).radius(RADIUS_ELEVATION_75)
                .strokeWidth(1)
                .strokeColor(Color.BLUE));
        Circle circle_elevation_80 = mapaSBS.addCircle(new CircleOptions()
                .center(center).radius(RADIUS_ELEVATION_80)
                .strokeWidth(2)
                .strokeColor(Color.BLUE));
        Circle circle_elevation_85 = mapaSBS.addCircle(new CircleOptions()
                .center(center).radius(RADIUS_ELEVATION_85)
                .strokeWidth(1)
                .strokeColor(Color.BLUE));

    }
}
