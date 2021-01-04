package com.example.quality_of_air_monitoring;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.quality_of_air_monitoring.R;
import com.example.quality_of_air_monitoring.accounts_creation.DatabaseHelper;
import com.example.quality_of_air_monitoring.accounts_creation.Weather;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MonitorFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor mTemp, mHumd;
    private TextView txtProgress, txtProgressHmd;
    private ProgressBar progressBar, progressBarHmd;
    private float pStatus = 0, hStatus = 0;
    private Handler handler = new Handler();
    private Handler handlerHumd = new Handler();
    private float tmp, hmd, tmp1, hmd1;

    private DatabaseHelper db;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Double lat;
    private Double lgt;

    private SwipeRefreshLayout swipeContainer;

    public MonitorFragment() {
        // Required empty public constructor
    }

    public String getCity (List<Address> addresses) {
        String city = addresses.get(0).getLocality();
        return city;
    }

    public Double getLong () {
        return lgt;
    }

    public static String getCurrentDate(){
        return CurrentDate;
    }

    public static MonitorFragment newInstance(Double lat, Double lgt) {
        MonitorFragment fragment = new MonitorFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_PARAM1, lat);
        args.putDouble(ARG_PARAM2, lgt);
        fragment.setArguments(args);
        return fragment;
    }

    // Function to display temperature progress bar
    public void tempBar(Sensor mTemp, float tmp){
        if( mTemp != null)
        {
            // The sensors exists
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (pStatus <= tmp) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress((int)pStatus);
                                txtProgress.setText(pStatus + " °C");
                            }
                        });
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        pStatus++;
                    }
                }
            }).start();
        }
        else
        {
            //Sensor unavailable
            txtProgress.setText("Temperature sensor unavailable !");
        }
    }

    // Function to display humidity progress bar
    public void humdBar(Sensor mHumd, float hmd){
        if( mHumd != null)
        {
            // The sensors exists
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (hStatus <= hmd) {
                        handlerHumd.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBarHmd.setProgress((int)hStatus);
                                txtProgressHmd.setText(hStatus + " %");
                            }
                        });
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        hStatus++;
                    }
                }
            }).start();
        }
        else
        {
            //Sensor unavailable
            txtProgressHmd.setText("Humidity sensor unavailable !");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHelper(getContext());
        db.clearWeather();

        if (getArguments() != null) {
            lat = getArguments().getDouble(ARG_PARAM1);
            lgt = getArguments().getDouble(ARG_PARAM2);
        }

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mTemp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mHumd = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        tempBar(mTemp, tmp);
        humdBar(mHumd, hmd);
    }

   /* @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        tempBar(mTemp, tmp);
        humdBar(mHumd, hmd);
    } */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // getView() works only after onCreateView()
        // We can't use it inside onCreate() or onCreateView() methods of the fragment

        View view = inflater.inflate(R.layout.fragment_monitor, container, false);

        TextView adTextView = view.findViewById(R.id.addressTextView);
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try{
            List<Address> addresses = geocoder.getFromLocation(lat, lgt, 1);
            if(addresses.isEmpty()){
                adTextView.setText("Latitude: "+ lat + " - Longitude: " + lgt);
            } else {
                String address = addresses.get(0).getAddressLine(0);
                //String city = addresses.get(0).getLocality();
                //String country = addresses.get(0).getCountryName();
                //String postalCode = addresses.get(0).getPostalCode();

                String text = address + ", ";
                /*if (postalCode != null && city != null) {
                    text += postalCode + " " + city + ", ";
                }
                text += country;
                */
                //Log.d("MonitorActivity2", "Address: "+ text);
                adTextView.setText(text);
            }
        } catch(IOException e){
            e.printStackTrace();
        }


        //Log.d("MonitorActivity", "Latitude: "+ lat + " - Longitude: " + lgt);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //fetchTimelineAsync(0);
                progressBar.setProgress((int)tmp);
                txtProgress.setText(tmp + " °C");
                progressBarHmd.setProgress((int)hmd);
                txtProgressHmd.setText(hmd + " %");
                swipeContainer.setRefreshing(false);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    static String CurrentDate;
    @Override
    public void onSensorChanged(SensorEvent event) {
        /**
         * Will be called if we have a new reading from a sensor with the exact same sensor values (but a newer timestamp).
         */
        txtProgress = (TextView) getView().findViewById(R.id.txtProgress);
        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        txtProgressHmd = (TextView) getView().findViewById(R.id.txtProgressHumd);
        progressBarHmd = (ProgressBar) getView().findViewById(R.id.progressBarHumd);
        //Log.d("Type: ", ""+event.sensor.getType());

        tmp1 = tmp;
        hmd1 = hmd;

        if(event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE)
        {
            // The ambient temperature sensor returns a single value.
            tmp = event.values[0];
        }
        if(event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY)
        {
            // The relative humidity sensor returns a single value.
            hmd = event.values[0];
        }

        // check if values changed
        // if not, we don't save them again in the DB
        if(tmp1 != tmp || hmd1 != hmd){
            progressBar.setProgress((int)tmp);
            txtProgress.setText(tmp + " °C");
            progressBarHmd.setProgress((int)hmd);
            txtProgressHmd.setText(hmd + " %");

            // Get the current date
            CurrentDate = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss", Locale.getDefault()).format(new Date());

            Log.d("Db add: ", "\n- "+ lat + " - "+ lgt + " - "+ CurrentDate + " - "+ hmd + " - " + tmp);

            Weather weather = new Weather(lat, lgt, CurrentDate, hmd, tmp);
            db.addWeather(weather);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, mTemp, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mHumd, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
