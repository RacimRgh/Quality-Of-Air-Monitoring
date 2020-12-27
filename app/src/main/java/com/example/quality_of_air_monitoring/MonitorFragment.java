package com.example.quality_of_air_monitoring;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quality_of_air_monitoring.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MonitorFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor mTemp, mHumd;
    private TextView txtProgress, txtProgressHmd;
    private ProgressBar progressBar, progressBarHmd;
    private int pStatus = 0, hStatus = 0;
    private Handler handler = new Handler();
    private Handler handlerHumd = new Handler();
    private float tmp, hmd;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Double lat;
    private Double lgt;

    private SwipeRefreshLayout swipeContainer;

    public MonitorFragment() {
        // Required empty public constructor
    }

    public static MonitorFragment newInstance(Double lat, Double lgt) {
        MonitorFragment fragment = new MonitorFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_PARAM1, lat);
        args.putDouble(ARG_PARAM2, lgt);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lat = getArguments().getDouble(ARG_PARAM1);
            lgt = getArguments().getDouble(ARG_PARAM2);
        }

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mTemp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mHumd = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        if( mTemp != null)
        {
            // The sensors exists
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (pStatus <= (int)tmp) {
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

        if( mHumd != null)
        {
            // The sensors exists
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (hStatus <= (int)hmd) {
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

        //Intent intent = new Intent(getActivity(), SensorActivity.class);
        //startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // getView() works only after onCreateView()
        // W can't use it inside onCreate() or onCreateView() methods of the fragment

        View view = inflater.inflate(R.layout.fragment_monitor, container, false);

        Button button = (Button) view.findViewById(R.id.buttonMonitor);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SensorActivity.class);
                startActivity(intent);
            }
        });
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
                // Make sure you call swipeContainer.setRefreshing(false)
                //fetchTimelineAsync(0);
                //Bundle tempBundle = new Bundle();
                //onCreate(tempBundle);
                swipeContainer.setRefreshing(false);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        txtProgress = (TextView) getView().findViewById(R.id.txtProgress);
        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        txtProgressHmd = (TextView) getView().findViewById(R.id.txtProgressHumd);
        progressBarHmd = (ProgressBar) getView().findViewById(R.id.progressBarHumd);
        Log.d("Type: ", ""+event.sensor.getType());
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
