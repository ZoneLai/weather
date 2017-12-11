package com.eagle.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Zone on 2017/12/10.
 */

public class Forecast {
    public String date;

    @SerializedName("tmp")
    public Temperature mTemperature;

    @SerializedName("cond")
    public More mMore;

    public class Temperature {
        public String max;
        public String min;
    }

    public class More {
        @SerializedName("txt_d")
        public String info;
    }
}
