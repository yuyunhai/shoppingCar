package com.example.mql.shoppingcardemo.bean;

import android.os.Parcel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by maqianli on 16/image_6/21.
 */
public class ShoppingCarStore implements Serializable{
    private int sid;
    private String sname;
    private String slogo;
    private  double amount;
    private List items;
    private boolean isChoosed;

    protected ShoppingCarStore(Parcel in) {
        sid = in.readInt();
        sname = in.readString();
        slogo = in.readString();
        amount = in.readDouble();
        isChoosed = in.readByte() != 0;
    }


    public boolean isChoosed() {
        return isChoosed;
    }

    public void setChoosed(boolean choosed) {
        isChoosed = choosed;
    }

    public ShoppingCarStore() {
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getSlogo() {
        return slogo;
    }

    public void setSlogo(String slogo) {
        this.slogo = slogo;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    @Override
    public String toString() {
        return "ShoppingCarStore{" +
                "amount=" + amount +
                ", sid=" + sid +
                ", sname='" + sname + '\'' +
                ", slogo='" + slogo + '\'' +
                ", items=" + items +
                ", isChoosed=" + isChoosed +
                '}';
    }


}
