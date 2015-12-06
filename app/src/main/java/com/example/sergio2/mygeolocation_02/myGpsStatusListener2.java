package com.example.sergio2.mygeolocation_02;

import android.app.Activity;
import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.widget.TextView;

import com.sergio2.auxiliares.Depuracion;

import java.util.Iterator;

/**
 * Created by sergio2 on 13/11/2015.
 */
public class myGpsStatusListener2 {
    int maxSat_ant = -1;
    int maxSat;
    GpsStatus gpsStatus2 = null;
    Iterable<GpsSatellite> satellites2;
    Iterator<GpsSatellite> sat2;
    Activity GUI;

    TextView  textDep;
    Context context;


    public myGpsStatusListener2(Context contexto) {

        //this.context = contexto;


        //this.context.textDepurador.setText("hola");


        //textDep.setText("hola");




    }


    public void onGpsStatusChanged2(int event) {

    }
}
