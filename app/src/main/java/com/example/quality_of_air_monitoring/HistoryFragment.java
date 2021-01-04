package com.example.quality_of_air_monitoring;

import android.graphics.Color;
import android.graphics.Paint;
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
import com.github.mikephil.charting.components.Legend;
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

    private LineChart chart;

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
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        chart = (LineChart) (view.findViewById(R.id.chart));
        charTest();
        return view;
    }
/*
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chart = (LineChart) (view.findViewById(R.id.chart));
        charTest();
    }
    */

    public void charTest(){

        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(true);
        chart.getDescription().setText("Variation of the temperature and humidity over time");
        chart.getDescription().setTextSize(20f);
        chart.getDescription().setTextAlign(Paint.Align.CENTER);
        chart.setDrawBorders(true);

        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setDrawAxisLine(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setSpaceMax(1f);
        chart.getXAxis().setSpaceMin(1f);
        //chart.getAxisLeft().setAxisMinimum(0);
        //chart.getAxisLeft().setAxisMaximum(100);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setTextSize(12f);

        DatabaseHelper db = new DatabaseHelper(getContext());
        List<Weather> list = db.getAllRows();

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        ArrayList<Entry> tempValues = new ArrayList<>();
        ArrayList<Entry> humdValues = new ArrayList<>();

        ArrayList<String> hours = new ArrayList<>();
        // Init list
        int i=0;
        Log.d("Size: ", ""+list.size());
        for(i=0; i<list.size()*4 +1; i++)
            hours.add("");
        //String[] hours = new String[]{};
        int j=0;
        for (Weather w : list) {
            if(j<i)
            {
                String log = "\n-Long: " + w.getLon() + "\n-Lat: " + w.getLat() + "\n-Date: " + w.getDate() + "\n-Humidity: " + w.getHmd() + "\n-Temperature: "+w.getTmp();
                Log.d("\n*********************\n", log);
                //double val = (Math.random() * 100);
                float valT = w.getTmp();
                float valH = w.getHmd();
                tempValues.add(new Entry(i++, valT));
                humdValues.add(new Entry(i++, valH));

                // the labels that should be drawn on the XAxis
                //hours[i] = w.getDate();
                hours.set(j, w.getDate());
                //hours.add(w.getDate());
                j+=4;
            }
        }


        //final String[] quarters = new String[] { "Q1", "Q2", "Q3", "Q4" };

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Log.d("Value: ", "v: "+value);
                if((int) value < list.size())
                    return hours.get((int) value);
                return "";
            }
            // we don't draw numbers, so no decimal digits needed
            //Override
            //public int getDecimalDigits() {  return 0; }
        };

        XAxis xAxis = chart.getXAxis();
//        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        LineDataSet dt = new LineDataSet(tempValues, "Temperature");
        dt.setLineWidth(2.5f);
        dt.setCircleRadius(6f);
        dt.setValueTextSize(10f);
        dt.setColor(Color.RED);
        dt.setCircleColor(Color.RED);
        dataSets.add(dt);

        LineDataSet dh = new LineDataSet(humdValues, "Humidity");
        dh.setLineWidth(2.5f);
        dh.setCircleRadius(6f);
        dh.setValueTextSize(10f);
        dh.setColor(Color.BLUE);
        dh.setCircleColor(Color.BLUE);
        dataSets.add(dh);

        LineData data = new LineData(dataSets);
        //chart.setVisibleXRange(100,100);
        chart.setData(data);
        //chart.setViewPortOffsets(0f, 0f, 0f, 0f);
        chart.animateXY(2000, 2000);
        chart.invalidate();
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

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

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

        {   // // X-Axis Style // //
            xAxis = chart.getXAxis();

            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 10f, 0f);
        }

        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = chart.getAxisLeft();

            // disable dual axis (only use LEFT axis)
            chart.getAxisRight().setEnabled(false);

            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f);

            // axis range
            yAxis.setAxisMaximum(200f);
            yAxis.setAxisMinimum(-50f);
        }


        chart.setData(data);
        chart.getDescription().setEnabled(true);
        chart.getDescription().setText("Temperature and humidity values in the last 6 calculations");
        chart.getDescription().setTextSize(20f);
        chart.getDescription().setTextColor(Color.BLACK);
        chart.getDescription().setTextAlign(Paint.Align.CENTER);

        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(11f);
        l.setTextColor(Color.BLACK);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        chart.animateXY(2000, 2000);
        chart.invalidate(); // refresh

        /*******************************************************************/

        /*BarData data = new BarData(bardataset);
        chart.setData(data); // set the data and list of labels into chart
        chart.setDescription(null);
        //chart.setDescription((Description)(R.string.chart_description1));  // set the description
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.animateY(5000); */
        
        /*
        for (int i=0; i<list.size(); i++) {
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
        */
    }
}