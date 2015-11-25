package com.itonlab.kitcher.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.itonlab.kitcher.R;
import com.itonlab.kitcher.custom.MyValueFormatter;
import com.itonlab.kitcher.custom.MyYAxisValueFormatter;
import com.itonlab.kitcher.database.KitcherDao;
import com.itonlab.kitcher.model.MenuItem;
import com.itonlab.kitcher.util.JsonFunction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import app.akexorcist.simpletcplibrary.SimpleTCPServer;

public class HistoryFragment extends Fragment {
    public final int TCP_PORT = 21111;
    private SimpleTCPServer server;
    private KitcherDao databaseDao;

    private TextView tvTodayIncome, tvPopFoodName;
    private BarChart barChartWeek, barChartMonth;
    private Button btnDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // initial database access object
        databaseDao = new KitcherDao(getActivity());
        databaseDao.open();
        // initial TCP server
        server = new SimpleTCPServer(TCP_PORT);
        server.setOnDataReceivedListener(new SimpleTCPServer.OnDataReceivedListener() {
            public void onDataReceived(String message, String ip) {
                JsonFunction jsonFunction = new JsonFunction(getActivity());
                jsonFunction.decideWhatToDo(JsonFunction.acceptMessage(message));
            }
        });

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        tvTodayIncome = (TextView) rootView.findViewById(R.id.tvTodayIncome);
        loadIncome();
        tvPopFoodName = (TextView) rootView.findViewById(R.id.tvFoodName);
        MenuItem popMenuItem = databaseDao.getPopularFood();
        Log.d("DEBUG", "pop menu:" + popMenuItem.getNameThai());
        tvPopFoodName.setText(popMenuItem.getNameThai());

        barChartWeek = (BarChart) rootView.findViewById(R.id.chartWeek);
        barChartWeek.setDescription("");
        barChartWeek.setDrawValueAboveBar(true);
        barChartWeek.setTouchEnabled(false); // ทำให้ขยายยืดหรือหดไม่ได้
        barChartWeek.setDrawBarShadow(false);
        barChartWeek.setDrawGridBackground(false);
        loadBarChartWeek();

        barChartMonth = (BarChart) rootView.findViewById(R.id.chartMonth);
        barChartMonth.setDescription("");
        barChartMonth.setDrawValueAboveBar(true);
        barChartMonth.setTouchEnabled(false);
        barChartMonth.setDrawBarShadow(false);
        barChartMonth.setDrawGridBackground(false);
        loadBarChartMonth();

        btnDetail = (Button) rootView.findViewById(R.id.btnDetail);
        btnDetail.setOnClickListener(btnDetailListener);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        server.start();
        databaseDao.open();
    }

    @Override
    public void onStop() {
        super.onStop();
        server.stop();
        databaseDao.close();
    }

    private void loadIncome() {
        double todayIncome = databaseDao.getDayIncome(new Date());
        tvTodayIncome.setText(String.valueOf(todayIncome) + " บาท");
    }


    private void loadBarChartWeek() {
        XAxis xAxis = barChartWeek.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(0);
        xAxis.setDrawGridLines(false);


        YAxisValueFormatter custom = new MyYAxisValueFormatter();
        YAxis leftAxis = barChartWeek.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawLabels(false);
        /*leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);*/

        YAxis rightAxis = barChartWeek.getAxisRight();
        rightAxis.setEnabled(false);

        // add a nice and smooth animation
        barChartWeek.animateY(2500);

        barChartWeek.getLegend().setEnabled(false);
        /*Legend l = barChartWeek.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);*/

        final int[] WEEK_COLORS = {
                Color.rgb(229, 20, 0), Color.rgb(227, 200, 0), Color.rgb(244, 114, 208),
                Color.rgb(96, 169, 23), Color.rgb(250, 104, 0), Color.rgb(27, 161, 226),
                Color.rgb(170, 0, 155)
        };

        Calendar calendar = new GregorianCalendar();
        // for debugging
        /*int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        Log.d("DEBUG", "Day of week = " + dayOfWeek);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String day = dateFormat.format(calendar.getTime());
        Log.d("DEBUG", "Day of week = " + day);*/

        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
        for (int i = 0; i < 7; i++) {
            calendar.set(Calendar.DAY_OF_WEEK, (i + 1));
            yValues.add(new BarEntry((float) databaseDao.getDayIncome(calendar.getTime()), i));
        }

        ArrayList<String> xValues = new ArrayList<String>();
        xValues.add("อาทิตย์");
        xValues.add("จันทร์");
        xValues.add("อังคาร");
        xValues.add("พุธ");
        xValues.add("พฤหัสบดี");
        xValues.add("ศุกร์");
        xValues.add("เสาร์");

        // ชุดข้อมูลที่จะเอาไปแสดงในแกน y
        BarDataSet barDataSet = new BarDataSet(yValues, "Data Set");
        barDataSet.setColors(WEEK_COLORS);
        barDataSet.setDrawValues(true);
        barDataSet.setValueFormatter(new MyValueFormatter());
        // รวมชุดข้อมูลของแกน y ก่อน (สามารถมีข้อมูลหลายชุดได้)
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(barDataSet);
        // ข้อมูลที่จะเอาไปแสดงทั้งแกน x และ y
        BarData data = new BarData(xValues, dataSets);

        barChartWeek.setData(data);
        barChartWeek.invalidate();
    }

    private void loadBarChartMonth() {
        XAxis xAxis = barChartMonth.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(0);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = barChartMonth.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawLabels(false);

        YAxis rightAxis = barChartMonth.getAxisRight();
        rightAxis.setEnabled(false);
        // add a nice and smooth animation
        barChartMonth.animateY(2500);
        barChartMonth.getLegend().setEnabled(false);

        Calendar calendar = new GregorianCalendar();
        // for debugging
        /*int monthOfYear= calendar.get(Calendar.MONTH);
        Log.d("DEBUG", "Month of year = " + monthOfYear);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String month = dateFormat.format(calendar.getTime());
        Log.d("DEBUG", "Month of year = " + month);*/

        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
        for (int i = 0; i < 12; i++) {
            calendar.set(Calendar.MONTH, i);
            yValues.add(new BarEntry((float) databaseDao.getMonthIncome(calendar.getTime()), i));
        }

        ArrayList<String> xValues = new ArrayList<String>();
        xValues.add("ม.ค.");
        xValues.add("ก.พ.");
        xValues.add("มี.ค.");
        xValues.add("เม.ย.");
        xValues.add("พ.ค.");
        xValues.add("มิ.ย.");
        xValues.add("ก.ค.");
        xValues.add("ส.ค.");
        xValues.add("ก.ย.");
        xValues.add("ต.ค.");
        xValues.add("พ.ย.");
        xValues.add("ธ.ค.");

        // ชุดข้อมูลที่จะเอาไปแสดงในแกน y
        BarDataSet barDataSet = new BarDataSet(yValues, "Data Set");
        barDataSet.setDrawValues(true);
        barDataSet.setValueFormatter(new MyValueFormatter());
        // รวมชุดข้อมูลของแกน y ก่อน (สามารถมีข้อมูลหลายชุดได้)
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(barDataSet);
        // ข้อมูลที่จะเอาไปแสดงทั้งแกน x และ y
        BarData data = new BarData(xValues, dataSets);

        barChartMonth.setData(data);
        barChartMonth.invalidate();
    }

    View.OnClickListener btnDetailListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Fragment fragment = new HistoryDetailFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

        }
    };
}
