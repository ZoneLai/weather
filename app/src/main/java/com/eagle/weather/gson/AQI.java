package com.eagle.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Zone on 2017/12/10.
 */

public class AQI {
    @SerializedName("city")
    public AQICity mAQICity;

    public class AQICity {
        @SerializedName("aqi")
        public String aqi;
        @SerializedName("pm25")
        public String pm25;
    }
}
