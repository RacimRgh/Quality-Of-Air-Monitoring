package com.example.quality_of_air_monitoring;

import android.graphics.Canvas;
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
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class HistoryFragment extends Fragment {

    private LineChart chart;

    public SimpleDateFormat dateFormatter;
    public Date date;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public class CustomXAxisRenderer extends XAxisRenderer {
        public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
            super(viewPortHandler, xAxis, trans);
        }

        @Override
        protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
            String line[] = formattedLabel.split("\n");
            Log.d("Line: ", line[0]);
            Utils.drawXAxisValue(c, line[0], x, y, mAxisLabelPaint, anchor, angleDegrees);
            Utils.drawXAxisValue(c, line[1], x + mAxisLabelPaint.getTextSize(), y + mAxisLabelPaint.getTextSize(), mAxisLabelPaint, anchor, angleDegrees);
        }
    }

    public void charTest(){

        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        /*chart.getDescription().setText("Variation of the temperature \n\n" +
                "and humidity over time\n");
        chart.getDescription().setTextSize(20f);
        chart.getDescription().setTextAlign(Paint.Align.CENTER);*/
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
        if(list.size() == 0)
            return;

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        ArrayList<Entry> tempValues = new ArrayList<>();
        ArrayList<Entry> humdValues = new ArrayList<>();

        ArrayList<String> hours = new ArrayList<>();
        // Init list
        int i=0;
        //Log.d("Size: ", ""+list.size());
        for(i=0; i<list.size()*4 +1; i++)
            hours.add("");
        //String[] hours = new String[]{};
        int j=0;

        //final String[] quarters = new String[] { "Q1", "Q2", "Q3", "Q4" };
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                //Log.d("Value: ", "v: "+value);
                if((int) value < list.size())
                    return hours.get((int) value);
                return "";
            }
            // we don't draw numbers, so no decimal digits needed
            //Override
            //public int getDecimalDigits() {  return 0; }
        };

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setGranularity(2f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        for (Weather w : list) {
            if(j<i)
            {
                //String log = "\n-Long: " + w.getLon() + "\n-Lat: " + w.getLat() + "\n-Date: " + w.getDate() + "\n-Humidity: " + w.getHmd() + "\n-Temperature: "+w.getTmp();
                //Log.d("\n*********************\n", log);
                //double val = (Math.random() * 100);
                float valT = w.getTmp();
                float valH = w.getHmd();
                tempValues.add(new Entry(j, valT));
                humdValues.add(new Entry(j, valH));

                // the labels that should be drawn on the XAxis
                //hours[i] = w.getDate();
                //hours.set(j, w.getDate());
                //hours.set(j, "Q"+j);
                try {
                    dateFormatter = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss", Locale.getDefault());
                    date = dateFormatter.parse(w.getDate());
                    int month = date.getMonth();
                    String mo =  new DateFormatSymbols().getMonths()[month];
                    Log.d("Time: ", ""+date.getDay()+"-"+mo.substring(0,3)+"_"+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds());
                    hours.set(j, date.getDay()+"-"+mo.substring(0,3)+"\n"+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                j++;
            }
        }

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
        chart.setXAxisRenderer(new CustomXAxisRenderer(chart.getViewPortHandler(), chart.getXAxis(), chart.getTransformer(YAxis.AxisDependency.LEFT)));
        //chart.setViewPortOffsets(0f, 0f, 0f, 0f);
        chart.animateXY(2000, 2000);
        chart.invalidate();
    }
}