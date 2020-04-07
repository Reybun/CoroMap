package com.sim.coromap.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.sim.coromap.core.APIResponse;
import com.sim.coromap.core.Latest;

@Database(entities = {APIResponse.class,  Latest.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract InformationsDao infoDao();
}