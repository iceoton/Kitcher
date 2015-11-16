package com.itonlab.kitcher.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.adapter.OrderDetailListAdapter;
import com.itonlab.kitcher.database.KitcherDao;
import com.itonlab.kitcher.model.OrderDetailItem;
import com.itonlab.kitcher.util.JsonFunction;

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
                JsonFunction jsonFunction = new JsonFunction(OrderDetailActivity.this);
                jsonFunction.decideWhatToDo(JsonFunction.acceptMessage(message));
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

        Button btnServed = (Button) findViewById(R.id.btnServed);
        btnServed.setOnClickListener(btnServedOnClickListener);
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
            totalPrice += (orderDetailItem.getPrice() * orderDetailItem.getQuantity());
        }

        return totalPrice;
    }

    View.OnClickListener btnServedOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Set order in database is served.
            int orderId = getIntent().getIntExtra("ORDER_ID", 0);
            databaseDao.setOrderServed(orderId, true);
            // Close this activity
            finish();
        }
    };

}
