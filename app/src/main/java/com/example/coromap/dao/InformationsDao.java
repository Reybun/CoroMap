package com.example.coromap.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.coromap.core.APIResponse;
import com.example.coromap.core.Latest;

@Dao
public interface InformationsDao {
    @Query("SELECT * FROM APIResponse ")
    APIResponse getAll();

    @Insert
    public void insertAPIResponse(APIResponse ... apiResponses);

}