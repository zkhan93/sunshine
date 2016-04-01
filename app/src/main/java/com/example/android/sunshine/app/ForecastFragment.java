package com.example.android.sunshine.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zeeshan Khan on 3/30/2016.
 */
public class ForecastFragment extends Fragment {
    public static final String TAG = "ForecastFragment";
    private ArrayAdapter<String> adapter = null;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        List<String> sampleData = new ArrayList<>();
        sampleData.add("Today-Sunny-88/63");
        sampleData.add("Tomorrow-Sunny-88/63");
        sampleData.add("Wed-Sunny-88/63");
        sampleData.add("Today-Sunny-88/63");
        sampleData.add("Tomorrow-Sunny-88/63");
        sampleData.add("Wed-Sunny-88/63");
        adapter = new ArrayAdapter<String>(getActivity(), R.layout
                .list_item_forcast, R.id.list_item_forecast_textview, sampleData);
        ((ListView) rootView.findViewById(R.id.listview_forecast)).setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_refresh) {
            new FetchWeatherTask().execute(226010);
            return true;
        }
        return false;
    }

    public class FetchWeatherTask extends AsyncTask<Integer, Void, String[]> {
        String TAG = FetchWeatherTask.class.getSimpleName();

        private String getReadableDateString(long time) {
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "main";
            final String OWM_MAX = "temp_max";
            final String OWM_MIN = "temp_min";
            final String OWM_DESCRIPTION = "description";
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
            Time dayTime = new Time();
            dayTime.setToNow();
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
            dayTime = new Time();
            String[] resultStrs = new String[numDays];
            for (int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay + i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }
            return resultStrs;
        }

        @Override
        protected String[] doInBackground(Integer... postalCode) {
            String forecastJsonStr = null;
            HttpURLConnection conn = null;
            BufferedReader br = null;
            try {
                String surl = new Uri.Builder().scheme("http")
                        .authority("api.openweathermap.org").appendPath("data").appendPath("2.5")
                        .appendPath("forecast").appendQueryParameter(Constants.WHETHER
                                .PARAMETERS_KEYS.LOCATION_ID, String.valueOf(postalCode))
                        .appendQueryParameter(Constants.WHETHER
                                .PARAMETERS_KEYS.MODE, Constants.WHETHER.PARAMETER_VALUES
                                .MODE).appendQueryParameter(Constants.WHETHER
                                .PARAMETERS_KEYS.APP_ID, Constants.WHETHER.PARAMETER_VALUES
                                .APPID).appendQueryParameter(Constants.WHETHER
                                .PARAMETERS_KEYS.COUNT, Constants.WHETHER.PARAMETER_VALUES
                                .COUNT).build().toString();
                Log.v(TAG, surl);
                URL url = new URL(surl);//lnew URL
                // (Constants.WHETHER.URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                InputStream ins = conn.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (ins == null) {
                    forecastJsonStr = null;
                    return null;
                }
                br = new BufferedReader(new InputStreamReader(ins));
                String line;
                while ((line = br.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    forecastJsonStr = null;
                    return null;
                }
                forecastJsonStr = buffer.toString();

            } catch (MalformedURLException urlEx) {
                Log.e(TAG, "" + urlEx);
                return null;
            } catch (IOException ioEx) {
                Log.e(TAG, "" + ioEx);
                return null;
            } finally {
                if (conn != null)
                    conn.disconnect();
                if (br != null)
                    try {
                        br.close();
                    } catch (IOException e) {
                        Log.e(TAG, "error closing stream" + e);
                    }
            }
            try {
                return getWeatherDataFromJson(forecastJsonStr, 7);
            } catch (JSONException jEx) {
                Log.e(TAG, "error parsing/processing JSON" + jEx);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings == null)
                return;
            adapter.addAll(strings);
        }
    }
}
