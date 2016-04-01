package com.example.android.sunshine.app;

/**
 * Created by Zeeshan Khan on 3/31/2016.
 */
public interface Constants {

    interface WHETHER {
        String URL = "http://api.openweathermap.org/data/2.5/forecast?q=226010&mode=json&cnt=7&unit=metric&appid=60af7c7f072bcf38560db68d81fbebbe";

        interface PARAMETERS_KEYS {
            String MODE = "mode";
            String COUNT = "cnt";
            String UNIT = "unit";
            String LOCATION_ID = "q";
            String APP_ID = "appid";
        }

        interface PARAMETER_VALUES {
            String UNIT = "metric";
            String MODE = "json";
            String COUNT = "7";
            String APPID = "60af7c7f072bcf38560db68d81fbebbe";
            String LOCATION_ID = "226010";
        }
    }
}
