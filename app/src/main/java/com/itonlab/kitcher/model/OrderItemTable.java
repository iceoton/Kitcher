package com.itonlab.kitcher.model;

public class OrderItemTable {
    public static final String TABLE_NAME = "order_item";

    public static class Columns {
        public Columns() {
        }

        public static final String _ID = "id";
        public static final String _ORDER_ID = "order_id";
        public static final String _MENU_ID = "menu_id";
        public static final String _AMOUNT = "amount";
        public static final String _OPTION = "option";
        public static final String _SERVED = "served";
    }
}
