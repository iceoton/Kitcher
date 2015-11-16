package com.itonlab.kitcher.model;

public class OrderTable {
    public static final String TABLE_NAME = "'order'";

    public static class Columns {
        public Columns() {
        }

        public static final String _ID = "id";
        public static final String _CODE = "code";
        public static final String _CUSTOMER_NAME = "customer_name";
        public static final String _CUSTOMER_IP = "customer_ip";
        /**
         * Total quantity of order_item in order
         */
        public static final String _TOTAL_QUANTITY = "total_quantity";
        public static final String _TOTAL_PRICE = "total_price";
        public static final String _ORDER_TIME = "order_time";
        public static final String _SERVED = "served";
    }
}
