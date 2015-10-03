package com.itonlab.kitcher.util;

import android.content.Context;

import com.itonlab.kitcher.database.KitcherDao;
import com.itonlab.kitcher.model.FoodOrder;
import com.itonlab.kitcher.model.FoodOrderItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class OrderFunction {
    private Context mContext;

    public OrderFunction(Context context) {
        this.mContext = context;
    }

    public static class ClientOrderItem {
        private int id;
        private int amount;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }

    public static class ClientOrder {
        private String name;
        private String ip;
        private int total;
        private ArrayList<ClientOrderItem> clientOrderItems = new ArrayList<ClientOrderItem>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public ArrayList<ClientOrderItem> getClientOrderItems() {
            return clientOrderItems;
        }

        public void addOrderItems(ClientOrderItem item) {
            this.clientOrderItems.add(item);
        }
    }

    public FoodOrder acceptJSONOrder(String json) {
        JsonFunction jsonFunction = new JsonFunction(mContext);
        ClientOrder clientOrder = jsonFunction.getOrderFromJSON(json);
        FoodOrder foodOrder = new FoodOrder();
        foodOrder.setCustomerName(clientOrder.getName());
        foodOrder.setCustomerIP(clientOrder.getIp());
        foodOrder.setTotal(clientOrder.getTotal());
        Date now = new Date();
        foodOrder.setOrderTime(now);

        KitcherDao database = new KitcherDao(mContext);
        database.open();
        int orderId = database.addOrder(foodOrder);
        foodOrder.setId(orderId);
        // add all order item after add order is complete.
        for (ClientOrderItem clientOrderItem : clientOrder.getClientOrderItems()) {
            FoodOrderItem foodOrderItem = new FoodOrderItem();
            foodOrderItem.setOrderID(orderId);
            foodOrderItem.setMenuID(clientOrderItem.getId());
            foodOrderItem.setAmount(clientOrderItem.getAmount());
            database.addOrderItem(foodOrderItem);
        }
        database.close();

        return foodOrder;
    }

}
