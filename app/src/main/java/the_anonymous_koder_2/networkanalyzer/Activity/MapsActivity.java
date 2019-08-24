package the_anonymous_koder_2.networkanalyzer.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import the_anonymous_koder_2.networkanalyzer.Model.Bounds;
import the_anonymous_koder_2.networkanalyzer.Model.InfoPacket;
import the_anonymous_koder_2.networkanalyzer.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    private GoogleMap mMap;
    String operator;
    DatabaseReference rootRef, ref;
    String child = "";
    LatLng topLeft, topRight, bottomLeft, bottomRight;
    String TAG = "Maps Activity";
    int previousZoom = 0;
    int currentZoom = 0;
    boolean flag = true;
    Context context;
    DataSnapshot dataSnapshot;
    ArrayList<InfoPacket> infoPackets = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        context = getApplicationContext();
        rootRef = FirebaseDatabase.getInstance().getReference();
        operator = getIntent().getStringExtra(context.getString(R.string.firebase_operators));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        rootRef.child("raw").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: datasnapshot initiated");
                MapsActivity.this.dataSnapshot = dataSnapshot;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        AlertDialog.Builder a = new AlertDialog.Builder(this);
        a.setIcon(android.R.drawable.ic_dialog_info);
        a.setTitle("Map Guidelines");
        a.setMessage("Green : Best Signals\nYellow : Good Signals\nBlue : Normal Signals\nRed : Poor Signals\nBlack : Unknown Sites\n");
        a.show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng delhi = new LatLng(28, 77);
        mMap.addMarker(new MarkerOptions().position(delhi).title("Marker in Delhi"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(delhi));
        mMap.setOnCameraIdleListener(this);
    }


    public void addHeat(){
        ArrayList<WeightedLatLng> unknownSignalList = new ArrayList<>();
        ArrayList<WeightedLatLng> poorSignalList = new ArrayList<>();
        ArrayList<WeightedLatLng> slowSignalList = new ArrayList<>();
        ArrayList<WeightedLatLng> mediumSignalList = new ArrayList<>();
        ArrayList<WeightedLatLng> highSignalList = new ArrayList<>();
        for(int j =0; j < infoPackets.size();j++){
            InfoPacket pkt = infoPackets.get(j);
            double lat = pkt.getLatitude();
            double lng = pkt.getLongitude();
            int level = pkt.getLevel();
            switch (level){
                case 0:
                    unknownSignalList.add(new WeightedLatLng(new LatLng(lat,lng),4));
                    break;
                case 1:
                    poorSignalList.add(new WeightedLatLng(new LatLng(lat,lng),4));
                    break;
                case 2:
                    slowSignalList.add(new WeightedLatLng(new LatLng(lat,lng),4));
                    break;
                case 3:
                    mediumSignalList.add(new WeightedLatLng(new LatLng(lat,lng),4));
                    break;
                case 4:
                    highSignalList.add(new WeightedLatLng(new LatLng(lat,lng),4));
                    break;
            }
        }
        if(poorSignalList.size()>0)
            addPoorSignalHeat(poorSignalList);
        if(highSignalList.size()>0)
            addHighSignalHeat(highSignalList);
        if(unknownSignalList.size()>0)
            addUnknownSignalHeat(unknownSignalList);
        if(slowSignalList.size()>0)
            addSlowSignalHeat(slowSignalList);
        if(mediumSignalList.size()>0)
            addMediumSignalHeat(mediumSignalList);
    }

    public void addUnknownSignalHeat(ArrayList<WeightedLatLng> unknownSignalList){

        int[] colors = {
                Color.rgb(75, 79, 75), // green
                Color.rgb(50, 55, 50),  // red
                Color.rgb(26, 32, 26),   //
                Color.rgb(4, 8, 4)   // blue
        };

        float[] startPoints = {
                0.1f, 0.2f , 0.3f, 0.4f
        };

        Gradient gradient = new Gradient(colors, startPoints);

        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .weightedData(unknownSignalList)
                .gradient(gradient)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    public void addPoorSignalHeat(ArrayList<WeightedLatLng> poorSignalList){

        int[] colors = {
                Color.rgb(255, 16, 16), // green
                Color.rgb(242, 12, 12),  // red
                Color.rgb(214, 8, 8),   //
                Color.rgb(189, 11, 11)   // blue
        };

        float[] startPoints = {
                0.2f, 0.4f , 0.6f, 0.8f
        };

        Gradient gradient = new Gradient(colors, startPoints);

        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .weightedData(poorSignalList)
                .gradient(gradient)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    public void addSlowSignalHeat(ArrayList<WeightedLatLng> slowSignalList){

        int[] colors = {
                Color.rgb(242, 248, 85), // green
                Color.rgb(236, 244, 21),  // red
                Color.rgb(216, 224, 11),   //
                Color.rgb(208, 215, 5)   // blue
        };

        float[] startPoints = {
                0.1f, 0.2f , 0.3f, 0.4f
        };

        Gradient gradient = new Gradient(colors, startPoints);

        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .weightedData(slowSignalList)
                .gradient(gradient)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    public void addMediumSignalHeat(ArrayList<WeightedLatLng> mediumSignalList){

        int[] colors = {
                Color.rgb(88, 56, 248), // green
                Color.rgb(67, 33,  237),  // red
                Color.rgb(50, 18, 210),   //
                Color.rgb(36, 6, 188)   // blue
        };

        float[] startPoints = {
                0.1f, 0.2f , 0.3f, 0.4f
        };

        Gradient gradient = new Gradient(colors, startPoints);

        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .weightedData(mediumSignalList)
                .gradient(gradient)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    public void addHighSignalHeat(ArrayList<WeightedLatLng> highSignalList){

        int[] colors = {
                Color.rgb( 16,255, 16), // green
                Color.rgb( 12,242, 12),  // red
                Color.rgb( 8, 214,8),   //
                Color.rgb( 11,189, 11)   // blue
        };

        float[] startPoints = {
                0.1f, 0.2f , 0.3f, 0.4f
        };

        Gradient gradient = new Gradient(colors, startPoints);

        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .weightedData(highSignalList)
                .gradient(gradient)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }


    @Override
    public void onCameraIdle() {
        LatLng test = mMap.getProjection().getVisibleRegion().farLeft;
        Log.d(TAG, "onMapReady: "+test.latitude + "," + test.longitude);
        final Bounds bounds = new Bounds(mMap.getProjection().getVisibleRegion());
        Log.d(TAG, "Bottom Left Lat Deg : "+bounds.getBottomLeftLatDeg()+"Top Right Lat Deg : "+bounds.getTopRightLatDeg());
        Log.d(TAG, "Bottom Left Lng Deg : "+bounds.getBottomLeftLngDeg()+"Top Right Lng Deg : "+bounds.getTopRightLngDeg());
        infoPackets.clear();
        int latMin = bounds.getBottomLeftLatDeg(), latMax = bounds.getTopRightLatDeg(), latMinMin = 0, latMinMax = 60;
        int lonMin = bounds.getBottomLeftLngDeg(), lonMax = bounds.getTopRightLngDeg(), lonMinMin = 0, lonMinMax = 60;
        if(dataSnapshot!=null) {
            for (DataSnapshot latDegreeSnapshot : dataSnapshot.getChildren()) {
                if ((Integer.parseInt(latDegreeSnapshot.getKey())) >= latMin && (Integer.parseInt(latDegreeSnapshot.getKey()) <= latMax)) {
                    for (DataSnapshot lngDegreeSnapshot : latDegreeSnapshot.getChildren()) {
                        if ((Integer.parseInt(lngDegreeSnapshot.getKey())) >= lonMin && (Integer.parseInt(lngDegreeSnapshot.getKey()) <= lonMax)) {
                            for (DataSnapshot latMinuteSnapshot : lngDegreeSnapshot.getChildren()) {
                                if ((Integer.parseInt(latMinuteSnapshot.getKey())) >= latMinMin && (Integer.parseInt(latMinuteSnapshot.getKey()) <= latMinMax)) {
                                    for (DataSnapshot lonMinuteSnapshot : latMinuteSnapshot.getChildren()) {
                                        if ((Integer.parseInt(lonMinuteSnapshot.getKey())) >= lonMinMin && (Integer.parseInt(lonMinuteSnapshot.getKey()) <= lonMinMax)) {
                                            for (DataSnapshot operatorSnapshot : lonMinuteSnapshot.getChildren()) {
                                                Log.d(TAG, "LatDegreeSnapshot " + latDegreeSnapshot.getKey() + " LngDegreeSnapshot " + lngDegreeSnapshot.getKey() + " LatMinuteSnapshot " + latMinuteSnapshot.getKey() + " LngMinuteSnapshot " + lonMinuteSnapshot.getKey() + " Operator " + operatorSnapshot.getKey());
                                                if ((operatorSnapshot.getKey()).equals(operator)) {
                                                    DataSnapshot yearSnapshot = null;
                                                    Iterator yearIterator = operatorSnapshot.getChildren().iterator();
                                                    while (yearIterator.hasNext())
                                                        yearSnapshot = (DataSnapshot) yearIterator.next();
                                                    DataSnapshot monthSnapshot = null;
                                                    Iterator monthIterator = yearSnapshot.getChildren().iterator();
                                                    while (monthIterator.hasNext())
                                                        monthSnapshot = (DataSnapshot) monthIterator.next();
                                                    DataSnapshot dateSnapshot = null;
                                                    Iterator dateIterator = monthSnapshot.getChildren().iterator();
                                                    while (dateIterator.hasNext())
                                                        dateSnapshot = (DataSnapshot) dateIterator.next();
                                                    //arraylist
                                                    for (DataSnapshot packetSnapshot : dateSnapshot.getChildren()) {
                                                        InfoPacket infoPacket = packetSnapshot.getValue(InfoPacket.class);
                                                        infoPackets.add(infoPacket);
                                                    }
                                                    //arraylist - same location and same operator
                                                    //algo to aggregate
                                                    //save data to same gierarchy in processed node
                                                    Log.d(TAG, "Total Packets: " + infoPackets.size());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(flag) {
                addHeat();
                flag = false;
            }
        }
    }
}
