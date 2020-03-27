package com.example.coromap.core;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

public class Locations {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @Embedded
    List<Pays> Pays;

    public List<Pays> getPays() {
        return Pays;
    }

    public void setPays(List<Pays> pays) {
        Pays = pays;
    }
}
