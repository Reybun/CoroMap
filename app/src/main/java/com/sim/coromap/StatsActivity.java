package com.sim.coromap;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sim.coromap.mapper.CoordinatesM;
import com.sim.coromap.mapper.LatestM;
import com.sim.coromap.mapper.PaysM;

import java.util.ArrayList;

public class StatsActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    //private RecyclerView.Adapter mAdapter;
    private DataAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<PaysM> datarep = new ArrayList<PaysM>();
    SQLiteDatabase db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        db = new MyOpenHelper(this).getWritableDatabase();


        Cursor c = db.rawQuery("SELECT idlatestall FROM response", null);

        if (c.moveToLast()){

            Cursor lat = db.rawQuery("SELECT confirmed, death, recovered FROM latestcases WHERE id_latest = ?",  new String[] {String.valueOf(c.getInt(0))});
            if(lat.moveToLast()) {
                TextView totdeath = findViewById(R.id.morttotal);
                totdeath.setText(String.valueOf(lat.getInt(1))+" MORTS");
                TextView totinf = findViewById(R.id.infectedtotal);
                totinf.setText(String.valueOf(lat.getInt(0))+" INFECTES");

            }

        }


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

}
