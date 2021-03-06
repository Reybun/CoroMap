package com.sim.coromap;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sim.coromap.mapper.APIResponseM;
import com.sim.coromap.mapper.CoordinatesM;
import com.sim.coromap.mapper.LatestM;
import com.sim.coromap.mapper.PaysM;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {

    private GoogleMap mMap;
    SQLiteDatabase db;
    APIResponseM resp;
    String act;
    ArrayList<PaysM> datarep = new ArrayList<PaysM>();
    boolean mapready = false;
    final ArrayList<Marker> markerList = new ArrayList<Marker>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        db = new MyOpenHelper(this).getWritableDatabase();


        String hour = "";
        Cursor c = db.rawQuery("SELECT datehour FROM response ", null);
        if (c.moveToLast()) { hour = c.getString(0); }

        if(hour == "") {
            act = "corinfo"; //getCorinfo();
        } else if (ChronoUnit.HOURS.between(LocalDateTime.parse(hour), LocalDateTime.now()) > 1 ) {
            Log.i("testselect", "VICTORY");
            act = "corinfo"; //getCorinfo();
        } else {
            act = "load"; //loadData(); // recup info de bdd
        }
        c.close();
        db.close();

        FloatingActionButton floatingActionButton = findViewById(R.id.button1);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("floating", "works");
                Intent intent = new Intent(MapsActivity.this , StatsActivity.class );
                startActivity(intent);
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapready = true;
        mMap = googleMap;
        mMap.setInfoWindowAdapter(this);
        mMap.setMinZoomPreference(3.3f);
        mMap.setMaxZoomPreference(5.5f);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(3.55f));

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));
            if (!success) {
                Log.e("style", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("style", "Can't find style. Error: ", e);
        }
        if(act == "load") { loadData(); } else { getCorinfo(); }

    }


    public void displayMap() {
        final Map<String, Integer> mapCM = new HashMap<>();
        if(datarep.size() != 0 && mapready) {
            int size = datarep.size();
            for (int x = 0; x < size; x++) {

                //Log.i("radiuscheck" , resp.getLocations().get(x).getLatest().getConfirmed()+"" );
                double radius = Math.sqrt(datarep.get(x).getLatest().getDeaths() + datarep.get(x).getLatest().getConfirmed()) * 1000 + 50000;
                if(radius > 225000) radius = 225000;
                CircleOptions circleOptions = new CircleOptions()
                        .center(new LatLng(Double.parseDouble(datarep.get(x).getCoordinates().getLatitude()), Double.parseDouble(datarep.get(x).getCoordinates().getLongitude())))
                        .radius(radius ) // In meters
                        .fillColor(Color.argb(100, 255, 0, 0))
                        .clickable(true)
                        .zIndex(2)
                        .strokeWidth(0);

                // Get back the mutable Circle
                Circle circle = mMap.addCircle(circleOptions);

                String idcercle = circle.getId();
                mapCM.put(idcercle, x); // map >> X
            }

            mMap.setOnCircleClickListener(
                    new GoogleMap.OnCircleClickListener() {
                        @Override
                        public void onCircleClick(Circle circle) {

                            int x = mapCM.get(circle.getId());
                            String title = !datarep.get(x).getProvince().isEmpty() ? datarep.get(x).getCountry() + " - " + datarep.get(x).getProvince() : datarep.get(x).getCountry();

                            Marker displaymarker = mMap.addMarker(
                                    new MarkerOptions()
                                            .position(new LatLng(Double.parseDouble(datarep.get(x).getCoordinates().getLatitude()), Double.parseDouble(datarep.get(x).getCoordinates().getLongitude())))
                                            .visible(true)
                                            .alpha(0)
                                            .zIndex(-1)
                                            .title(title)
                                            .infoWindowAnchor(0.5f,1f)

                                            .snippet("Infectés : "+datarep.get(x).getLatest().getConfirmed() + System.getProperty("line.separator") +
                                                    "Mort : "+datarep.get(x).getLatest().getDeaths() + System.getProperty("line.separator"))

                            );
                            markerList.add(displaymarker);
                            
                            Log.i("testcircle", displaymarker.getTitle());
                            //displaymark.setVisible(true);
                            displaymarker.showInfoWindow();
                        }
                    }
            );

            mMap.setOnMapClickListener(
                    new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            if(markerList != null) {
                                for (Marker marker: markerList) {
                                    marker.remove();
                                }
                                markerList.clear();
                            }
                        }
                    }
            );

            mMap.setOnMarkerClickListener(
                    new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            Log.i("clickmark", "df");
                            return false;
                        }
                    }
            );
        }
    }

    public void loadData() {
        db = new MyOpenHelper(this).getWritableDatabase();
        Cursor d = db.rawQuery("SELECT id_pays FROM pays WHERE country = ?",  new String[] {"Malawi"});

        if (d.moveToFirst()){
            do {
                Log.i("testdatat", ""+d.getString(0));
            } while(d.moveToNext());
        }

        Cursor c = db.rawQuery("SELECT pays.country, pays.countrycode, pays.province, coordinates.lat, coordinates.long, latestcases.confirmed, latestcases.death, latestcases.recovered, paysupdate.countrypop, paysupdate.lastupdated " +
                "FROM response " +
                "INNER JOIN paysupdate ON paysupdate.idresp = id_response " +
                "INNER JOIN pays ON pays.id_pays = paysupdate.idpays " +
                "INNER JOIN coordinates ON pays.idcoord = coordinates.id_coord " +
                "INNER JOIN latestcases ON paysupdate.idlatest = latestcases.id_latest",  null);

        if (c.moveToFirst()){

            PaysM pays;
            CoordinatesM coord;
            LatestM latest;
            do {
                Log.i("testdata", ""+c.getString(0));
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
                displayMap();
            }
        });
    }

    public void getCorinfo() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://coronavirus-tracker-api.herokuapp.com/v2/locations")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = inflater.inflate(R.layout.popuperror, null);

                        // create the popup window
                        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        boolean focusable = false; // lets taps outside the popup also dismiss it
                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                        popupWindow.showAtLocation( MapsActivity.this.findViewById(R.id.map), Gravity.CENTER, 0, 0);

                        popupView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                popupWindow.dismiss();
                                return true;
                            }
                        });



                    }
                });


            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                ObjectReader reader = mapper.reader().forType(APIResponseM.class);
                resp = reader.readValue(response.body().string()); // Creer lobjet

                db = new MyOpenHelper(MapsActivity.this).getWritableDatabase();
                ContentValues content;

                //latestall
                content = new ContentValues();
                content.put("confirmed" , resp.getLatest().getConfirmed());
                content.put("death" , resp.getLatest().getDeaths());
                content.put("recovered" , resp.getLatest().getRecovered());
                long idlatestall = db.insert("latestcases", null, content);

                Cursor c = db.rawQuery("SELECT * FROM latestcases", null);
                if (c.moveToLast()) {
                    Log.i("testselect", "1 ||"+ c.getString(0)); // id
                    Log.i("testselect", "2 ||"+ c.getString(1)); // confirmer
                    Log.i("testselect", "3 ||"+ c.getString(2)); // death
                }

                //response
                content = new ContentValues();
                content.put("datehour" , LocalDateTime.now().toString());
                content.put("idlatestall" , Math.toIntExact(idlatestall));
                long idresp = db.insert("response", null, content);

                int size = resp.getLocations().size() ;
                long idpays;

                for (int x = 0; x < size ; x++) {

                    Cursor cursor;
                    cursor = db.rawQuery("SELECT id_pays FROM pays WHERE id = ?", new String[] {String.valueOf(resp.getLocations().get(x).getId())});
                    //Log.i("testselect", "NA "+ resp.getLocations().get(x).getCountry()); // id
                    if (cursor.moveToLast()) { //means the entry exist as a pays then no need to duplicate it
                        //Log.i("testselect", "NA "+ cursor.getString(0)); // id
                        idpays = Long.parseLong(c.getString(0));
                    } else { // create the data for the country

                        //coordinates
                        content = new ContentValues();
                        content.put("long" , resp.getLocations().get(x).getCoordinates().getLongitude());
                        content.put("lat" , resp.getLocations().get(x).getCoordinates().getLatitude());
                        long idcoord = db.insert("coordinates", null, content);

                        //pays
                        content = new ContentValues();
                        content.put("country" , resp.getLocations().get(x).getCountry());
                        content.put("countrycode" , resp.getLocations().get(x).getCountry_code());
                        content.put("province", resp.getLocations().get(x).getProvince());
                        content.put("id", resp.getLocations().get(x).getId());
                        content.put("idcoord", idcoord);
                        idpays = db.insert("pays", null, content);
                    }

                    //latestcountry
                    content = new ContentValues();
                    content.put("confirmed" , resp.getLocations().get(x).getLatest().getConfirmed());
                    content.put("death" , resp.getLocations().get(x).getLatest().getDeaths());
                    content.put("recovered" , resp.getLocations().get(x).getLatest().getRecovered());
                    long idlatestcountry = db.insert("latestcases", null, content);

                    //Log.i("testselect2", "" + x + resp.getLocations().get(x).getCountry()); // id
                    //paysupdate
                    content = new ContentValues();
                    content.put("countrypop" , resp.getLocations().get(x).getCountry_population());
                    content.put("lastupdated" , resp.getLocations().get(x).getLast_updated());
                    content.put("idresp", idresp);
                    content.put("idlatest", idlatestcountry);
                    content.put("idpays", idpays);
                    long idpaysupdt = db.insert("paysupdate", null, content);

                    cursor.close();
                }

                db.close();
                loadData();
            }
        });
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return prepareInfoView(marker);
    }

    @Override
    public View getInfoContents(Marker marker) {
        //return null;
        return prepareInfoView(marker);
    }

    private View prepareInfoView(Marker marker){

        View myContentsView = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
        tvTitle.setText(marker.getTitle());
        TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.snippet));
        tvSnippet.setText(marker.getSnippet());

        return myContentsView;

    }
}
