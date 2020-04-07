package com.sim.coromap.core;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Latest {
    @PrimaryKey(autoGenerate = true)
    public int idlatest;

    int confirmed;
    int deaths;
    int recovered;

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getRecovered() {
        return recovered;
    }

    public void setRecovered(int recovered) {
        this.recovered = recovered;
    }
}
