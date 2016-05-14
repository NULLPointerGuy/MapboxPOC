package karthik.com.mapboxsdk;

import android.Manifest.permission;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMap.CancelableCallback;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.io.IOException;
import java.io.Reader;

import karthik.com.mapboxsdk.Models.Results;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends Activity implements MapboxMap.OnMyLocationChangeListener {
    /**
     * MapBox varaibles
     */
    private MapView mapView;
    private MapboxMap map;
    private MarkerOptions user_postion;

    /**
     * Google PLaces PLACES_API variable
     */
    private String url = "https://maps.googleapis.com/maps/api/place/search/json?radius=500&sensor=false&key=";
    private String PLACES_API = BuildConfig.API_KEY;
    private String PLACE_QUERY = "&types=pharmacy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        if(android.os.Build.VERSION.SDK_INT>= VERSION_CODES.M){
            Dexter.checkPermission(new PermissionListener() {
                @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                    getMyLocation();
                }
                @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                    //show snackbar message saying location permission is required

                }
                @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                }
            }, permission.ACCESS_FINE_LOCATION);
        }else{
            getMyLocation();
        }
    }


    private void getMyLocation(){
        mapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                mapboxMap.setOnMyLocationChangeListener(MainActivity.this);
                mapboxMap.setMyLocationEnabled(true);
            }
        });
    }

    private void getShopsLocation(LatLng latLng){
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url+ PLACES_API +"&location="+latLng.getLatitude()+ "," +latLng.getLongitude()+PLACE_QUERY).newBuilder();
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TAG","PLACES_API ERROR");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                ResponseBody body = response.body();
                Reader charStream = body.charStream();
                karthik.com.mapboxsdk.Models.Response map_response= gson.fromJson(charStream, karthik.com.mapboxsdk.Models.Response.class);
                mapShopsLocation(map_response);
            }
        });
    }


    private void mapShopsLocation(karthik.com.mapboxsdk.Models.Response mapResponse){
        IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
        Drawable iconDrawable = ContextCompat.getDrawable(MainActivity.this, R.mipmap.med);
        try{
            for(int i=0;i<mapResponse.results.size();i++){
                Results results = mapResponse.results.get(i);
                MarkerOptions shops_location = new MarkerOptions()
                        .title(results.name)
                        .snippet("Medical shop number: "+i)
                        .icon(iconFactory.fromDrawable(iconDrawable))
                        .position(new LatLng(Double.parseDouble(results.geometry.location.lat),Double.parseDouble(results.geometry.location.lon)));
                map.addMarker(shops_location);
            }
        }catch (Exception e){
            Log.d("TAG","ERROR PARSING SHOP LOCATION");
            Log.d("TAG",e.getMessage());
        }
    }

    @Override
    public void onMyLocationChange(@Nullable final Location location) {
        if (location != null) {
            if(user_postion==null){
                user_postion = new MarkerOptions();
                user_postion.title("You are here!!");
                user_postion.snippet("This is your current location");
                user_postion.position(new LatLng(location.getLatitude(), location.getLongitude()));
                map.addMarker(user_postion);
            }
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(),location.getLongitude()))
                    .zoom(15)
                    .bearing(180)
                    .tilt(40)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(position),13000,new CancelableCallback() {


                @Override
                public void onCancel() {}

                @Override
                public void onFinish() {
                    getShopsLocation(new LatLng(location.getLatitude(),location.getLongitude()));
                }
            });

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
