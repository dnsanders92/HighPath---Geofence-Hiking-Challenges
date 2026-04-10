package com.example.highpath.Database.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "activities")
public class ActivityEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "activity_id")
    private String activityId;

    @ColumnInfo(name = "activity_name")
    private String activityName;

    @ColumnInfo(name = "distance")
    private Double distance;

    @ColumnInfo(name = "elevation_gain")
    private Double elevationGain;

    @ColumnInfo(name = "elevation_loss")
    private Double elevationLoss;

    @ColumnInfo(name = "average_pace")
    private Double averagePace;

    @ColumnInfo(name = "moving_time")
    private Long movingTime;

    @ColumnInfo(name = "actual_time")
    private Long actualTime;

    @ColumnInfo(name = "calories")
    private Double calories;

    @ColumnInfo(name = "gpx_file_path")
    private String gpxFilePath;

    @ColumnInfo(name = "timestamp")
    private Long timestamp;

    @ColumnInfo(name = "is_challenge")
    private Boolean isChallenge;

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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getElevationGain() {
        return elevationGain;
    }

    public void setElevationGain(Double elevationGain) {
        this.elevationGain = elevationGain;
    }

    public Double getElevationLoss() {
        return elevationLoss;
    }

    public void setElevationLoss(Double elevationLoss) {
        this.elevationLoss = elevationLoss;
    }

    public Double getAveragePace() {
        return averagePace;
    }

    public void setAveragePace(Double averagePace) {
        this.averagePace = averagePace;
    }

    public Long getMovingTime() {
        return movingTime;
    }

    public void setMovingTime(Long movingTime) {
        this.movingTime = movingTime;
    }

    public Long getActualTime() {
        return actualTime;
    }

    public void setActualTime(Long actualTime) {
        this.actualTime = actualTime;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public String getGpxFilePath() {
        return gpxFilePath;
    }

    public void setGpxFilePath(String gpxFilePath) {
        this.gpxFilePath = gpxFilePath;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    // Fixed naming to resolve "private access" errors
    public Boolean getIsChallenge() {
        return isChallenge;
    }

    public void setIsChallenge(Boolean isChallenge) {
        this.isChallenge = isChallenge;
    }
}