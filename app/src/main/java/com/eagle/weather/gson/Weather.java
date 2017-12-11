package com.eagle.weather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Zone on 2017/12/10.
 */

public class Weather {
    public String status;

    @SerializedName("basic")
    public Basic mBasic;

    @SerializedName("aqi")
    public AQI mAQI;

    @SerializedName("now")
    public Now mNow;

    @SerializedName("suggestion")
    public Suggestion mSuggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> mForecastList;
}
