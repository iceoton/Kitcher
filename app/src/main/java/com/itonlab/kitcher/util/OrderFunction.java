package com.itonlab.kitcher.util;

import android.content.Context;

import com.itonlab.kitcher.database.KitcherDao;
import com.itonlab.kitcher.model.Order;
import com.itonlab.kitcher.model.OrderItem;

import java.util.ArrayList;
import java.util.Date;

public class OrderFunction {
    private Context mContext;

    public OrderFunction(Context context) {
        this.mContext = context;
    }

    public static class ClientOrderItem {
        private int id;
        private int amount;
        private String option;

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

        public String getOption() {
            return option;
        }

        public void setOption(String option) {
            this.option = option;
        }
    }

    public static class ClientOrder {
        private String name;
        private String ip;
        private int total;
        private double totalPrice;
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

        public double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
        }

        public ArrayList<ClientOrderItem> getClientOrderItems() {
            return clientOrderItems;
        }

        public void addOrderItems(ClientOrderItem item) {
            this.clientOrderItems.add(item);
        }
    }

    public Order acceptJSONOrder(String json) {
        JsonFunction jsonFunction = new JsonFunction(mContext);
        ClientOrder clientOrder = jsonFunction.getOrderFromJSON(json);
        Order order = new Order();
        order.setCustomerName(clientOrder.getName());
        order.setCustomerIP(clientOrder.getIp());
        order.setTotal(clientOrder.getTotal());
        order.setTotalPrice(clientOrder.getTotalPrice());
        Date now = new Date();
        order.setOrderTime(now);

        KitcherDao database = new KitcherDao(mContext);
        database.open();
        int orderId = database.addOrder(order);
        order.setId(orderId);
        // add all order item after add order is complete.
        for (ClientOrderItem clientOrderItem : clientOrder.getClientOrderItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderID(orderId);
            orderItem.setMenuID(clientOrderItem.getId());
            orderItem.setAmount(clientOrderItem.getAmount());
            orderItem.setOption(clientOrderItem.getOption());
            database.addOrderItem(orderItem);
        }
        database.close();

        return order;
    }

}
