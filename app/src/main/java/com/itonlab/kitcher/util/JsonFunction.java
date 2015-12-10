package com.itonlab.kitcher.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.itonlab.kitcher.MainActivity;
import com.itonlab.kitcher.R;
import com.itonlab.kitcher.database.KitcherDao;
import com.itonlab.kitcher.model.Order;
import com.itonlab.kitcher.model.OrderItem;
import com.itonlab.kitcher.model.OrderItemTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import app.akexorcist.simpletcplibrary.TCPUtils;


public class JsonFunction {
    private Context mContext;

    public static class Message {
        public enum Type {
            ORDER_MESSAGE("order_ms"),
            SYNC_DATA_MESSAGE("sync_data_ms");

            Type(String key) {
                this.jsonKey = key;
            }

            public String getJsonKey() {
                return this.jsonKey;
            }

            private String jsonKey;
        }

        private Type messageType;
        private String fromIP;
        private JSONObject jsonBody;

        public Type getMessageType() {
            return messageType;
        }

        public void setMessageType(String messageTypeKey) {
            try {
                if (messageTypeKey.equals(Type.ORDER_MESSAGE.getJsonKey())) {
                    this.messageType = Type.ORDER_MESSAGE;
                } else if (messageTypeKey.equals(Type.SYNC_DATA_MESSAGE.getJsonKey())) {
                    this.messageType = Type.SYNC_DATA_MESSAGE;
                } else {
                    throw new JSONException("Message type key can't accepted.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getFromIP() {
            return fromIP;
        }

        public void setFromIP(String fromIP) {
            this.fromIP = fromIP;
        }

        public JSONObject getJsonBody() {
            return jsonBody;
        }

        public void setJsonBody(JSONObject jsonBody) {
            this.jsonBody = jsonBody;
        }
    }

    public JsonFunction(Context context) {
        this.mContext = context;
    }

    public static Message acceptMessage(String json) {
        Log.d("JSON", json);
        Message message = new Message();
        try {
            JSONObject jsonObject = new JSONObject(json);

            message.setMessageType(jsonObject.getString("message_type"));
            message.setFromIP(jsonObject.getString("from_ip"));
            message.setJsonBody(jsonObject.getJSONObject("body"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message;
    }

    public void decideWhatToDo(Message message) {
        switch (message.getMessageType()) {
            case ORDER_MESSAGE:
                acceptOrderFromClient(message);
                break;
            case SYNC_DATA_MESSAGE:
                acceptSyncDataRequest(message);
                break;
            default:
                Log.d("JSON", "Do nothing");
        }
    }

    public Order acceptOrderFromClient(Message message) {
        Order order = new Order();
        JSONObject body = message.getJsonBody();
        try {
            order.setCustomerName(body.getString("name"));
            order.setCustomerIP(message.getFromIP());
            order.setTotalQuantity(body.getInt("total_quantity"));
            order.setTotalPrice(body.getDouble("total_price"));
            order.setTakeByValue(body.getInt("take"));
            Date now = new Date();
            order.setOrderTime(now);

            KitcherDao database = new KitcherDao(mContext);
            database.open();

            order = database.addOrder(order);

            JSONArray jsonArrayOrderItems = body.getJSONArray("order");
            for (int i = 0; i < jsonArrayOrderItems.length(); i++) {
                JSONObject jsonOrderItem = jsonArrayOrderItems.getJSONObject(i);
                // add all order item after add order is complete.
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderID(order.getId());
                orderItem.setPreId(jsonOrderItem.getInt("pre_id"));
                orderItem.setMenuCode(jsonOrderItem.getString("menu_code"));
                orderItem.setQuantity(jsonOrderItem.getInt("quantity"));
                orderItem.setOption(jsonOrderItem.getString("option"));
                database.addOrderItem(orderItem);
            }
            database.close();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Food Ordering")
                        .setContentText("มีการสั่งอาหารจาก " + order.getCustomerName());
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mContext, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder.setAutoCancel(true);
        Notification notification = mBuilder.build();
        notification.defaults = Notification.DEFAULT_ALL;
        // Id allows you to update the notification later on.
        mNotificationManager.notify(order.getId(), notification);

        return order;
    }

    private void acceptSyncDataRequest(Message message) {
        /*if (message.getMessageType().equals(Message.Type.SYNC_DATA_MESSAGE)) {
            //read file from storage

            //send it to client
            final int TCP_PORT = 21111;
            SimpleTCPClient.send("data file", message.getFromIP(), TCP_PORT);
        }*/
    }

    public String getJSONPayConfirmMessage() {
        JSONObject message = new JSONObject();
        try {
            message.put("message_type", "pay_confirm_ms");
            message.put("from_ip", TCPUtils.getIP(mContext));
            //prepare body for add to message
            JSONObject messageBody = new JSONObject();
            // add body to message
            message.put("body", messageBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message.toString();
    }

    public String getJSONOrderStatusMessage(OrderItem orderItem) {
        JSONObject message = new JSONObject();
        try {
            message.put("message_type", "order_status_ms");
            message.put("from_ip", TCPUtils.getIP(mContext));
            //prepare body for add to message
            JSONObject messageBody = new JSONObject();
            messageBody.put(OrderItemTable.Columns._PRE_ID, orderItem.getPreId());
            int servedValue = orderItem.isServed() ? 1 : 0;
            messageBody.put(OrderItemTable.Columns._SERVED, servedValue);
            messageBody.put(OrderItemTable.Columns._STATUS, orderItem.getStatus().getValue());

            // add body to message
            message.put("body", messageBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message.toString();
    }

    public String getJSONEditOrderMessage(OrderItem orderItem) {
        JSONObject message = new JSONObject();
        try {
            message.put("message_type", "edit_order_ms");
            message.put("from_ip", TCPUtils.getIP(mContext));
            //prepare body for add to message
            JSONObject messageBody = new JSONObject();
            messageBody.put(OrderItemTable.Columns._PRE_ID, orderItem.getPreId());
            messageBody.put(OrderItemTable.Columns._QUANTITY, orderItem.getQuantity());
            messageBody.put(OrderItemTable.Columns._OPTION, orderItem.getOption());

            // add body to message
            message.put("body", messageBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message.toString();
    }

    public String getJSONDeleteOrderItem(int preOrderItemId) {
        JSONObject message = new JSONObject();

        return message.toString();
    }

}
