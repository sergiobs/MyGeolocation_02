package com.sergio2.auxiliares;

import android.widget.TextView;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by sergio2 on 12/11/2015.
 */
public class Depuracion {
    static  String idDep = "DEBUG_SBS: ";
    public static void traza(String mensaje, TextView tv) {
        String hora = DateFormat.getTimeInstance().format(new Date())+": ";
        System.out.println(hora + " " + idDep + mensaje);
        tv.append("\n" + hora + mensaje);
    }
    public static void traza(String mensaje) {
        String hora = DateFormat.getTimeInstance().format(new Date())+": ";
        System.out.println(hora + " " + idDep + mensaje);
    }
    public static void traza(String mensaje, TextView tv, FileRegister fr) {
        String hora = DateFormat.getTimeInstance().format(new Date())+": ";
        System.out.println(hora + " " + idDep + mensaje);
        tv.append("\n" + hora + mensaje);
        String cadena = hora + "  " + mensaje;
        fr.registrarDEP(cadena);
    }

    public static void traza(String mensaje, FileRegister fr) {
        String hora = DateFormat.getTimeInstance().format(new Date())+": ";
        String cadena = hora + "  " + mensaje;
        fr.registrarDEP(cadena);
    }



}
