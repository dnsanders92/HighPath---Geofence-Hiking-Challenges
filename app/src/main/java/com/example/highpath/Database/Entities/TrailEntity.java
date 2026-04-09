package com.example.highpath.Database.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "trails")
public class TrailEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "trail_id")
    private String trailId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "region")
    private String region;

    @ColumnInfo(name = "distance_km")
    private double distanceKm;

    @ColumnInfo(name = "ascent_m")
    private int ascentM;

    @ColumnInfo(name = "difficulty")
    private String difficulty;

    @ColumnInfo(name = "thumbnail", typeAffinity = ColumnInfo.BLOB)
    private byte[] thumbnail;

    @ColumnInfo(name = "preview_image", typeAffinity = ColumnInfo.BLOB)
    private byte[] previewImage;

    @ColumnInfo(name = "center_lat")
    private double centerLat;

    @ColumnInfo(name = "center_lon")
    private double centerLon;

    // --- Bounding Box Fields ---
    @ColumnInfo(name = "min_lat")
    private double minLat;

    @ColumnInfo(name = "max_lat")
    private double maxLat;

    @ColumnInfo(name = "min_lon")
    private double minLon;

    @ColumnInfo(name = "max_lon")
    private double maxLon;

    // --- Getters and Setters ---

    @NonNull
    public String getTrailId() { return trailId; }
    public void setTrailId(@NonNull String trailId) { this.trailId = trailId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public int getAscentM() { return ascentM; }
    public void setAscentM(int ascentM) { this.ascentM = ascentM; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public byte[] getThumbnail() { return thumbnail; }
    public void setThumbnail(byte[] thumbnail) { this.thumbnail = thumbnail; }

    public byte[] getPreviewImage() { return previewImage; }
    public void setPreviewImage(byte[] previewImage) { this.previewImage = previewImage; }

    public double getCenterLat() { return centerLat; }
    public void setCenterLat(double centerLat) { this.centerLat = centerLat; }

    public double getCenterLon() { return centerLon; }
    public void setCenterLon(double centerLon) { this.centerLon = centerLon; }

    // Bounding Box Getters/Setters
    public double getMinLat() { return minLat; }
    public void setMinLat(double minLat) { this.minLat = minLat; }

    public double getMaxLat() { return maxLat; }
    public void setMaxLat(double maxLat) { this.maxLat = maxLat; }

    public double getMinLon() { return minLon; }
    public void setMinLon(double minLon) { this.minLon = minLon; }

    public double getMaxLon() { return maxLon; }
    public void setMaxLon(double maxLon) { this.maxLon = maxLon; }
}