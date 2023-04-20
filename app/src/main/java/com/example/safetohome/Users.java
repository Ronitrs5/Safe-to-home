package com.example.safetohome;

public class Users {

    String name1, name2, phone1, phone2;

    public Users() {
    }

    public Users(String name1, String name2, String phone1, String phone2) {
        this.name1 = name1;
        this.name2 = name2;
        this.phone1 = phone1;
        this.phone2 = phone2;
    }


    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }
}
