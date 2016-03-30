package com.example.android.sunshine.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zeeshan Khan on 3/30/2016.
 */
public class ForecastFragment extends Fragment {
    /**
     * A placeholder fragment containing a simple view.
     */
    public ForecastFragment() {
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout
                .list_item_forcast, R.id.list_item_forecast_textview, sampleData);
        ((ListView) rootView.findViewById(R.id.listview_forecast)).setAdapter(adapter);
        return rootView;
    }

}
