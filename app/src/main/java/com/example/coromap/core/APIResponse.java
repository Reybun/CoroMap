package com.example.coromap.core;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.List;

@Entity
public class APIResponse {
    @PrimaryKey(autoGenerate = true)
    public int idresponse;

    public int latestId;


    @Embedded
    @Ignore
    List<Pays> locations;

    public List<Pays> getLocations() {
        return locations;
    }

    public void setLocations(List<Pays> locations) {
        this.locations = locations;
    }

    public int getLatestId() {
        return latestId;
    }

    public void setLatestId(int latestId) {
        this.latestId = latestId;
    }
}
