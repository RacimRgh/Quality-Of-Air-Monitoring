package com.example.quality_of_air_monitoring;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quality_of_air_monitoring.R;

public class MonitorFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor mTemp;
    private TextView txtProgress;
    private ProgressBar progressBar;
    private int pStatus = 0;
    private Handler handler = new Handler();
    private float lux;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public MonitorFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MonitorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MonitorFragment newInstance(String param1, String param2) {
        MonitorFragment fragment = new MonitorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mTemp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        if( mTemp != null)
        {
            // The sensors exists
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (pStatus <= (int)lux) {
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
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        lux = event.values[0];
        txtProgress = (TextView) getView().findViewById(R.id.txtProgress);
        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, mTemp, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
