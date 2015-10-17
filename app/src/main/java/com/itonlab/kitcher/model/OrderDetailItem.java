package com.itonlab.kitcher.model;

/**
 * คลาสสำหรับใช้เป็น model แสดงข้อมูลในหน้า Order Detail (หน้าแสดง order item ที่ลูกค้าสั่ง)
 */
public class OrderDetailItem {
    private int menuId;
    private String name;
    private double price;
    private int amount;

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
