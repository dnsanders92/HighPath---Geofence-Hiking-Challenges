package com.example.highpath.Database.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "track_points",
        foreignKeys = @ForeignKey(
                entity = TrailEntity.class,
                parentColumns = "trail_id",
                childColumns = "trail_id",
                onDelete = ForeignKey.CASCADE
        )
)
public class TrackPointEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "point_id")
    private int pointId;

    @NonNull
    @ColumnInfo(name = "trail_id", index = true)
    private String trailId;

    @ColumnInfo(name = "coordinate")
    public String coordinate;

    @ColumnInfo(name = "elevation")
    private double elevation;

    @ColumnInfo(name = "point_index")
    private int pointIndex;

    public int getPointId() { return pointId; }
    public void setPointId(int pointId) { this.pointId = pointId; }

    @NonNull
    public String getTrailId() { return trailId; }
    public void setTrailId(@NonNull String trailId) { this.trailId = trailId; }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public double getElevation() { return elevation; }
    public void setElevation(double elevation) { this.elevation = elevation; }

    public int getPointIndex() { return pointIndex; }
    public void setPointIndex(int pointIndex) { this.pointIndex = pointIndex; }
}
