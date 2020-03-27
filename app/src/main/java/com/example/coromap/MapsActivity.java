package com.example.coromap;

import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coromap.core.APIResponse;
import com.example.coromap.dao.AppDatabase;
import com.example.coromap.mapper.APIResponseM;
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

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {

    private GoogleMap mMap;
    AppDatabase db;
    APIResponseM resp;
    boolean mapready = false;
    final ArrayList<Marker> markerList = new ArrayList<Marker>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "CoroMapDB").build();

        getCorinfo();
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

        displayMap();

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(46.2, 2.2);
        // Instantiates a new CircleOptions object and defines the center and radius
        /*CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(46.2, 2.2))
                .radius(1000000) // In meters
                .fillColor(Color.argb(100, 255, 0, 0))
                .strokeWidth(0);

        // Get back the mutable Circle
        Circle circle = mMap.addCircle(circleOptions);
        */
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    public void displayMap() {
        final Map<String, Integer> mapCM = new HashMap<>();



        

        if(resp != null && mapready) {
            int size = resp.getLocations().size() - 1;

            for (int x = 0; x < size; x++) {
                //Log.i("radiuscheck" , resp.getLocations().get(x).getLatest().getConfirmed()+"" );
                double radius = Math.sqrt(resp.getLocations().get(x).getLatest().getDeaths() + resp.getLocations().get(x).getLatest().getConfirmed()) * 1000 + 50000;
                if(radius > 225000) radius = 225000;
                CircleOptions circleOptions = new CircleOptions()
                        .center(new LatLng(Double.parseDouble(resp.getLocations().get(x).getCoordinates().getLatitude()), Double.parseDouble(resp.getLocations().get(x).getCoordinates().getLongitude())))
                        .radius(radius ) // In meters
                        .fillColor(Color.argb(100, 255, 0, 0))
                        .clickable(true)
                        .zIndex(2)
                        .strokeWidth(0);

                // Get back the mutable Circle
                Circle circle = mMap.addCircle(circleOptions);

                /*final Marker marker = mMap.addMarker(
                        new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(resp.getLocations().get(x).getCoordinates().getLatitude()), Double.parseDouble(resp.getLocations().get(x).getCoordinates().getLongitude())))
                            .visible(false)
                            .alpha(0)
                            .title(resp.getLocations().get(x).getCountry())
                            .snippet("Mort : "+resp.getLocations().get(x).getLatest().getDeaths())
                );*/

                String idcercle = circle.getId();
                mapCM.put(idcercle, x); // map >> X
            }


            mMap.setOnCircleClickListener(
                    new GoogleMap.OnCircleClickListener() {
                        @Override
                        public void onCircleClick(Circle circle) {

                            int x = mapCM.get(circle.getId());
                            String title = resp.getLocations().get(x).getProvince() != "" ? resp.getLocations().get(x).getCountry() + " - " + resp.getLocations().get(x).getProvince() : resp.getLocations().get(x).getCountry();
                            Marker displaymarker = mMap.addMarker(
                                    new MarkerOptions()
                                            .position(new LatLng(Double.parseDouble(resp.getLocations().get(x).getCoordinates().getLatitude()), Double.parseDouble(resp.getLocations().get(x).getCoordinates().getLongitude())))
                                            .visible(true)
                                            .alpha(0)
                                            .zIndex(-1)
                                            .title(title)
                                            .infoWindowAnchor(0.5f,1f)

                                            .snippet("Confirmed : "+resp.getLocations().get(x).getLatest().getConfirmed() + System.getProperty("line.separator") +
                                                    "Mort : "+resp.getLocations().get(x).getLatest().getDeaths() + System.getProperty("line.separator"))

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

    public void getCorinfo() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://coronavirus-tracker-api.herokuapp.com/v2/locations")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(MapsActivity.this, "Erreur réseau :(", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                ObjectReader reader = mapper.reader().forType(APIResponseM.class);
                resp = reader.readValue(response.body().string()); // Creer lobjet
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayMap();
                    }
                });

                Log.i("coro89", ""+resp.getLocations().get(237).getCoordinates().getLatitude());
                Log.i("coro89", ""+resp.getLocations().get(236).getCoordinates().getLatitude());
                //mapper.readTree(response.body().string()).get("departures").get(2).get());
                //db.infoDao().insertAPIResponse(resp);
                //db.displayDao().insertDisplayInformations(resp.getDepartures().get(2).getDisplayInformations());
                //Log.i("dbtest", db.infoDao().getAll() +"");
            }
        });
    }

    @Override
    public View getInfoWindow(Marker marker) {
        //return null;
        return prepareInfoView(marker);
    }

    @Override
    public View getInfoContents(Marker marker) {
        //return null;
        return prepareInfoView(marker);
    }

    private View prepareInfoView(Marker marker){
        //prepare InfoView programmatically
/* infoView = new LinearLayout(MapsActivity.this);

        LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        infoView.setOrientation(LinearLayout.HORIZONTAL);

        infoView.setLayoutParams(infoViewParams);

        ImageView infoImageView = new ImageView(MapsActivity.this);
        //Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
        Drawable drawable = getResources().getDrawable(android.R.drawable.ic_dialog_map);
        infoImageView.setImageDrawable(drawable);
        infoView.addView(infoImageView);

        LinearLayout subInfoView = new LinearLayout(MapsActivity.this);
        LinearLayout.LayoutParams subInfoViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subInfoView.setOrientation(LinearLayout.VERTICAL);
        subInfoView.setLayoutParams(subInfoViewParams);

        TextView subInfoLat = new TextView(MapsActivity.this);
        subInfoLat.setText(marker.getTitle());
        TextView subInfoLnt = new TextView(MapsActivity.this);
        subInfoLnt.setText("Lnt: " + marker.getPosition().longitude);
        subInfoView.addView(subInfoLat);
        subInfoView.addView(subInfoLnt);
        infoView.addView(subInfoView);



        return infoView;


 */
        View myContentsView = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
        tvTitle.setText(marker.getTitle());
        TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.snippet));
        tvSnippet.setText(marker.getSnippet());

        return myContentsView;


        //return getLayoutInflater().inflate(R.layout.custom_info_contents, null);

    }
}
