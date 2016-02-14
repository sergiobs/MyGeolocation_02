package com.sergio2.auxiliares;


import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sergio2 on 11/02/2016.
 */
public class SatPos {

    static double rt = 6378;   // radius of the earth (km)
    static double hs = 20200;  // height of the GPS satellite from the surface of the earth (Km)
    static double rs = rt+hs;  // height of the GPS satellite from the center of the earth (Km)

    public static LatLng calc_LatS(LatLng pos_observador, float elevation, float azimuth) {
        double alpha = Math.PI/180*(90 - elevation) - Math.asin((rt/rs)*Math.cos(Math.PI*elevation/180)); // in radians
        double alpha_degrees = alpha *180/Math.PI;

        LatLng pos_sateliteGPS = new LatLng(
                pos_observador.latitude + alpha_degrees*Math.cos(azimuth*Math.PI/180),
                pos_observador.longitude + alpha_degrees*Math.sin(azimuth*Math.PI/180));

//

        return pos_sateliteGPS;
    }
}
