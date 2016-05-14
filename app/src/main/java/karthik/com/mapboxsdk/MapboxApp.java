package karthik.com.mapboxsdk;

import android.app.Application;

import com.karumi.dexter.Dexter;

/**
 * Created by Karthik R on 5/13/2016.
 */
public class MapboxApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Dexter.initialize(this);
    }
}
