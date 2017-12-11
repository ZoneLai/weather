package com.eagle.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Zone on 2017/12/10.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More mMore;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}
