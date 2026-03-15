package com.shopping.trends.dto;

import java.util.Map;

public class FrequencyStatsDTO {
    private Map<String, Integer> dailyFrequency;
    private Map<String, Integer> weeklyFrequency;
    private Map<String, Integer> monthlyFrequency;

    // Getters and Setters
    public Map<String, Integer> getDailyFrequency() {
        return dailyFrequency;
    }

    public void setDailyFrequency(Map<String, Integer> dailyFrequency) {
        this.dailyFrequency = dailyFrequency;
    }

    public Map<String, Integer> getWeeklyFrequency() {
        return weeklyFrequency;
    }

    public void setWeeklyFrequency(Map<String, Integer> weeklyFrequency) {
        this.weeklyFrequency = weeklyFrequency;
    }

    public Map<String, Integer> getMonthlyFrequency() {
        return monthlyFrequency;
    }

    public void setMonthlyFrequency(Map<String, Integer> monthlyFrequency) {
        this.monthlyFrequency = monthlyFrequency;
    }
}