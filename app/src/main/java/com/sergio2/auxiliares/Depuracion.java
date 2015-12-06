package com.sergio2.auxiliares;

import android.widget.TextView;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by sergio2 on 12/11/2015.
 */
public class Depuracion {
    public static void traza(String mensaje, TextView tv) {
        String hora = DateFormat.getTimeInstance().format(new Date())+": ";
        String idDep = "DEBUG_SBS: ";
        System.out.println(hora + " " + idDep + mensaje);
        tv.append("\n" + hora+mensaje);
    }
    public static void traza(String mensaje) {
        String hora = DateFormat.getTimeInstance().format(new Date())+": ";
        String idDep = "DEBUG_SBS: ";
        System.out.println(hora + " " + idDep + mensaje);
    }
    public static void traza(String mensaje, TextView tv, TextView tv2) {
        String hora = DateFormat.getTimeInstance().format(new Date())+": ";
        String idDep = "DEBUG_SBS: ";
        System.out.println(hora + " " + idDep + mensaje);
        tv.append("\n" + hora + mensaje);
        tv2.append("\n" + hora + mensaje);
    }
}
