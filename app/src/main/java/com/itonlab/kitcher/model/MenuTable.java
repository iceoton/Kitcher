package com.itonlab.kitcher.model;

public class MenuTable {
    public static final String TABLE_NAME = "menu";

    public static class Columns{
        public Columns(){}

        public static final String _ID = "id";
        public static final String _CODE = "code";
        public static final String _NAME_THAI = "name_th";
        public static final String _NAME_ENG = "name_en";
        public static final String _PRICE = "price";
        public static final String _IMAGE_PATH = "img";
    }
}
