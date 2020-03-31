package com.example.coromap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "coromap1.db";

    public MyOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE latestcases (id_latest INTEGER PRIMARY KEY, confirmed INTEGER, death INTEGER, recovered INTEGER)");
        db.execSQL("CREATE TABLE coordinates (id_coord INTEGER PRIMARY KEY, lat INTEGER, long INTEGER)");
        db.execSQL("CREATE TABLE response (id_response INTEGER PRIMARY KEY, datehour TEXT, idlatestall INTEGER, FOREIGN KEY(idlatestall) REFERENCES latestcases(id_latest))");
        db.execSQL("CREATE TABLE pays (id_pays INTEGER PRIMARY KEY, id INTEGER, country TEXT, countrycode TEXT, province TEXT, idcoord INTEGER, FOREIGN KEY(idcoord) REFERENCES coordinates(id_coord))");
        db.execSQL("CREATE TABLE paysupdate (id_paysupd INTEGER PRIMARY KEY, lastupdated TEXT, countrypop INTEGER, idpays INTEGER, idlatest INTEGER, idresp INTEGER, FOREIGN KEY(idlatest) REFERENCES latestcases(id_latest),  FOREIGN KEY(idpays) REFERENCES pays(id_pays),  FOREIGN KEY(idresp) REFERENCES response(id_response))");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
