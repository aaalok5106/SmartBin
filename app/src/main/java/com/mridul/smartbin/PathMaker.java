package com.mridul.smartbin;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.YuvImage;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.mridul.smartbin.MakingPath.DirectionFinder;
import com.mridul.smartbin.MakingPath.DirectionFinderListener;
import com.mridul.smartbin.MakingPath.Route;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * This Class will be used to make path through required bins.
 */


public class PathMaker extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    private static int netDISTANCE = 0;
    private static int netTIME = 0;
    private static int checkPOINT = 0;

    private GoogleMap mMap;
    private Button btnFindPath;

    ArrayList<String> LATITUDE=new ArrayList<>();
    ArrayList<String> LONGITUDE=new ArrayList<>();
    ArrayList<String> BIN_ID=new ArrayList<>();


    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_maker);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        /*LATITUDE = savedInstanceState.getStringArrayList("arrayList_latitude");
        LONGITUDE = savedInstanceState.getStringArrayList("arrayList_longitude");
        BIN_ID = savedInstanceState.getStringArrayList("arrayList_bin_id");*/
        //String data = savedInstanceState.getString("jsonArray");

        Intent intent1 = getIntent();
        String data = intent1.getStringExtra("jsonArray");

        Log.d("In PathMaker", data);

        try {
            JSONArray ja =new JSONArray(data);
            JSONObject jo =null;

            LATITUDE.clear();
            LONGITUDE.clear();
            BIN_ID.clear();

            for(int i=0; i<ja.length(); i++ ){
                jo = ja.getJSONObject(i);

                String latitude = jo.getString("lat");
                String longitude = jo.getString("lng");
                String bin_id = jo.getString("bin_id");
                LATITUDE.add(latitude);
                LONGITUDE.add(longitude);
                BIN_ID.add(bin_id);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //Thread.sleep(5000);
                            sendRequest();
                        }catch (Exception e){
                            //...........
                        }

                    }
                }).start();

                //sendRequest();
            }
        });
    }

    private void sendRequest() {

        netDISTANCE = 0 ;
        netTIME = 0 ;
        checkPOINT = 0 ;


        //String[] checking = {"25.538794,84.850326","25.534607,84.853888","25.538944,84.858813","25.543116,84.862117", "25.547724,84.863919", "25.554132,84.869112"};
        int size = LATITUDE.size();
        //int i = 0;
        for(int i = 0; i < size-1 ; i++){
            String lat_s = LATITUDE.get(i).toString();
            String lng_s = LONGITUDE.get(i).toString();
            String lat_e = LATITUDE.get(i+1).toString();
            String lng_e = LONGITUDE.get(i+1).toString();

            String origin_bin_id = BIN_ID.get(i).toString();

            String origin = lat_s+","+lng_s ;
            String destination = lat_e+","+lng_e ;

            Log.d("Origin", origin);
            Log.d("Destination", destination);

            try {
                new DirectionFinder(this, origin, destination, origin_bin_id).execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public void onBackPressed() {
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/


        android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(this);
        alert.setTitle("Exit Alert!!");
        alert.setMessage("Sure to Exit this window?");
        alert.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
                // Do flush Job here...
            }
        });
        alert.setNegativeButton("Stay", null);
        alert.show();



    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng pos = new LatLng(25.536014,84.8488763);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 6));


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }


    @Override
    public void onDirectionFinderStart() {
        //progressDialog = ProgressDialog.show(this, "Please wait.",
        //      "Finding direction..!", true);

     /*   if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }*/
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes, int dist, int time, String origin_id_of_bin) {

        netDISTANCE += dist ;
        netTIME += time ;
        String displayDistance = ""+(netDISTANCE/1000)+"."+(netDISTANCE%1000)+" KM";
        String displayTime = ""+(netTIME/3600)+" hr "+((netTIME%3600)/60)+" min";



        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();


        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 9));
            /*((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);*/
            ((TextView) findViewById(R.id.tvDuration)).setText(displayTime);
            ((TextView) findViewById(R.id.tvDistance)).setText(displayDistance);

            if(origin_id_of_bin.trim().equals("BINSTART")){
                originMarkers.add(mMap.addMarker(new MarkerOptions()
                        .title("START POSITION")
                        .snippet("Your Start Position")
                        .position(route.startLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_pos_marker_small))));
            }else{
                checkPOINT += 1 ;
                originMarkers.add(mMap.addMarker(new MarkerOptions()
                        .title("CHECKPOINT "+checkPOINT)
                        .snippet("Bin id : "+origin_id_of_bin)
                        .position(route.startLocation)));
            }

            /*originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.endAddress)
                    .position(route.endLocation)));*/

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }


    }
}

