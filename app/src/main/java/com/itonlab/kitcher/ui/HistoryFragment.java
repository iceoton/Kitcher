package com.itonlab.kitcher.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.itonlab.kitcher.util.OrderFunction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import app.akexorcist.simpletcplibrary.SimpleTCPServer;

public class HistoryFragment extends Fragment {
    public final int TCP_PORT = 21111;
    private SimpleTCPServer server;
    private KitcherDao databaseDao;

    private TextView tvTodayIncome, tvMonthIncome, tvPopFoodName;
    private BarChart barChartWeek;
    private Button btnDetail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // initial database access object
        databaseDao = new KitcherDao(getActivity());
        databaseDao.open();
        // initial TCP server
        server = new SimpleTCPServer(TCP_PORT);
        server.setOnDataReceivedListener(new SimpleTCPServer.OnDataReceivedListener() {
            public void onDataReceived(String message, String ip) {
                Log.d("JSON", message);
                OrderFunction orderFunction = new OrderFunction(getActivity());
                orderFunction.acceptJSONOrder(message);
            }
        });

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        tvTodayIncome = (TextView) rootView.findViewById(R.id.tvTodayIncome);
        tvMonthIncome = (TextView) rootView.findViewById(R.id.tvMonthIncome);
        loadIncome();
        tvPopFoodName = (TextView) rootView.findViewById(R.id.tvFoodName);
        MenuItem popMenuItem = databaseDao.getPopularFood();
        Log.d("DEBUG", "pop menu:" + popMenuItem.getNameThai());
        tvPopFoodName.setText(popMenuItem.getNameThai());

        barChartWeek = (BarChart) rootView.findViewById(R.id.chartWeek);
        barChartWeek.setDescription("");
        barChartWeek.setDrawValueAboveBar(true);
        // ทำให้ขยายยืดหรือหดไม่ได้
        barChartWeek.setTouchEnabled(false);

        barChartWeek.setDrawBarShadow(false);
        barChartWeek.setDrawGridBackground(false);
        loadBarChartWeek();

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
        double monthIncome = databaseDao.getMonthIncome(new Date());
        tvTodayIncome.setText(String.valueOf(todayIncome) + " บาท");
        tvMonthIncome.setText(String.valueOf(monthIncome) + " บาท");
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
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        Log.d("DEBUG", "Day of week = " + dayOfWeek);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String day = dateFormat.format(calendar.getTime());
        Log.d("DEBUG", "Day of week = " + day);

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
