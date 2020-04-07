package com.sim.coromap.mapper;


public class PaysM {

    CoordinatesM coordinates;
    String country;
    String country_code;
    int country_population;
    int id;
    String last_updated;
    LatestM latest;

    String province;

    public int getCountry_population() {
        return country_population;
    }

    public void setCountry_population(int country_population) {
        this.country_population = country_population;
    }

    public LatestM getLatest() {
        return latest;
    }

    public void setLatest(LatestM latest) {
        this.latest = latest;
    }

    public CoordinatesM getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(CoordinatesM coordinates) {
        this.coordinates = coordinates;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
