package com.itonlab.kitcher.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.adapter.OrderDetailListAdapter;
import com.itonlab.kitcher.database.KitcherDao;
import com.itonlab.kitcher.model.Order;
import com.itonlab.kitcher.model.OrderDetailItem;
import com.itonlab.kitcher.model.OrderItem;
import com.itonlab.kitcher.model.OrderItemTable;
import com.itonlab.kitcher.model.OrderTable;
import com.itonlab.kitcher.util.JsonFunction;

import java.util.ArrayList;

import app.akexorcist.simpletcplibrary.SimpleTCPClient;
import app.akexorcist.simpletcplibrary.SimpleTCPServer;

public class OrderDetailActivity extends Activity {
    public final int TCP_PORT = 21111;
    private SimpleTCPServer server;
    ArrayList<OrderDetailItem> orderDetailItems;
    ArrayList<OrderItem> orderItems;
    OrderDetailListAdapter orderDetailListAdapter;
    private KitcherDao databaseDao;
    private int orderId;
    private Order order;
    private ListView lvBillList;
    private JsonFunction jsonFunction;

    private void initialVariable() {
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

        jsonFunction = new JsonFunction(OrderDetailActivity.this);

        orderId = getIntent().getIntExtra("ORDER_ID", 0);
        orderDetailItems = databaseDao.getOrderDetail(orderId);
        orderItems = databaseDao.getOrderItem(orderId);
        order = databaseDao.getOrderAtID(orderId);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        // Let's do this first of all.
        initialVariable();

        lvBillList = (ListView) findViewById(R.id.lvBillList);
        orderDetailListAdapter = new OrderDetailListAdapter(OrderDetailActivity.this, orderDetailItems);
        lvBillList.setAdapter(orderDetailListAdapter);
        lvBillList.setOnItemClickListener(onOrderItemClickListener);

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

    AdapterView.OnItemClickListener onOrderItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            if (!view.isEnabled()) {
                return;
            }

            final Dialog dialogOrderItem = new Dialog(OrderDetailActivity.this);
            dialogOrderItem.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogOrderItem.setCancelable(true);
            dialogOrderItem.setContentView(R.layout.dialog_order_item);
            dialogOrderItem.show();

            final OrderItem orderItem = orderItems.get(position);

            Button btnDone = (Button) dialogOrderItem.findViewById(R.id.btnDone);
            btnDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderDetailItems.get(position).setStatus(OrderItem.Status.DONE);
                    orderItem.setStatus(OrderItem.Status.DONE);
                    String json = jsonFunction.getJSONOrderStatusMessage(orderItem);
                    Log.d("JSON", json);
                    ContentValues values = new ContentValues();
                    values.put(OrderItemTable.Columns._STATUS, OrderItem.Status.DONE.getValue());
                    databaseDao.updateOrderItemByValue(orderItem.getId(), values);
                    sendUpdateStatus(json);
                    dialogOrderItem.dismiss();
                    updateListViewOrderItem(position, orderItem);
                }
            });

            Button btnServed = (Button) dialogOrderItem.findViewById(R.id.btnServed);
            btnServed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderDetailItems.get(position).setStatus(OrderItem.Status.DONE);
                    orderDetailItems.get(position).setServed(true);
                    orderItem.setStatus(OrderItem.Status.DONE);
                    orderItem.setServed(true);
                    String json = jsonFunction.getJSONOrderStatusMessage(orderItem);
                    Log.d("JSON", json);
                    ContentValues values = new ContentValues();
                    values.put(OrderItemTable.Columns._STATUS, OrderItem.Status.DONE.getValue());
                    values.put(OrderItemTable.Columns._SERVED, 1);
                    databaseDao.updateOrderItemByValue(orderItem.getId(), values);
                    sendUpdateStatus(json);
                    dialogOrderItem.dismiss();
                    updateListViewOrderItem(position, orderItem);
                }
            });

            Button btnEdit = (Button) dialogOrderItem.findViewById(R.id.btnEdit);
        }
    };

    private void updateListViewOrderItem(int position, OrderItem orderItem) {
        // Remove from item list. However, it will add back later.
        orderItems.remove(position);
        OrderDetailItem tmpOrderDetailItem = orderDetailItems.get(position);
        orderDetailItems.remove(position);
        // Re-add to item list.
        orderItems.add(orderItem);
        orderDetailItems.add(position, tmpOrderDetailItem);
        orderDetailListAdapter.notifyDataSetChanged();
    }

    View.OnClickListener btnPayOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // set message to customer
            String json = jsonFunction.getJSONPayConfirmMessage();
            Log.d("JSON", json);
            SimpleTCPClient.send(json, order.getCustomerIP(), TCP_PORT, new SimpleTCPClient.SendCallback() {
                @Override
                public void onSuccess(String tag) {

                }

                @Override
                public void onFailed(String tag) {
                    AlertDialog alertDialog = new AlertDialog.Builder(OrderDetailActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage(getString(R.string.send_message_failed));
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

    private void sendUpdateStatus(String json) {
        SimpleTCPClient.send(json, order.getCustomerIP(), TCP_PORT, new SimpleTCPClient.SendCallback() {
            @Override
            public void onSuccess(String tag) {

            }

            @Override
            public void onFailed(String tag) {
                AlertDialog alertDialog = new AlertDialog.Builder(OrderDetailActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage(getString(R.string.send_message_failed));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }, "UPDATE_ORDER");
    }

}
