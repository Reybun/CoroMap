package com.sim.coromap.core;

import androidx.room.Embedded;
import androidx.room.Relation;

public class APIResponseAndLatest {
    @Embedded
    public Latest latest;
    @Relation(
            parentColumn = "idlatest",
            entityColumn = "latestId"
    )
    public APIResponse apiResponse;
}
