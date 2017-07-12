package br.ufrn.imd.vdc.domain;

import java.io.Serializable;

/**
 * Created by Cephas on 27/03/2017.
 */

public class ObdReading implements Serializable {

    private int id;
    private String vehicleId;

    private long timestamp;
    private double latitude;
    private double longitude;
    private double altitude;

    private String readings;

    public ObdReading() {

    }

    public ObdReading(double latitude, double longitude, double altitude, long timestamp, String
        vehicleid, String readings) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timestamp = timestamp;
        this.vehicleId = vehicleid;
        this.readings = readings;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getReadings() {
        return readings;
    }

    public void setReadings(String readings) {
        this.readings = readings;
    }

    public String toString() {

        return "lat:" + latitude + ";" + "long:" + longitude + ";" + "alt:" + altitude + ";" +
               "vehicleid:" + vehicleId + ";" + "readings: " + readings;
    }
}

