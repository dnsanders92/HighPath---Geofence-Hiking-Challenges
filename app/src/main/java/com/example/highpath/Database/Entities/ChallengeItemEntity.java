package com.example.highpath.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "challenge_items",
        foreignKeys = {
                @ForeignKey(
                        entity = ChallengesEntity.class,
                        parentColumns = "challenge_id",
                        childColumns = "challenge_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = SummitsEntity.class,
                        parentColumns = "summit_id",
                        childColumns = "summit_id",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index("challenge_id"), @Index("summit_id")}
)
public class ChallengeItemEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "item_id")
    private int itemId;

    @ColumnInfo(name = "challenge_id")
    private int challengeId;

    @ColumnInfo(name = "summit_id")
    private Integer summitId; // nullable

    @ColumnInfo(name = "item_name")
    private String itemName;

    @ColumnInfo(name = "latitude")
    private Double latitude;

    @ColumnInfo(name = "longitude")
    private Double longitude;

    @ColumnInfo(name = "is_completed")
    private boolean isCompleted;

    // Getters and Setters
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getChallengeId() { return challengeId; }
    public void setChallengeId(int challengeId) { this.challengeId = challengeId; }

    public Integer getSummitId() { return summitId; }
    public void setSummitId(Integer summitId) { this.summitId = summitId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}