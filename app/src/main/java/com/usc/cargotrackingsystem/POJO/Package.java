package com.usc.cargotrackingsystem.POJO;

/**
 * Created by Kevin on 1/31/2017.
 */

public class Package {

    public static final String TAG_PACKAGE_ID = "packageID";
    public static final String TAG_PACKAGE_STATUS = "packageStat";
    public static final String TAG_PACKAGE_SENDER = "packageSender";
    public static final String TAG_PACKAGE_RECEPIENT = "packageRec";
    public static final String TAG_CONTAINER_NUMBER = "containerNum";
    public static final String TAG_DELIVERY_ID = "deliveryID";
    public static final String TAG_TRANSACTION_ID = "transactionID";


    public String getPackageID() {
        return packageID;
    }

    public void setPackageID(String packageID) {
        this.packageID = packageID;
    }

    public String getPackageStatus() {
        return packageStatus;
    }

    public void setPackageStatus(String packageStatus) {
        this.packageStatus = packageStatus;
    }

    public String getPackageSender() {
        return packageSender;
    }

    public void setPackageSender(String packageSender) {
        this.packageSender = packageSender;
    }

    public String getPackageRecepient() {
        return packageRecepient;
    }

    public void setPackageRecepient(String packageRecepient) {
        this.packageRecepient = packageRecepient;
    }

    public String getContainerNumber() {
        return containerNumber;
    }

    public void setContainerNumber(String containerNumber) {
        this.containerNumber = containerNumber;
    }

    public String getDeliveryID() {
        return deliveryID;
    }

    public void setDeliveryID(String deliveryID) {
        this.deliveryID = deliveryID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    String packageID;
    String packageStatus;
    String packageSender;
    String packageRecepient;
    String containerNumber;
    String deliveryID;
    String type;
    String transactionCode;
    String transactionID;


    //delivery info values, should move to its own object
    String destinationLatitude;
    String destinationLongitude;


    public String getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(String destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public String getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(String destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }


}
