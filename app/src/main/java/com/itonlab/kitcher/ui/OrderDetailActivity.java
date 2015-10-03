package com.itonlab.kitcher.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.adapter.OrderDetailListAdapter;
import com.itonlab.kitcher.database.KitcherDao;
import com.itonlab.kitcher.model.OrderDetailItem;
import com.itonlab.kitcher.util.OrderFunction;

import java.util.ArrayList;

import app.akexorcist.simpletcplibrary.SimpleTCPServer;

public class OrderDetailActivity extends Activity {
    public final int TCP_PORT = 21111;
    private SimpleTCPServer server;
    ArrayList<OrderDetailItem> orderDetailItems;
    private KitcherDao databaseDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        server = new SimpleTCPServer(TCP_PORT);
        // initial TCP server
        server = new SimpleTCPServer(TCP_PORT);
        server.setOnDataReceivedListener(new SimpleTCPServer.OnDataReceivedListener() {
            public void onDataReceived(String message, String ip) {
                Log.d("JSON", message);
                OrderFunction orderFunction = new OrderFunction(OrderDetailActivity.this);
                orderFunction.acceptJSONOrder(message);
            }
        });
        databaseDao = new KitcherDao(OrderDetailActivity.this);
        databaseDao.open();

        int orderId = getIntent().getIntExtra("ORDER_ID", 0);
        orderDetailItems = databaseDao.getOrderDetail(orderId);
        ListView lvBillList = (ListView) findViewById(R.id.lvBillList);
        OrderDetailListAdapter summaryListAdapter = new OrderDetailListAdapter(OrderDetailActivity.this, orderDetailItems);
        lvBillList.setAdapter(summaryListAdapter);
        TextView tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        tvTotalPrice.setText(String.valueOf(findTotalPrice()));

    }

    @Override
    protected void onResume() {
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

    private double findTotalPrice() {
        double totalPrice = 0;
        for (OrderDetailItem orderDetailItem : orderDetailItems) {
            totalPrice += (orderDetailItem.getPrice() * orderDetailItem.getAmount());
        }

        return totalPrice;
    }

}
