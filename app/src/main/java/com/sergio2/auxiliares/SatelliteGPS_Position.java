package com.sergio2.auxiliares;

import java.text.DecimalFormat;

/**
 * Created by sergio2 on 13/02/2016.
 */
public class SatelliteGPS_Position {
    public static DecimalFormat format_coord = new DecimalFormat("000.0");
    public static DecimalFormat format_PRN = new DecimalFormat("00");
    public static DecimalFormat format_SNR = new DecimalFormat("00.0");
    public static DecimalFormat format_Azi = new DecimalFormat("000.0");
    public static DecimalFormat format_Ele = new DecimalFormat("00.0");


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public SatelliteGPS_Position() {
        this.latitude = 0;
        this.longitude = 0;
        this.elevation = -1;
        this.azimut = -1;
        this.PRN = -1;
        this.SNR = -1;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getPRN() {
        return PRN;
    }

    public void setPRN(int PRN) {
        this.PRN = PRN;
    }

    public float getSNR() {
        return SNR;
    }

    public void setSNR(float SNR) {
        this.SNR = SNR;
    }


    public float getElevation() {
        return elevation;
    }

    public long getRegisteredPositions() {
        return registeredPositions;
    }

    public void setRegisteredPositions(long registeredPositions) {
        this.registeredPositions = registeredPositions;
    }

    public void setElevation(float elevation) {
                this.elevation = elevation;
    }

    public float getAzimut() {
        return azimut;
    }

    public void setAzimut(float azimut) {
        this.azimut = azimut;
    }

    private double latitude = 0;
    private double longitude = 0;
    private float elevation = -1;
    private float azimut = -1;
    private int PRN = -1;
    private float SNR = -1;
    long registeredPositions = 0;
}
