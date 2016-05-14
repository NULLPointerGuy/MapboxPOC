package karthik.com.mapboxsdk.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Karthik R on 5/13/2016.
 */
public class Response {
    @SerializedName("results")
    public ArrayList<Results> results = new ArrayList<>();
}
