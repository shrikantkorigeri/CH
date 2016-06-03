package com.san.emenu.chowmein.bean;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by SANTECH on 11/26/2015.
 */
public class ItemsBean {

    private String itemname;
    private String itemid;
    private  String itemcode;
    private String itemprice;
    private String rscatid;
    private int qty;
    private String ingrdients;
    private  String subtot;
    private  String ordertype;
    private String rstatus;
    private String itmsts;
    private String rating;
    Bitmap bitmapimg;
    Drawable imgdrwbl;

    public Bitmap getBitmapimg() {
        return bitmapimg;
    }

    public void setBitmapimg(Bitmap bitmapimg) {
        this.bitmapimg = bitmapimg;
    }

    public Drawable getImgdrwbl() {
        return imgdrwbl;
    }

    public void setImgdrwbl(Drawable imgdrwbl) {
        this.imgdrwbl = imgdrwbl;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRstatus() {
        return rstatus;
    }

    public void setRstatus(String rstatus) {
        this.rstatus = rstatus;
    }

    public String getItmsts() {
        return itmsts;
    }

    public void setItmsts(String itmsts) {
        this.itmsts = itmsts;
    }

    public String getPretime() {
        return pretime;
    }

    public void setPretime(String pretime) {
        this.pretime = pretime;
    }

    private  String pretime;

    public String getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(String ordertype) {
        this.ordertype = ordertype;
    }

    public String getSubtot() {
        return subtot;
    }

    public void setSubtot(String subtot) {
        this.subtot = subtot;
    }

    public String getIngrdients() {
        return ingrdients;
    }

    public void setIngrdients(String ingrdients) {
        this.ingrdients = ingrdients;
    }

    public int getQty() {
        return qty;
    }

    public int setQty(int qty) {
        this.qty = qty;
        return qty;
    }

    public String getRscatid() {
        return rscatid;
    }

    public void setRscatid(String rscatid) {
        this.rscatid = rscatid;
    }

    public String getItemcode() {
        return itemcode;
    }

    public void setItemcode(String itemcode) {
        this.itemcode = itemcode;
    }

    public String getItemprice() {
        return itemprice;
    }

    public void setItemprice(String itemprice) {
        this.itemprice = itemprice;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }
}
