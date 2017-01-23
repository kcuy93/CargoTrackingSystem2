package com.usc.cargotrackingsystem.POJO;

/**
 * Created by Kevin on 1/23/2017.
 */

public class Driver {
    public static final String ID_TAG = "id";
    public static final String FNAME_TAG = "fname";
    public static final String LNAME_TAG = "lname";
    public static final String MNAME_TAG = "mname";
    public static final String LICENSE_TAG = "licenseNo";
    public static final String ADDRESS_TAG = "address";


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    String id;
    String username;
    String password;
    String fname;
    String lnmae;

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    String lname;
    String mname;
    String license;
}
