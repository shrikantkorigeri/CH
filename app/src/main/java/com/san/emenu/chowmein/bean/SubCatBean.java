package com.san.emenu.chowmein.bean;

/**
 * Created by SANTECH on 12/4/2015.
 */
public class SubCatBean {

    private String scname;
    private String sccode;
    private String scatid;
    private String rstatus;

    public String getScatid() {
        return scatid;
    }

    public void setScatid(String scatid) {
        this.scatid = scatid;
    }

    public String getRstatus() {
        return rstatus;
    }

    public void setRstatus(String rstatus) {
        this.rstatus = rstatus;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    private String stat;
    private String rcid;

    public String getRcid() {
        return rcid;
    }

    public void setRcid(String rcid) {
        this.rcid = rcid;
    }

    public String getSccode() {
        return sccode;
    }

    public void setSccode(String sccode) {
        this.sccode = sccode;
    }

    public String getScname() {

        return scname;
    }

    public void setScname(String scname) {
        this.scname = scname;
    }
}
