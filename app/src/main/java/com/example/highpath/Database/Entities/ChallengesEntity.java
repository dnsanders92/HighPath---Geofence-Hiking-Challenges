package com.example.highpath.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "challenges")
public class ChallengesEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "challenge_id")
    private int challengeId;

    @ColumnInfo(name = "challenge_name")
    private String challengeName;

    // Added region to match the province in SummitsEntity
    @ColumnInfo(name = "region")
    private String region;

    @ColumnInfo(name = "short_description")
    private String shortDescription;

    @ColumnInfo(name = "long_description")
    private String longDescription;

    @ColumnInfo(name = "completed_summits")
    private int completedSummits;

    @ColumnInfo(name = "total_summits")
    private int totalSummits;

    @ColumnInfo(name = "is_completed")
    private boolean isCompleted;

    @ColumnInfo(name = "challenge_thumbnail")
    private byte[] challengeThumbnail;

    // Getters and Setters

    public int getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(int challengeId) {
        this.challengeId = challengeId;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public int getCompletedSummits() {
        return completedSummits;
    }

    public void setCompletedSummits(int completedSummits) {
        this.completedSummits = completedSummits;
    }

    public int getTotalSummits() {
        return totalSummits;
    }

    public void setTotalSummits(int totalSummits) {
        this.totalSummits = totalSummits;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public byte[] getChallengeThumbnail() {
        return challengeThumbnail;
    }

    public void setChallengeThumbnail(byte[] challengeThumbnail) {
        this.challengeThumbnail = challengeThumbnail;
    }
}