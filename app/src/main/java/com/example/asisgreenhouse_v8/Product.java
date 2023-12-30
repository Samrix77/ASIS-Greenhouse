package com.example.asisgreenhouse_v8;

import com.google.firebase.database.Exclude;

public class Product {
    private String id,name,price,photo;



    public Product() {
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    // created getter and setter
    public String getName() {
        return name;
    }
    public void setName(String val) {
        this.name = val;
    }
    public String getPrice() {
        return price;
    }
    public void setPrice(String val) {
        this.price = val;
    }
    @Exclude
    public void setPrice(double val) {
        this.price = String.valueOf(val);
    }
    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String val) {
        this.photo = val;
    }

    public double getPriceAsDouble() {
        try {
            return Double.parseDouble(price);
        } catch (NumberFormatException e) {
            return 0.0; // or handle the error as appropriate
        }
    }
}

