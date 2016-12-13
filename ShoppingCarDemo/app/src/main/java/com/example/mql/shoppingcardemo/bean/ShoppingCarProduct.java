package com.example.mql.shoppingcardemo.bean;

import android.os.Parcel;

import java.io.Serializable;

/**
 * Created by maqianli on 16/image_6/21.
 */
public class ShoppingCarProduct implements Serializable {
    private int pro_id;
    private String pro_name;
    private String pic_url;
    private int num;
    private double sprice;
    private double price;
    private boolean isChoosed;
    public ShoppingCarProduct() {
    }

//    protected ShoppingCarProduct(Parcel in) {
//        pro_id = in.readString();
//        pro_name = in.readString();
//        pic_url = in.readString();
//        num = in.readInt();
//        sprice = in.readDouble();
//        price = in.readDouble();
//        isChoosed = in.readByte() != 0;
//    }



    @Override
    public String toString() {
        return "ShoppingCarProduct{" +
                "isChoosed=" + isChoosed +
                ", pro_id='" + pro_id + '\'' +
                ", pro_name='" + pro_name + '\'' +
                ", pic_url='" + pic_url + '\'' +
                ", num=" + num +
                ", sprice=" + sprice +
                ", price=" + price +
                '}';
    }

    public boolean isChoosed() {
        return isChoosed;
    }

    public void setChoosed(boolean choosed) {
        isChoosed = choosed;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getPro_id() {
        return pro_id;
    }

    public void setPro_id(int pro_id) {
        this.pro_id = pro_id;
    }

    public String getPro_name() {
        return pro_name;
    }

    public void setPro_name(String pro_name) {
        this.pro_name = pro_name;
    }

    public double getSprice() {
        return sprice;
    }

    public void setSprice(double sprice) {
        this.sprice = sprice;
    }


}
