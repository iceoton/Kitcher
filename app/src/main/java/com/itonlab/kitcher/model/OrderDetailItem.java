package com.itonlab.kitcher.model;

/**
 * คลาสสำหรับใช้เป็น model แสดงข้อมูลในหน้า Order Detail (หน้าแสดง order item ที่ลูกค้าสั่ง)
 */
public class OrderDetailItem {
    private String menuCode;
    private String name;
    private double price;
    private int quantity;
    private String option;

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
