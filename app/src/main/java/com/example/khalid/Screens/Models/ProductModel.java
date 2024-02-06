package com.example.khalid.Screens.Models;

public class ProductModel {
    String id, pName, pPrice, pStock, pDesc, pImage, status;
    public ProductModel(){


    }

    public ProductModel(String id, String pName, String pPrice, String pStock, String pDesc, String pImage, String status) {
        this.id = id;
        this.pName = pName;
        this.pPrice = pPrice;
        this.pStock = pStock;
        this.pDesc = pDesc;
        this.pImage = pImage;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getpPrice() {
        return pPrice;
    }

    public void setpPrice(String pPrice) {
        this.pPrice = pPrice;
    }

    public String getpStock() {
        return pStock;
    }

    public void setpStock(String pStock) {
        this.pStock = pStock;
    }

    public String getpDesc() {
        return pDesc;
    }

    public void setpDesc(String pDesc) {
        this.pDesc = pDesc;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
