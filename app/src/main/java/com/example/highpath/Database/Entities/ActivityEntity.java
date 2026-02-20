package com.example.highpath.Database.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "activities")
public class ActivityEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "activity_id") private String activityId;
    @ColumnInfo(name = "activity_name") private String activityName;
    @ColumnInfo(name = "distance") private double distance;
    @ColumnInfo(name = "elevation_gain") private double elevationGain;
    @ColumnInfo(name = "elevation_loss") private double elevationLoss;
    @ColumnInfo(name = "average_pace") private double averagePace;
    @ColumnInfo(name = "moving_time") private long movingTime;
    @ColumnInfo(name = "actual_time") private long actualTime;
    @ColumnInfo(name = "calories") private double calories;
    @ColumnInfo(name = "gpx_file_path") private String gpxFilePath;
    @ColumnInfo(name = "timestamp") private long timestamp;

    @NonNull
    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(@NonNull String activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getElevationGain() {
        return elevationGain;
    }

    public void setElevationGain(double elevationGain) {
        this.elevationGain = elevationGain;
    }

    public double getElevationLoss() {
        return elevationLoss;
    }

    public void setElevationLoss(double elevationLoss) {
        this.elevationLoss = elevationLoss;
    }

    public double getAveragePace() {
        return averagePace;
    }

    public void setAveragePace(double averagePace) {
        this.averagePace = averagePace;
    }

    public long getMovingTime() {
        return movingTime;
    }

    public void setMovingTime(long movingTime) {
        this.movingTime = movingTime;
    }

    public long getActualTime() {
        return actualTime;
    }

    public void setActualTime(long actualTime) {
        this.actualTime = actualTime;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public String getGpxFilePath() {
        return gpxFilePath;
    }

    public void setGpxFilePath(String gpxFilePath) {
        this.gpxFilePath = gpxFilePath;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
