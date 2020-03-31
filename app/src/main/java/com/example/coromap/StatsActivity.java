package com.example.coromap;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coromap.mapper.APIResponseM;
import com.example.coromap.mapper.CoordinatesM;
import com.example.coromap.mapper.LatestM;
import com.example.coromap.mapper.PaysM;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StatsActivity extends FragmentActivity {


    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<PaysM> datarep = new ArrayList<PaysM>();
    SQLiteDatabase db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        loadData();
    }
    public void displayStat(){
        if(datarep.size() > 0) {

            recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true);

            // use a linear layout manager
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            // specify an adapter (see also next example)
            mAdapter = new DataAdapter(datarep);
            recyclerView.setAdapter(mAdapter);
        }

    }

    public void loadData() {
        db = new MyOpenHelper(this).getWritableDatabase();
        Cursor c = db.rawQuery("SELECT pays.country, pays.countrycode, pays.province, coordinates.lat, coordinates.long, latestcases.confirmed, latestcases.death, latestcases.recovered, paysupdate.countrypop, paysupdate.lastupdated " +
                "FROM response " +
                "INNER JOIN paysupdate ON paysupdate.idresp = id_response " +
                "INNER JOIN pays ON pays.id_pays = paysupdate.idpays " +
                "INNER JOIN coordinates ON pays.idcoord = coordinates.id_coord " +
                "INNER JOIN latestcases ON paysupdate.idlatest = latestcases.id_latest " +
                "ORDER BY latestcases.death DESC, latestcases.confirmed DESC", null);

        if (c.moveToFirst()){
            PaysM pays;
            CoordinatesM coord;
            LatestM latest;
            do {
                pays = new PaysM();
                coord = new CoordinatesM();
                latest = new LatestM();

                //SET COORD
                coord.setLatitude(String.valueOf(c.getInt(3)));
                coord.setLongitude(String.valueOf(c.getInt(4)));
                pays.setCoordinates(coord);

                //SET LATEST
                latest.setConfirmed(c.getInt(5));
                latest.setDeaths(c.getInt(6));
                latest.setRecovered(c.getInt(7));
                pays.setLatest(latest);

                //SET ALL THE THINGS
                pays.setCountry(c.getString(0));
                pays.setCountry_code(c.getString(1));
                pays.setProvince(c.getString(2));
                pays.setLast_updated(c.getString(9));
                pays.setCountry_population(c.getInt(8));

                datarep.add(pays);
                Log.i("tPays", ""+c.getString(0) );
                Log.i("tPays", ""+c.getString(1) );
                Log.i("tPays", ""+c.getInt(3) );


            } while(c.moveToNext());
        }
        c.close();
        db.close();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayStat();
            }
        });
    }

}
