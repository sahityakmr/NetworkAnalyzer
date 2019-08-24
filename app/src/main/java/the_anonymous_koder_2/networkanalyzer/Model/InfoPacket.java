package the_anonymous_koder_2.networkanalyzer.Model;

import android.location.Geocoder;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sahitya on 3/21/2018.
 */

public class InfoPacket {
    private String date_time;
    private double latitude;
    private double longitude;
    private String area;
    private String operator;
    private int strength;
    private int level;

    public InfoPacket() {
    }

    public InfoPacket(String date_time, double latitude, double longitude, String area, String operator, int strength, int level) {
        this.date_time = date_time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.area = area;
        this.operator = operator;
        this.strength = strength;
        this.level = level;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
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

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}