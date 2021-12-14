package com.bill.cashmanager.models;

public class NavCategoryModel {
    String description;
    String name;
    String discount;
    String img_url;
    String type;

    public NavCategoryModel() {
    }

    public NavCategoryModel(String description, String name, String discount, String img_url, String type) {
        this.description = description;
        this.name = name;
        this.discount = discount;
        this.img_url = img_url;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
