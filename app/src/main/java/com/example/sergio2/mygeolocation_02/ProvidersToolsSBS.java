package com.example.sergio2.mygeolocation_02;

import android.location.LocationManager;
import android.widget.TextView;

import com.sergio2.auxiliares.Depuracion;

import java.util.List;

/**
 * Created by sergio2 on 08/12/2015.
 */
public class ProvidersToolsSBS {
    public static void printInfoProviders2(LocationManager lm, TextView tv) {
        List<String> listProviders = lm.getAllProviders();
        for (int i=0;i<listProviders.size(); i++) {
            Depuracion.traza("provider.getName(): " + lm.getProvider(listProviders.get(i)).getName()
                    + ", getAccuracy(): " + lm.getProvider(listProviders.get(i)).getAccuracy()
                    + ", supportsAltitude(): " + lm.getProvider(listProviders.get(i)).supportsAltitude()
                    + ", getPowerRequirement(): " + lm.getProvider(listProviders.get(i)).getPowerRequirement()
                    + ", requiresCell(): " + lm.getProvider(listProviders.get(i)).requiresCell()
                    + ", supportsSpeed(): " + lm.getProvider(listProviders.get(i)).supportsSpeed()
                    , tv);
        }
    }
}
