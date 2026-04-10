package com.example.highpath.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "summits")
public class SummitsEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "summit_id")
    private int summitId;

    @ColumnInfo(name = "summit_name")
    private String summitName;

    @ColumnInfo(name = "region")
    private String region;

    @ColumnInfo(name = "elevation")
    private int elevation;

    @ColumnInfo(name = "prominence")
    private int prominence;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "completed")
    private boolean completed;

    @ColumnInfo(name = "description")
    private String description;

    // New field for the image
    @ColumnInfo(name = "summit_image", typeAffinity = ColumnInfo.BLOB)
    private byte[] summitImage;

    // --- Getters and Setters ---

    public int getSummitId() {
        return summitId;
    }

    public void setSummitId(int summitId) {
        this.summitId = summitId;
    }

    public String getSummitName() {
        return summitName;
    }

    public void setSummitName(String summitName) {
        this.summitName = summitName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getElevation() {
        return elevation;
    }

    public void setElevation(int elevation) {
        this.elevation = elevation;
    }

    public int getProminence() {
        return prominence;
    }

    public void setProminence(int prominence) {
        this.prominence = prominence;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // New Getter and Setter for the image
    public byte[] getSummitImage() {
        return summitImage;
    }

    public void setSummitImage(byte[] summitImage) {
        this.summitImage = summitImage;
    }
}