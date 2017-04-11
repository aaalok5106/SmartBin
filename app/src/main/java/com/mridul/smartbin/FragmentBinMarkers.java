package com.mridul.smartbin;



import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.PrivateKey;
import java.util.ArrayList;


public class FragmentBinMarkers extends Fragment implements OnMapReadyCallback{

    GoogleMap mGoogleMap;
    String url_locate_all_bins = "http://172.16.176.179/123.php";
    String url_locate_filled_bins = "http://172.16.176.179/locate_filled_bins.php";

    ArrayList<String> lat=new ArrayList<>();    //used in downloader() fn.
    ArrayList<String> lng=new ArrayList<>();    //used in downloader() fn.
    ArrayList<String> binId=new ArrayList<>();
    protected View view;
    MarkerOptions options;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //view = inflater.inflate(R.layout.fragment_bin_markers, container, false);
        AfterLogin1.toolbar.setTitle("Bin Locations");



        //checking if google play services are working...
        if (googleServicesAvailable()) {
            Toast.makeText(getContext(), "Nice Play Services Working...", Toast.LENGTH_LONG).show();
            //    activity.setContentView(R.layout.activity_bin_markers);
            view = inflater.inflate(R.layout.fragment_bin_markers, container, false);

            Button button = (Button)view.findViewById(R.id.btn_locate_all_bins);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGoogleMap.clear();
                    try {
                        locateAllBins(v);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            Button button1 = (Button)view.findViewById(R.id.btn_locate_filled_bins);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGoogleMap.clear();
                    try {
                        locateFilledBins(v);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            //initializing maps...
            initMap();
        } else {
            //No maps available here.
        }

        return view;
    }

    private void initMap() {
        if(mGoogleMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapfragment1);
            mapFragment.getMapAsync(this);
        }

    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(getContext());
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;

        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(getActivity() , isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(getContext(), "Can't connect to Google Play Services", Toast.LENGTH_LONG).show();

        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        gotoLocationZoom(25.536014,84.8488763, 8);

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
    }

    private void gotoLocationZoom(double lat, double lng, float zoom) {
        LatLng latlng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, zoom);
        mGoogleMap.moveCamera(update);
    }

    private void locateAllBins(View view) throws IOException {

        //permitting network on existing thread...
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());

        // downloader() is used to download and data from server and store it in Array of String...
        downloader(url_locate_all_bins);

        int size = lat.size();
        for(int i=0; i<size; i++)
        {

            double latitude = Double.parseDouble(lat.get(i).toString());
            double longitude = Double.parseDouble(lng.get(i).toString());
            String BIN_ID = binId.get(i);

            /*Log.v("Latitude is", "" + latitude);
            Log.v("Longitude is", "" + longitude);
            Log.v("binId is", "" + BIN_ID);*/

            options = new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .snippet("A Bin is HERE")
                    .title(BIN_ID);
            mGoogleMap.addMarker(options);

        }

        mGoogleMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                // Enter your code here to delete a bin data.
                delete_bin(marker);

            }
        });
    }

    public void delete_bin(Marker marker) {

        final Marker markerTemp = marker;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Are You sure, You want to remove bin");
        alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                BackgroundWorker backgroundWorker = new BackgroundWorker(getActivity());
                backgroundWorker.execute("deletebin", markerTemp.getTitle());

                markerTemp.remove();

                //Toast.makeText(getApplicationContext() , "You Clicked delete" , Toast.LENGTH_SHORT).show();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel" , new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog , int which){
                Toast.makeText(getContext() , "Clicked Cancel Button" , Toast.LENGTH_SHORT).show();

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    private void locateFilledBins(View view) throws IOException {

        GoogleMap googleMap2 = mGoogleMap;

        //permitting network on existing thread...
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());

        // downloader() is used to download and data from server and store it in Array of String...
        downloader(url_locate_filled_bins);

        int size = lat.size();
        for(int i=0; i<size; i++)
        {

            double latitude = Double.parseDouble(lat.get(i).toString());
            double longitude = Double.parseDouble(lng.get(i).toString());
            String BIN_ID = binId.get(i);

            /*Log.v("Latitude is", "" + latitude);
            Log.v("Longitude is", "" + longitude);
            Log.v("binId is", "" + BIN_ID);*/

            if(BIN_ID.equals("BINSTART") || BIN_ID.equals("BINEND")){
                // Do not add marker for START & END position !!!
            }else {

                MarkerOptions options = new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .snippet("A Bin is HERE")
                        .title(BIN_ID);
                googleMap2.addMarker(options);
            }

        }


    }





    // method used to download bin_details( lat, lng, id ) from server.
    public void downloader(String address){
        //connect & get string of data.
        InputStream is = null;
        String line = null;

        try {
            URL url = new URL(address);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            is = new BufferedInputStream(con.getInputStream());
            //Log.d("Tag1 : ", "We are in downloader : ");

            BufferedReader br=new BufferedReader(new InputStreamReader(is));

            StringBuilder sb= new StringBuilder();


            if(br != null){
                while((line=br.readLine()) != null){
                    sb.append(line).append('\n');
                }
            }

            String data = sb.toString();
            Log.d("String from server : ", "" + data);

            try {
                JSONArray ja =new JSONArray(data);
                JSONObject jo =null;

                lat.clear();
                lng.clear();
                binId.clear();

                for(int i=0; i<ja.length(); i++ ){
                    jo = ja.getJSONObject(i);

                    String latitude = jo.getString("lat");
                    String longitude = jo.getString("lng");
                    String BIN_ID = jo.getString("bin_id");
                    lat.add(latitude);
                    lng.add(longitude);
                    binId.add(BIN_ID);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
