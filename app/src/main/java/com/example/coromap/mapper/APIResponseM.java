package com.example.coromap.mapper;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import com.example.coromap.core.Latest;
import com.example.coromap.core.Pays;

import java.util.List;

public class APIResponseM {

    LatestM latest;

    List<PaysM> locations;

    public LatestM getLatest() {
        return latest;
    }

    public void setLatest(LatestM latest) {
        this.latest = latest;
    }

    public List<PaysM> getLocations() {
        return locations;
    }

    public void setLocations(List<PaysM> locations) {
        this.locations = locations;
    }
}
