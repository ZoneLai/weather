package com.eagle.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DELL on 2017/12/10.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update mUpdate;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}
