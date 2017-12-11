package com.eagle.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Zone on 2017/12/10.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    @SerializedName("update")
    public Update mUpdate;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}
