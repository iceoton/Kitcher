package com.itonlab.kitcher.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.adapter.OrderDetailListAdapter;
import com.itonlab.kitcher.database.KitcherDao;
import com.itonlab.kitcher.model.OrderDetailItem;
import com.itonlab.kitcher.model.OrderTable;
import com.itonlab.kitcher.util.JsonFunction;

import java.util.ArrayList;

import app.akexorcist.simpletcplibrary.SimpleTCPServer;


public class HistoryOrderDetailActivity extends Activity {
    public final int TCP_PORT = 21111;
    private SimpleTCPServer server;
    private int orderId;
    private String orderTime;
    private KitcherDao databaseDao;
    private TextView tvOrderId, tvOrderTime, tvTotalPrice;
    private ListView lvOrderItem;
    private OrderDetailListAdapter orderDetailListAdapter;
    private ArrayList<OrderDetailItem> orderDetailItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_order_detail);

        databaseDao = new KitcherDao(HistoryOrderDetailActivity.this);
        databaseDao.open();

        // initial TCP server
        server = new SimpleTCPServer(TCP_PORT);
        server.setOnDataReceivedListener(new SimpleTCPServer.OnDataReceivedListener() {
            public void onDataReceived(String message, String ip) {
                JsonFunction jsonFunction = new JsonFunction(getApplicationContext());
                jsonFunction.decideWhatToDo(JsonFunction.acceptMessage(message));
            }
        });

        orderId = getIntent().getIntExtra(OrderTable.Columns._ID, 0);
        orderTime = getIntent().getStringExtra(OrderTable.Columns._ORDER_TIME);

        tvOrderId = (TextView) findViewById(R.id.tvOrderNumber);
        tvOrderId.setText(String.valueOf(orderId));
        tvOrderTime = (TextView) findViewById(R.id.tvOrderTime);
        tvOrderTime.setText(orderTime);

        orderDetailItems = databaseDao.getOrderDetail(orderId);
        orderDetailListAdapter = new OrderDetailListAdapter(HistoryOrderDetailActivity.this, orderDetailItems);
        lvOrderItem = (ListView) findViewById(R.id.lvOrderItem);
        lvOrderItem.setAdapter(orderDetailListAdapter);

        tvTotalPrice = (TextView)findViewById(R.id.tvTotalPrice);
        tvTotalPrice.setText(String.valueOf(findTotalPrice()));

    }

    @Override
    protected void onResume() {
        super.onResume();
        server.start();
        databaseDao.open();
    }

    @Override
    protected void onStop() {
        super.onStop();
        server.stop();
        databaseDao.close();
    }

    private double findTotalPrice(){
        double totalPrice = 0;
        for(OrderDetailItem orderDetailItem : orderDetailItems){
            totalPrice += (orderDetailItem.getPrice() * orderDetailItem.getAmount());
        }

        return totalPrice;
    }
}
