package com.example.quality_of_air_monitoring;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quality_of_air_monitoring.accounts_creation.DatabaseHelper;
import com.example.quality_of_air_monitoring.accounts_creation.Weather;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
//import com.bottomnavigationview.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private BarChart chartgraph;
    private BarChart chartgraph2;

    public HistoryFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chartgraph = view.findViewById(R.id.chart);
        chartgraph2 = view.findViewById(R.id.chart2);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setUp(){
        DatabaseHelper dbhelper = new DatabaseHelper(getContext());
        List<Weather> list = dbhelper.getAllRows();

        ArrayList listdate = new ArrayList();
        ArrayList listhumid = new ArrayList();
        ArrayList listtemper = new ArrayList();


        for (int i=0; i<list.size(); i++) {
        /*
            try {
                Date date1=new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss").parse(list.get(i).getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        */
            String date = list.get(i).getDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy 'at' HH:mm:ss", Locale.ENGLISH);
            LocalDateTime localDate = LocalDateTime.parse(date, formatter);
            long timeInMilliseconds = localDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();

            BarEntry vtem = new BarEntry(timeInMilliseconds, list.get(i).getTmp());
            BarEntry vhmd = new BarEntry(timeInMilliseconds, list.get(i).getHmd());
            BarEntry vdate = new BarEntry(timeInMilliseconds, timeInMilliseconds);

            // Si non fonctionel, decommenter la ligne ci dessous et commenter la ligne en dessous d'elle

            //listdate.add(timeInMilliseconds);
            listdate.add(vdate);
            listhumid.add(vhmd);
            listtemper.add(vtem);

        }

        BarDataSet bartemp = new BarDataSet(listtemper, "temperature");
        bartemp.setColor(Color.rgb(0, 155, 0));
        BarDataSet barhmd = new BarDataSet(listhumid, "humidity");
        barhmd.setColor(Color.rgb(0, 0, 155));

        BarDataSet bardate = new BarDataSet(listdate, "date");
        barhmd.setColor(Color.rgb(0, 0, 155));

        ArrayList dataSets = new ArrayList();
        dataSets.add(bartemp);
        dataSets.add(barhmd);

        BarData data = new BarData(bardate, bartemp);
        chartgraph.setData(data);
       //chartgraph.setDescription("Temp evolution");
        chartgraph.animateXY(2000, 2000);
        chartgraph.invalidate();

        BarData data2 = new BarData(bardate, barhmd);
        chartgraph2.setData(data);
        //chartgraph2.setDescription("Humidity evolution");
        chartgraph2.animateXY(2000, 2000);
        chartgraph2.invalidate();
    }
}