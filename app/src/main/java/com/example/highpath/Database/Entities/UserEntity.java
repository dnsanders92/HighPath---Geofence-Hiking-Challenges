package com.example.highpath.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "user")
    public class UserEntity {

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "user_id")
        public int userId;

        @ColumnInfo(name = "first_name")
        public String firstName;

        @ColumnInfo(name = "surname")
        public String surname;

        @ColumnInfo(name = "month_of_birth")
        public String monthOfBirth;

        @ColumnInfo(name = "year_of_birth")
        public int yearOfBirth;

        @ColumnInfo(name = "total_distance_km")
        public int totalDistanceKm;

        @ColumnInfo(name = "total_elevation_m")
        public int totalElevationM;

        @ColumnInfo(name = "completed_activities")
        public int completedActivities;

        @ColumnInfo(name = "completed_challenges")
        public int completedChallenges;

        @ColumnInfo(name = "total_time_outside_seconds")
        public int totalTimeOutsideSeconds;

        @ColumnInfo(name = "total_moving_time_seconds")
        public int totalMovingTimeSeconds;

        @ColumnInfo(name = "profile_image")
        public byte[] profileImage;

        @ColumnInfo(name = "banner_image")
        public byte[] bannerImage;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMonthOfBirth() {
        return monthOfBirth;
    }

    public void setMonthOfBirth(String monthOfBirth) {
        this.monthOfBirth = monthOfBirth;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public int getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(int totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }

    public int getTotalElevationM() {
        return totalElevationM;
    }

    public void setTotalElevationM(int totalElevationM) {
        this.totalElevationM = totalElevationM;
    }

    public int getCompletedActivities() {
        return completedActivities;
    }

    public void setCompletedActivities(int completedActivities) {
        this.completedActivities = completedActivities;
    }

    public int getCompletedChallenges() {
        return completedChallenges;
    }

    public void setCompletedChallenges(int completedChallenges) {
        this.completedChallenges = completedChallenges;
    }

    public int getTotalTimeOutsideSeconds() {
        return totalTimeOutsideSeconds;
    }

    public void setTotalTimeOutsideSeconds(int totalTimeOutsideSeconds) {
        this.totalTimeOutsideSeconds = totalTimeOutsideSeconds;
    }

    public int getTotalMovingTimeSeconds() {
        return totalMovingTimeSeconds;
    }

    public void setTotalMovingTimeSeconds(int totalMovingTimeSeconds) {
        this.totalMovingTimeSeconds = totalMovingTimeSeconds;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }

    public byte[] getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(byte[] bannerImage) {
        this.bannerImage = bannerImage;
    }
}
