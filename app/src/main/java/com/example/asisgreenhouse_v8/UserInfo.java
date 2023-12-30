package com.example.asisgreenhouse_v8;

public class UserInfo {
    private String Fullname;
    private String Email;
    private String Address;
    private String Phone;

    public UserInfo() {
    }
// created getter and setter
    public String getFullname() {
        return Fullname;
    }
    public void setFullname(String val) {
        this.Fullname = val;
    }
    public String getEmail() {
        return Email;
    }
    public void setEmail(String val) {
        this.Email = val;
    }
    public String getAddress() {
        return Address;
    }
    public void setAddress(String val) {
        this.Address = val;
    }
    public String getPhone() {
        return Phone;
    }
    public void setPhone(String val) {
        this.Phone = val;
    }
}
