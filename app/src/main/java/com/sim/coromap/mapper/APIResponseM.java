package com.sim.coromap.mapper;

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
