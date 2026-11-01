package com.intellihire.dto;

import java.util.List;

public class ParsedResumeDTO {
    private List<String> skills;
    private int yearsOfExperience;
    private String education;
    private String summary;

    public ParsedResumeDTO() {}

    public ParsedResumeDTO(List<String> skills, int yearsOfExperience, String education, String summary) {
        this.skills = skills;
        this.yearsOfExperience = yearsOfExperience;
        this.education = education;
        this.summary = summary;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}