package com.itonlab.kitcher.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.adapter.OrderDetailListAdapter;
import com.itonlab.kitcher.database.KitcherDao;
import com.itonlab.kitcher.model.Order;
import com.itonlab.kitcher.model.OrderDetailItem;
import com.itonlab.kitcher.model.OrderTable;
import com.itonlab.kitcher.util.JsonFunction;

import java.util.ArrayList;

import app.akexorcist.simpletcplibrary.SimpleTCPClient;
import app.akexorcist.simpletcplibrary.SimpleTCPServer;

public class OrderDetailActivity extends Activity {
    public final int TCP_PORT = 21111;
    private SimpleTCPServer server;
    ArrayList<OrderDetailItem> orderDetailItems;
    private KitcherDao databaseDao;
    private int orderId;

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

        orderId = getIntent().getIntExtra("ORDER_ID", 0);

        orderDetailItems = databaseDao.getOrderDetail(orderId);
        ListView lvBillList = (ListView) findViewById(R.id.lvBillList);
        OrderDetailListAdapter summaryListAdapter = new OrderDetailListAdapter(OrderDetailActivity.this, orderDetailItems);
        lvBillList.setAdapter(summaryListAdapter);
        TextView tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        tvTotalPrice.setText(String.valueOf(findTotalPrice()));

        TextView tvTake = (TextView) findViewById(R.id.textViewTake);
        tvTake.setText(getIntent().getStringExtra(OrderTable.Columns._TAKE));

        Button btnPay = (Button) findViewById(R.id.btnPay);
        btnPay.setOnClickListener(btnPayOnClickListener);
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

    View.OnClickListener btnPayOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // set message to customer
            JsonFunction jsonFunction = new JsonFunction(OrderDetailActivity.this);
            String json = jsonFunction.getJSONPayConfirmMessage();
            Log.d("JSON", json);
            Order order = databaseDao.getOrderAtID(orderId);
            SimpleTCPClient.send(json, order.getCustomerIP(), TCP_PORT, new SimpleTCPClient.SendCallback() {
                @Override
                public void onSuccess(String tag) {

                }

                @Override
                public void onFailed(String tag) {
                    AlertDialog alertDialog = new AlertDialog.Builder(OrderDetailActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage(getString(R.string.send_pay_confirm_failed));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }, "PAY_CONFIRM");

            // Set order in database is served.
            int orderId = getIntent().getIntExtra("ORDER_ID", 0);
            databaseDao.setOrderServed(orderId, true);
            // Close this activity
            finish();
        }
    };

}
