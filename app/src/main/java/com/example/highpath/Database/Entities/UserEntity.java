package com.example.highpath.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class UserEntity {

    @PrimaryKey()
    @ColumnInfo(name = "user_id")
    public Integer userId; // Integer allows NULL, int does not

    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "surname")
    public String surname;

    @ColumnInfo(name = "month_of_birth")
    public String monthOfBirth;

    @ColumnInfo(name = "year_of_birth")
    public Integer yearOfBirth;

    @ColumnInfo(name = "total_distance_km")
    public Integer totalDistanceKm;

    @ColumnInfo(name = "total_elevation_m")
    public Integer totalElevationM;

    @ColumnInfo(name = "completed_activities")
    public Integer completedActivities;

    @ColumnInfo(name = "completed_challenges")
    public Integer completedChallenges;

    @ColumnInfo(name = "total_time_outside_seconds")
    public Long totalTimeOutsideSeconds;

    @ColumnInfo(name = "total_moving_time_seconds")
    public Long totalMovingTimeSeconds;

    @ColumnInfo(name = "profile_image")
    public byte[] profileImage;

    @ColumnInfo(name = "banner_image")
    public byte[] bannerImage;

    // Standard Getters and Setters
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getMonthOfBirth() { return monthOfBirth; }
    public void setMonthOfBirth(String monthOfBirth) { this.monthOfBirth = monthOfBirth; }

    public Integer getYearOfBirth() { return yearOfBirth; }
    public void setYearOfBirth(Integer yearOfBirth) { this.yearOfBirth = yearOfBirth; }

    public Integer getTotalDistanceKm() { return totalDistanceKm; }
    public void setTotalDistanceKm(Integer totalDistanceKm) { this.totalDistanceKm = totalDistanceKm; }

    public Integer getTotalElevationM() { return totalElevationM; }
    public void setTotalElevationM(Integer totalElevationM) { this.totalElevationM = totalElevationM; }

    public Integer getCompletedActivities() { return completedActivities; }
    public void setCompletedActivities(Integer completedActivities) { this.completedActivities = completedActivities; }

    public Integer getCompletedChallenges() { return completedChallenges; }
    public void setCompletedChallenges(Integer completedChallenges) { this.completedChallenges = completedChallenges; }

    public Long getTotalTimeOutsideSeconds() { return totalTimeOutsideSeconds; }
    public void setTotalTimeOutsideSeconds(Long totalTimeOutsideSeconds) { this.totalTimeOutsideSeconds = totalTimeOutsideSeconds; }

    public Long getTotalMovingTimeSeconds() { return totalMovingTimeSeconds; }
    public void setTotalMovingTimeSeconds(Long totalMovingTimeSeconds) { this.totalMovingTimeSeconds = totalMovingTimeSeconds; }

    public byte[] getProfileImage() { return profileImage; }
    public void setProfileImage(byte[] profileImage) { this.profileImage = profileImage; }

    public byte[] getBannerImage() { return bannerImage; }
    public void setBannerImage(byte[] bannerImage) { this.bannerImage = bannerImage; }
}