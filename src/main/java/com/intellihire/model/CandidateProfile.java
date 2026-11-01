package com.intellihire.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "candidate_profiles")
public class CandidateProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String candidateName;
    private String candidateEmail;
    private String resumeFileName;

    // Apache PDFBox will store the extracted plain text here
    @Column(columnDefinition = "LONGTEXT")
    private String rawResumeText;

    private int yearsOfExperience;
    private String education;

    @Column(columnDefinition = "TEXT")
    private String extractedSkills;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    public CandidateProfile() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getCandidateEmail() { return candidateEmail; }
    public void setCandidateEmail(String candidateEmail) { this.candidateEmail = candidateEmail; }

    public String getResumeFileName() { return resumeFileName; }
    public void setResumeFileName(String resumeFileName) { this.resumeFileName = resumeFileName; }

    public String getRawResumeText() { return rawResumeText; }
    public void setRawResumeText(String rawResumeText) { this.rawResumeText = rawResumeText; }

    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getExtractedSkills() { return extractedSkills; }
    public void setExtractedSkills(String extractedSkills) { this.extractedSkills = extractedSkills; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}