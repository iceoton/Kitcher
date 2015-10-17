package com.itonlab.kitcher.util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JsonFunction {
    private Context mContext;

    public JsonFunction(Context context) {
        this.mContext = context;
    }

    public OrderFunction.ClientOrder getOrderFromJSON(String json) {
        OrderFunction.ClientOrder clientOrder = new OrderFunction.ClientOrder();
        try {
            JSONObject jsonOrder = new JSONObject(json);
            clientOrder.setName(jsonOrder.getString("name"));
            clientOrder.setIp(jsonOrder.getString("ip"));
            clientOrder.setTotal(jsonOrder.getInt("total"));
            clientOrder.setTotalPrice(jsonOrder.getDouble("total_price"));
            JSONArray jsonArrayOrderItems = jsonOrder.getJSONArray("order");
            for (int i = 0; i < jsonArrayOrderItems.length(); i++) {
                JSONObject jsonOrderItem = jsonArrayOrderItems.getJSONObject(i);
                OrderFunction.ClientOrderItem clientOrderItem = new OrderFunction.ClientOrderItem();
                clientOrderItem.setId(jsonOrderItem.getInt("id"));
                clientOrderItem.setAmount(jsonOrderItem.getInt("amount"));
                clientOrder.addOrderItems(clientOrderItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return clientOrder;
    }

}
