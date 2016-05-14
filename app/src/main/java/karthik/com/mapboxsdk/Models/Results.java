package karthik.com.mapboxsdk.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Karthik R on 5/10/2016.
 */
public class Results {
    @SerializedName("geometry")
    public Geometry geometry;

    @SerializedName("name")
    public String name;

    @SerializedName("opening_hours")
    public OpeningHours openingHours;

    @SerializedName("vicinity")
    public String vicinity;
}
