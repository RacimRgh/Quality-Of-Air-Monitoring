package com.example.QualityOfAirMonitoring.accounts_creation;

public class Weather {
    private double lat;
    private double lon;
    private String date;
    private Float hmd;
    private Float tmp;

    public Weather(double lat, double lon, String date, Float hmd, Float tmp) {
        this.lat = lat;
        this.lon = lon;
        this.date = date;
        this.hmd = hmd;
        this.tmp = tmp;
    }


    public Float getTmp() {
        return tmp;
    }

    public void setTmp(Float tmp) {
        this.tmp = tmp;
    }

    public Float getHmd() {
        return hmd;
    }

    public void setHmd(Float hmd) {
        this.hmd = hmd;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}