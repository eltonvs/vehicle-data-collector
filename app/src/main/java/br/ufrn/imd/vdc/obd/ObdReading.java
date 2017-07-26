package br.ufrn.imd.vdc.obd;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class ObdReading implements Serializable {
    private long id;
    private long timestamp;

    private String vehicleId;
    private double latitude;
    private double longitude;
    private double altitude;
    private HashMap<String, String> readings;

    public ObdReading() {
        readings = new HashMap<>();
    }

    public ObdReading(long id, long timestamp, String vehicleId, double latitude, double
        longitude, double altitude, Map<String, String> readings) {
        this.id = id;
        this.timestamp = timestamp;
        this.vehicleId = vehicleId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.readings = (HashMap<String, String>) readings;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public Map<String, String> getReadings() {
        return readings;
    }

    public void setReadings(Map<String, String> readings) {
        this.readings = (HashMap<String, String>) readings;
    }

    public void addReading(String sensor, String value) {
        this.readings.put(sensor, value);
    }
}
