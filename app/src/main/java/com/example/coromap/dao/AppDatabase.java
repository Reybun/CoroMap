package com.example.coromap.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.coromap.core.APIResponse;
import com.example.coromap.core.Coordinates;
import com.example.coromap.core.Latest;
import com.example.coromap.core.Locations;
import com.example.coromap.core.Pays;

@Database(entities = {APIResponse.class,  Latest.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract InformationsDao infoDao();
}