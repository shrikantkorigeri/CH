package com.san.emenu.chowmein.bean;

/**
 * Created by SANTECH on 12/2/2015.
 */
public class PreOrderBean {

    private String itemname ;
    private String price;
    private String qty;
    private String subtot;

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getSubtot() {
        return subtot;
    }

    public void setSubtot(String subtot) {
        this.subtot = subtot;
    }
}
