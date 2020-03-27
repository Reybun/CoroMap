package com.example.coromap.core;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Coordinates {
    @PrimaryKey(autoGenerate = true)
    public int idcoordinates;
    String latitude;
    String longitude;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
