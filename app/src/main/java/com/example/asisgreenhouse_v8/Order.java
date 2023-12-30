package com.example.asisgreenhouse_v8;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Order {

    private String id,email,productName,productPrice,datetime;
    private Integer quantity;
    private boolean approved;

    public Order() {
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String val) {
        this.email = val;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String val) {
        this.productName = val;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer val) {
        this.quantity = val;
    }
    public String getProductPrice() {
        return productPrice;
    }
    public void setProductPrice(String val) {
        this.productPrice = val;
    }
    public String getDatetime() {
        return datetime;
    }
    public void setDatetime(String val) {
        this.datetime = val;
    }
    public Boolean getApproved() {
        return approved;
    }
    public void setApproved(Boolean val) {
        this.approved = val;
    }

}