package com.shopping.trends.dto;

import java.util.Map;

public class UserStatsDTO {
    private Integer activeUserCount;
    private Double avgPurchaseFrequency;
    private Map<String, Integer> ageGroupDistribution;
    private Map<String, Integer> genderDistribution;
    private Map<String, Integer> locationDistribution;

    // Getters and Setters
    public Integer getActiveUserCount() {
        return activeUserCount;
    }

    public void setActiveUserCount(Integer activeUserCount) {
        this.activeUserCount = activeUserCount;
    }

    public Double getAvgPurchaseFrequency() {
        return avgPurchaseFrequency;
    }

    public void setAvgPurchaseFrequency(Double avgPurchaseFrequency) {
        this.avgPurchaseFrequency = avgPurchaseFrequency;
    }

    public Map<String, Integer> getAgeGroupDistribution() {
        return ageGroupDistribution;
    }

    public void setAgeGroupDistribution(Map<String, Integer> ageGroupDistribution) {
        this.ageGroupDistribution = ageGroupDistribution;
    }

    public Map<String, Integer> getGenderDistribution() {
        return genderDistribution;
    }

    public void setGenderDistribution(Map<String, Integer> genderDistribution) {
        this.genderDistribution = genderDistribution;
    }

    public Map<String, Integer> getLocationDistribution() {
        return locationDistribution;
    }

    public void setLocationDistribution(Map<String, Integer> locationDistribution) {
        this.locationDistribution = locationDistribution;
    }
}