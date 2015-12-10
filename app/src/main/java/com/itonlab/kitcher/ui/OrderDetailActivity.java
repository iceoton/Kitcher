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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.adapter.OrderDetailListAdapter;
import com.itonlab.kitcher.database.KitcherDao;
import com.itonlab.kitcher.model.MenuItem;
import com.itonlab.kitcher.model.Order;
import com.itonlab.kitcher.model.OrderItem;
import com.itonlab.kitcher.model.OrderItemDetail;
import com.itonlab.kitcher.model.OrderItemTable;
import com.itonlab.kitcher.model.OrderTable;
import com.itonlab.kitcher.model.Picture;
import com.itonlab.kitcher.util.JsonFunction;

import java.util.ArrayList;

import app.akexorcist.simpletcplibrary.SimpleTCPClient;
import app.akexorcist.simpletcplibrary.SimpleTCPServer;

public class OrderDetailActivity extends Activity {
    public final int TCP_PORT = 21111;
    private SimpleTCPServer server;
    ArrayList<OrderItemDetail> orderItemDetails;
    ArrayList<OrderItem> orderItems;
    OrderDetailListAdapter orderDetailListAdapter;
    private KitcherDao databaseDao;
    private Order order;
    private ListView lvBillList;
    private JsonFunction jsonFunction;
    private TextView tvTotalPrice;
    private int orderId;

    private void initialVariable() {
        server = new SimpleTCPServer(TCP_PORT);
        // initial TCP server
        server = new SimpleTCPServer(TCP_PORT);
        server.setOnDataReceivedListener(new SimpleTCPServer.OnDataReceivedListener() {
            public void onDataReceived(String message, String ip) {
                JsonFunction jsonFunction = new JsonFunction(OrderDetailActivity.this);
                JsonFunction.Message jsonMessage = JsonFunction.acceptMessage(message);

                if (jsonMessage.getMessageType().equals(JsonFunction.Message.Type.ORDER_MESSAGE)) {
                    Order comeOrder = jsonFunction.acceptOrderFromClient(jsonMessage);
                    if (comeOrder.getId() == orderId) {
                        // re-load data
                        orderItemDetails = databaseDao.getOrderDetail(orderId);
                        orderItems = databaseDao.getOrderItem(orderId);
                        order = comeOrder;
                        orderDetailListAdapter = new OrderDetailListAdapter(OrderDetailActivity.this, orderItemDetails);
                        lvBillList.setAdapter(orderDetailListAdapter);
                        tvTotalPrice.setText(String.valueOf(order.getTotalPrice()));
                    }
                }
            }
        });
        databaseDao = new KitcherDao(OrderDetailActivity.this);
        databaseDao.open();

        jsonFunction = new JsonFunction(OrderDetailActivity.this);

        orderId = getIntent().getIntExtra("ORDER_ID", 0);
        orderItemDetails = databaseDao.getOrderDetail(orderId);
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
        orderDetailListAdapter = new OrderDetailListAdapter(OrderDetailActivity.this, orderItemDetails);
        lvBillList.setAdapter(orderDetailListAdapter);
        lvBillList.setOnItemClickListener(onOrderItemClickListener);

        tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        tvTotalPrice.setText(String.valueOf(order.getTotalPrice()));

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
        for (OrderItemDetail orderItemDetail : orderItemDetails) {
            totalPrice += (orderItemDetail.getPrice() * orderItemDetail.getQuantity());
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
                    orderItemDetails.get(position).setStatus(OrderItem.Status.DONE);
                    orderItem.setStatus(OrderItem.Status.DONE);
                    sendUpdateStatus(position, orderItem);
                    dialogOrderItem.dismiss();
                }
            });

            Button btnServed = (Button) dialogOrderItem.findViewById(R.id.btnServed);
            btnServed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderItemDetails.get(position).setStatus(OrderItem.Status.DONE);
                    orderItemDetails.get(position).setServed(true);
                    orderItem.setStatus(OrderItem.Status.DONE);
                    orderItem.setServed(true);
                    sendUpdateStatus(position, orderItem);
                    dialogOrderItem.dismiss();
                }
            });

            Button btnEdit = (Button) dialogOrderItem.findViewById(R.id.btnEdit);
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogEditOrderItem(position);
                    dialogOrderItem.dismiss();
                }
            });
        }
    };

    private void updateListViewOrderItem(int position, OrderItem orderItem) {
        // Remove from item list. However, it will add back later.
        orderItems.remove(position);
        OrderItemDetail tmpOrderItemDetail = orderItemDetails.get(position);
        orderItemDetails.remove(position);
        // Re-add to item list.
        orderItems.add(position, orderItem);
        orderItemDetails.add(position, tmpOrderItemDetail);
        orderDetailListAdapter.notifyDataSetChanged();
    }

    View.OnClickListener btnPayOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
            builder.setMessage(R.string.text_confirm_payment)
                    .setPositiveButton("ยืนยัน", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            paymentAndCheckout();
                        }
                    })
                    .setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            // Create the AlertDialog object
            AlertDialog dialog = builder.create();
            dialog.show();


        }
    };

    private void paymentAndCheckout() {
        // set message to customer
        String json = jsonFunction.getJSONPayConfirmMessage();
        Log.d("JSON", json);
        SimpleTCPClient.send(json, order.getCustomerIP(), TCP_PORT, new SimpleTCPClient.SendCallback() {
            @Override
            public void onSuccess(String tag) {
                // Set order in database is served.
                int orderId = getIntent().getIntExtra("ORDER_ID", 0);
                databaseDao.setOrderServed(orderId, true);
                // Close this activity
                finish();
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
    }

    private void sendUpdateStatus(final int position, final OrderItem orderItem) {
        String json = jsonFunction.getJSONOrderStatusMessage(orderItem);
        Log.d("JSON", json);
        SimpleTCPClient.send(json, order.getCustomerIP(), TCP_PORT, new SimpleTCPClient.SendCallback() {
            @Override
            public void onSuccess(String tag) {
                ContentValues values = new ContentValues();
                values.put(OrderItemTable.Columns._STATUS, orderItem.getStatus().getValue());
                values.put(OrderItemTable.Columns._SERVED, orderItem.isServed() ? 1 : 0);
                databaseDao.updateOrderItemByValue(orderItem.getId(), values);
                updateListViewOrderItem(position, orderItem);

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

    private void showDialogEditOrderItem(final int itemPosition) {
        final OrderItem orderItem = orderItems.get(itemPosition);

        final Dialog dialogEditSummary = new Dialog(OrderDetailActivity.this);
        dialogEditSummary.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogEditSummary.setCancelable(true);
        dialogEditSummary.setContentView(R.layout.dialog_edit_order_item);
        // show detail of food by menu code
        MenuItem menuItem = databaseDao.getMenuByCode(orderItem.getMenuCode());

        TextView tvName = (TextView) dialogEditSummary.findViewById(R.id.tvName);
        tvName.setText(menuItem.getNameThai());

        final TextView tvPrice = (TextView) dialogEditSummary.findViewById(R.id.tvPrice);
        tvPrice.setText(Double.toString(menuItem.getPrice()));

        final EditText etOption = (EditText) dialogEditSummary.findViewById(R.id.editTextOption);
        etOption.setText(orderItem.getOption());

        ImageView ivImgFood = (ImageView) dialogEditSummary.findViewById(R.id.ivImgFood);
        Picture picture = databaseDao.getMenuPicture(menuItem.getPictureId());
        ivImgFood.setImageBitmap(picture.getBitmapPicture());

        dialogEditSummary.show();

        //preparing to find new total quantity
        order.setTotalQuantity(order.getTotalQuantity() - orderItem.getQuantity());

        final EditText etAmount = (EditText) dialogEditSummary.findViewById(R.id.etAmount);
        etAmount.setText(String.valueOf(orderItem.getQuantity()));
        Button btnOK = (Button) dialogEditSummary.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int amount = Integer.parseInt(etAmount.getText().toString());
                String option = etOption.getText().toString();
                // In case the user entering zero or negative, skip updating.
                if (amount > 0) {
                    orderItemDetails.get(itemPosition).setQuantity(amount);
                    orderItemDetails.get(itemPosition).setOption(option);
                    orderItem.setQuantity(amount);
                    orderItem.setOption(option);
                    // update data in database
                    final ContentValues values = new ContentValues();
                    values.put(OrderItemTable.Columns._QUANTITY, amount);
                    values.put(OrderItemTable.Columns._OPTION, option);
                    //send message to customer
                    String json = jsonFunction.getJSONEditOrderMessage(orderItem);
                    SimpleTCPClient.send(json, order.getCustomerIP(), TCP_PORT, new SimpleTCPClient.SendCallback() {
                        @Override
                        public void onSuccess(String tag) {
                            updateListViewOrderItem(itemPosition, orderItem);
                            //update data in database
                            databaseDao.updateOrderItemByValue(orderItem.getId(), values);
                            // change total quantity and total price of order
                            ContentValues orderValues = new ContentValues();
                            int newQuantity = order.getTotalQuantity() + orderItem.getQuantity();
                            double newPrice = findTotalPrice();
                            orderValues.put(OrderTable.Columns._TOTAL_QUANTITY, newQuantity);
                            orderValues.put(OrderTable.Columns._TOTAL_PRICE, newPrice);
                            databaseDao.updateOrderByValue(order.getId(), orderValues);
                            // update order object and view
                            order.setTotalQuantity(newQuantity);
                            order.setTotalPrice(newPrice);
                            tvTotalPrice.setText(String.valueOf(newPrice));
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
                    }, "EDIT_ORDER");
                }

                dialogEditSummary.dismiss();
            }
        });

        dialogEditSummary.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // do not anything.
            }
        });
    }
}
