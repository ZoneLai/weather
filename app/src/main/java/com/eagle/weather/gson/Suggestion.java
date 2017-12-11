package com.eagle.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Zone on 2017/12/10.
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort mComfort;

    @SerializedName("cw")
    public CarWash mCarWash;

    @SerializedName("sport")
    public Sport mSport;

    public class Comfort {
        @SerializedName("txt")
        public String info;
    }

    public class CarWash {
        @SerializedName("txt")
        public String info;
    }

    public class Sport {
        @SerializedName("txt")
        public String info;
    }
}
