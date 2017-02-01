package com.usc.cargotrackingsystem.POJO;

public class Transaction {


    public static final String ID_TAG = "trans_id";
    public static final String CODE_TAG = "trans_code";
    public static final String DELIVERY_ID_TAG = "delivery_id";
    public static final String TRUCK_TAG = "truck_id";
    public static final String DRIVER_ID_TAG = "driver_id";
    public static final String CONTAINER_ID_TAG = "container_id";
    public static final String STATUS_TAG = "trans_status";
    public static final String DATE_TAG = "date";


    String id;
    String code;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDeliveryID() {
        return deliveryID;
    }

    public void setDeliveryID(String deliveryID) {
        this.deliveryID = deliveryID;
    }

    public String getTruckID() {
        return truckID;
    }

    public void setTruckID(String truckID) {
        this.truckID = truckID;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public String getContainerID() {
        return containerID;
    }

    public void setContainerID(String containerID) {
        this.containerID = containerID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    String deliveryID;
    String truckID;
    String driverID;
    String containerID;
    String status;
    String date;


}
