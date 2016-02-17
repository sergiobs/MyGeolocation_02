package com.sergio2.auxiliares;

import android.os.Environment;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

/**
 * Created by sergio2 on 14/02/2016.
 */
public class FileRegister {
    String Aplicacion = "MyGEO";
    File fileDep;
    File fullPath;
    boolean sdDisponible = false;
    boolean sdAccesoEscritura = false;
    static int PROVIDER_GPS = 1;
    static int PROVIDER_NETWORK = 2;
    static int PROVIDER_PASSIVE = 3;


    String estado;

    public FileRegister() {
        //Comprobamos el estado de la memoria externa (tarjeta SD)

        File fullPath_SAT;
        estado = Environment.getExternalStorageState();
        if (estado.equals(Environment.MEDIA_MOUNTED))
        {
            sdDisponible = true;
            sdAccesoEscritura = true;
        }
        else if (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
        {
            sdDisponible = true;
            sdAccesoEscritura = false;
        }
        else
        {
            sdDisponible = false;
            sdAccesoEscritura = false;
        }

        //todo: esto hay que cambarlo de ruta para que sea mas accesible... ahora es /storage/emulated/0/
        File tarjeta = Environment.getExternalStorageDirectory();
        fullPath 	= new File(tarjeta.getAbsolutePath()+'/'+Aplicacion+'/');


        if(!fullPath.exists()&&sdDisponible&&sdAccesoEscritura)
            fullPath.mkdirs();


        fileDep = calculaFicheroDEP();
        Depuracion.traza("Status of external storage: " + estado);
        Depuracion.traza("fileDep: " + fileDep);

    }

    public void registrarDEP(String cadena) {
        Date dateAhora = new Date();
//        String string2Save =  String_cero_Dec(1+dateAhora.getMonth())+"/"+
//                String_cero_Dec(dateAhora.getDate())+"-"+
//                String_cero_Dec(dateAhora.getHours())+":"+
//                String_cero_Dec(dateAhora.getMinutes())+"."+
//                String_cero_Dec(dateAhora.getSeconds()) + cadena;

        String string2Save = cadena;
        grabar(string2Save, fileDep, true);
    }

    public void registrarSAT(SatelliteGPS_Position satelliteGPS_Position) {
        File fullPath_SAT 	= new File(fullPath.getPath()+"/TrackSAT/"+SatelliteGPS_Position.format_PRN.format(satelliteGPS_Position.getPRN()));
        if(!fullPath_SAT.exists()&&sdDisponible&&sdAccesoEscritura)
            fullPath_SAT.mkdirs();

        String fileName= calculaNombreFicheroSAT(satelliteGPS_Position.getPRN());
        Date dateAhora = new Date();

        String string2Save =  String_cero_Dec(1+dateAhora.getMonth())+"/"+
                        String_cero_Dec(dateAhora.getDate())+"-"+
                        String_cero_Dec(dateAhora.getHours())+":"+
                        String_cero_Dec(dateAhora.getMinutes())+"."+
                        String_cero_Dec(dateAhora.getSeconds());

        string2Save += ":   " +
                SatelliteGPS_Position.format_PRN.format(satelliteGPS_Position.getPRN())+ ", " +
                SatelliteGPS_Position.format_SNR.format(satelliteGPS_Position.getSNR())+ ", " +
                SatelliteGPS_Position.format_Azi.format(satelliteGPS_Position.getAzimut())+ ", " +
                SatelliteGPS_Position.format_Ele.format(satelliteGPS_Position.getElevation())+ ", " +
                SatelliteGPS_Position.format_coord.format(satelliteGPS_Position.getLatitude())+ ", " +
                SatelliteGPS_Position.format_coord.format(satelliteGPS_Position.getLongitude());

        File file = new File(fullPath_SAT, fileName);

        if (file.exists()) {
        }
        else {
            grabar("TimeStamp:     PRN,  SRN,  Azim, Elev,  Latit, Longit", file, false);
        }
        grabar(string2Save, file, true);
    }

    //public void registrarOBS(LatLng coord, int providerType, int speed, int precision) {
    public void registrarOBS(String cadena) {
        File fullPath_OBS 	= new File(fullPath.getPath()+"/TrackOBS/");
        if(!fullPath_OBS.exists()&&sdDisponible&&sdAccesoEscritura)
            fullPath_OBS.mkdirs();

        String fileName= calculaNombreFicheroOBS();
        Date dateAhora = new Date();

        String string2Save =  String_cero_Dec(1+dateAhora.getMonth())+"/"+
                String_cero_Dec(dateAhora.getDate())+"-"+
                String_cero_Dec(dateAhora.getHours())+":"+
                String_cero_Dec(dateAhora.getMinutes())+"."+
                String_cero_Dec(dateAhora.getSeconds()) + cadena;

//        string2Save += ":   " +
//                "("+providerType + "), " + coord.latitude + ", " + coord.longitude + ", " + speed + ", " + precision;

        File file = new File(fullPath_OBS, fileName);

        if (file.exists()) {
        }
        else {
            grabar("TimeStamp:     (Prov),  Latit,    Long,       Speed,   Precision ", file, false);
        }

        grabar(string2Save, file, true);
    }

    public String calculaNombreFicheroSAT(int nSat) {
        // FileName(i.e.): MyGEO_SAT_01_20160214-16.txt
        String fileName=Aplicacion+"_SAT_"+String_cero_Dec(nSat)+"_";
        // TODO: intentar quitar lo deprecado usando algo similar a Depuracion.traza:
        // TODO: String hora = DateFormat.getTimeInstance().format(new Date())+": ";

        Date dateAhora = new Date();
        fileName +=
                "20"+(Integer.toString(dateAhora.getYear())).substring(1,3)+
                        String_cero_Dec(1+dateAhora.getMonth())+
                        String_cero_Dec(dateAhora.getDate())+"-"+
                        String_cero_Dec(dateAhora.getHours())+
                        //String_cero_Dec(dateAhora.getMinutes())+
                        //String_cero_Dec(dateAhora.getSeconds())+
                        ".txt";
        return fileName;
    }

    public String calculaNombreFicheroOBS() {
        // FileName(i.e.): MyGEO_OBS_20160214-16.txt
        String fileName=Aplicacion+"_SAT_";
        // TODO: intentar quitar lo deprecado usando algo similar a Depuracion.traza:
        // TODO: String hora = DateFormat.getTimeInstance().format(new Date())+": ";

        Date dateAhora = new Date();
        fileName +=
                "20"+(Integer.toString(dateAhora.getYear())).substring(1,3)+
                        String_cero_Dec(1+dateAhora.getMonth())+
                        String_cero_Dec(dateAhora.getDate())+"-"+
                        String_cero_Dec(dateAhora.getHours())+
                        //String_cero_Dec(dateAhora.getMinutes())+
                        //String_cero_Dec(dateAhora.getSeconds())+
                        ".txt";
        return fileName;
    }

    File calculaFicheroDEP() {
        // FileName(i.e.): MyGEO_DEP_20160214_nSesion.txt  donde nSesion es un numeral creciente cada vez que arranca la app que se calculara despues
        int nSesion = 0;
        boolean existeFile = false;
        String fileName=Aplicacion+"_DEP_";
        // TODO: intentar quitar lo deprecado usando algo similar a Depuracion.traza:
        // TODO: String hora = DateFormat.getTimeInstance().format(new Date())+": ";

        Date dateAhora = new Date();
        fileName +=
                "20"+(Integer.toString(dateAhora.getYear())).substring(1,3)+
                        String_cero_Dec(1+dateAhora.getMonth())+
                        String_cero_Dec(dateAhora.getDate())+"_"
                        //String_cero_Dec(dateAhora.getMinutes())+
                        //String_cero_Dec(dateAhora.getSeconds())+
                        ;

        while (!existeFile) {
            File file = new File(fullPath, fileName+String_cero_Dec(nSesion)+".txt");
            if (!file.exists())   {
                existeFile = true;
                Depuracion.traza("Fichero dep: " + file);
                grabar("Creado fichero depuracion", file, false);
                return file;
            }
            nSesion ++ ;

        }
        Depuracion.traza("Fichero dep: null");
        return null;
    }


    public void grabar(String texto2file, File file, boolean existeFile) {
        if(sdDisponible&&sdAccesoEscritura) {
            try
            {
                OutputStreamWriter osw =new OutputStreamWriter(new FileOutputStream(file, true));
                if (existeFile) {
                    osw.append(texto2file + "\r\n");
                }
                if (!existeFile) {
                    osw.write(texto2file + "\r\n");
                }
                osw.flush();
                osw.close();
            }
            catch (IOException ioe) {   }
        }
    }

    String String_cero_Dec (int in) {
        String String_cero="";
        if (in < 10) {
            String_cero = '0'+(Integer.toString(in));
        } else {
            String_cero = (Integer.toString(in));
        }

        return String_cero;
    }
}
