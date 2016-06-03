package com.san.emenu.chowmein.bean;

/**
 * Created by SANTECH on 12/10/2015.
 */
public class CategoryBean  {
    private String rcatid;
    private String rcatname;
    private int rcatstatus;
    private String catstatus;
    private String sectionid;

    public String getSectionid() {
        return sectionid;
    }

    public void setSectionid(String sectionid) {
        this.sectionid = sectionid;
    }

    public String getCatstatus() {
        return catstatus;
    }

    public void setCatstatus(String catstatus) {
        this.catstatus = catstatus;
    }

    public String getRcatid() {

        return rcatid;
    }

    public void setRcatid(String rcatid) {
        this.rcatid = rcatid;
    }

    public String getRcatname() {
        return rcatname;
    }

    public void setRcatname(String rcatname) {
        this.rcatname = rcatname;
    }

    public int getRcatstatus() {
        return rcatstatus;
    }

    public void setRcatstatus(int rcatstatus) {
        this.rcatstatus = rcatstatus;
    }
}
