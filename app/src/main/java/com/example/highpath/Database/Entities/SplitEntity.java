package com.example.highpath.Database.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "splits",
        foreignKeys = @ForeignKey(
                entity = ActivityEntity.class,
                parentColumns = "activity_id",
                childColumns = "activity_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("activity_id")}
)
public class SplitEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "split_id")
    private Integer splitId;

    @NonNull
    @ColumnInfo(name = "activity_id")
    private String activityId;

    @ColumnInfo(name = "split_index")
    private Integer splitIndex; // Changed to Integer for safer schema matching

    @ColumnInfo(name = "split_pace")
    private Double pace;

    @ColumnInfo(name = "split_distance")
    private Double distance;

    // --- Getters and Setters ---

    public Integer getSplitId() {
        return splitId;
    }

    public void setSplitId(Integer splitId) {
        this.splitId = splitId;
    }

    @NonNull
    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(@NonNull String activityId) {
        this.activityId = activityId;
    }

    public Integer getSplitIndex() {
        return splitIndex;
    }

    public void setSplitIndex(Integer splitIndex) {
        this.splitIndex = splitIndex;
    }

    public Double getPace() {
        return pace;
    }

    public void setPace(Double pace) {
        this.pace = pace;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}