package com.san.emenu.chowmein.bean;

/**
 * Created by SANTECH on 11/30/2015.
 */
public class DiningTableBean {
    private int tableno;
    private int tablests;
    private String tablearea;
    private int tblseats;
    private String tcount;


    public int getTableno() {
        return tableno;
    }

    public void setTableno(int tableno) {
        this.tableno = tableno;
    }

    public int getTablests() {
        return tablests;
    }

    public void setTablests(int tablests) {
        this.tablests = tablests;
    }

    public String getTablearea() {
        return tablearea;
    }

    public void setTablearea(String tablearea) {
        this.tablearea = tablearea;
    }

    public int getTblseats() {
        return tblseats;
    }

    public void setTblseats(int tblseats) {
        this.tblseats = tblseats;
    }

    public String getTcount() {
        return tcount;
    }

    public void setTcount(String tcount) {
        this.tcount = tcount;
    }
}
