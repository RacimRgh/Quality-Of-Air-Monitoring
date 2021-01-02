package com.example.quality_of_air_monitoring;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quality_of_air_monitoring.accounts_creation.DatabaseHelper;
import com.example.quality_of_air_monitoring.accounts_creation.Weather;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
//import com.bottomnavigationview.R;


public class HistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LineChart chartgraph;
    private LineChart chartgraph2;

    public HistoryFragment() {
        // Required empty public constructor
    }

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chartgraph = (LineChart) (view.findViewById(R.id.chart));
        //chartgraph2 = view.findViewById(R.id.chart2);
        setUp();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setUp(){
        DatabaseHelper dbhelper = new DatabaseHelper(getContext());
        List<Weather> list = dbhelper.getAllRows();
        for(Weather w : list)
        {
            String log = "\n-Long: " + w.getLon() + "\n-Lat: " + w.getLat() + "\n-Date: " + w.getDate() + "\n-Humidity: " + w.getHmd() + "\n-Temperature: "+w.getTmp();
            Log.d("***********", log);
        }

        ArrayList listdate = new ArrayList();
        ArrayList listhumid = new ArrayList();
        ArrayList listtemper = new ArrayList();

        /*****************************************************************/
        List<Entry> valsComp1 = new ArrayList<Entry>();
        List<Entry> valsComp2 = new ArrayList<Entry>();

        // the labels that should be drawn on the XAxis
        final String[] quarters = new String[] { "Q1", "Q2", "Q3", "Q4" };

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters[(int) value];
            }

            // we don't draw numbers, so no decimal digits needed
            //Override
            //public int getDecimalDigits() {  return 0; }
        };

        //XAxis xAxis = chartgraph.getXAxis();
        //xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        //xAxis.setValueFormatter(formatter);

        Entry c1e1 = new Entry(0f, 100f); // 0 == quarter 1
        valsComp1.add(c1e1);
        Entry c1e2 = new Entry(1f, 140f); // 1 == quarter 2 ...
        valsComp1.add(c1e2);
        Entry c1e3 = new Entry(2f, 90f); // 0 == quarter 1
        valsComp1.add(c1e3);
        Entry c1e4 = new Entry(3f, 190f); // 1 == quarter 2 ...
        valsComp1.add(c1e4);
        // and so on ...

        Entry c2e1 = new Entry(0f, 130f); // 0 == quarter 1
        valsComp2.add(c2e1);
        Entry c2e2 = new Entry(1f, 115f); // 1 == quarter 2 ...
        valsComp2.add(c2e2);
        Entry c2e3 = new Entry(2f, 50f); // 0 == quarter 1
        valsComp2.add(c2e3);
        Entry c2e4 = new Entry(3f, 200f); // 1 == quarter 2 ...
        valsComp2.add(c2e4);
        //...

        LineDataSet setComp1 = new LineDataSet(valsComp1, "Company 1");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setColor(Color.rgb(0,0,255));
        LineDataSet setComp2 = new LineDataSet(valsComp2, "Company 2");
        setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp2.setColor(Color.rgb(255,0,0));

        // use the interface ILineDataSet
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);
        dataSets.add(setComp2);

        LineData data = new LineData(dataSets);

        /*********************Styling***********************************************/
        XAxis xAxis;
        {   // // X-Axis Style // //
            xAxis = chartgraph.getXAxis();

            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 10f, 0f);
        }

        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = chartgraph.getAxisLeft();

            // disable dual axis (only use LEFT axis)
            chartgraph.getAxisRight().setEnabled(false);

            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f);

            // axis range
            yAxis.setAxisMaximum(200f);
            yAxis.setAxisMinimum(-50f);
        }


        chartgraph.setData(data);
        chartgraph.invalidate(); // refresh

        /*******************************************************************/

        /*BarData data = new BarData(bardataset);
        chartgraph.setData(data); // set the data and list of labels into chart
        chartgraph.setDescription(null);
        //chartgraph.setDescription((Description)(R.string.chart_description1));  // set the description
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        chartgraph.animateY(5000); */
        
        /*for (int i=0; i<list.size(); i++) {
        /*
            try {
                Date date1=new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss").parse(list.get(i).getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

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
        chartgraph.invalidate(); */

        //BarData data2 = new BarData(bardate, barhmd);
        //chartgraph2.setData(data);
        //chartgraph2.setDescription("Humidity evolution");
        //chartgraph2.animateXY(2000, 2000);
        //chartgraph2.invalidate();
    }
}