package com.sim.coromap.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.sim.coromap.core.APIResponse;

@Dao
public interface InformationsDao {
    @Query("SELECT * FROM APIResponse ")
    APIResponse getAll();

    @Insert
    public void insertAPIResponse(APIResponse ... apiResponses);

}