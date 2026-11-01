package com.intellihire.dto;

public class JobMatchRequest {
    private Long candidateId;
    private String jobDescription;

    public JobMatchRequest() {}

    public JobMatchRequest(Long candidateId, String jobDescription) {
        this.candidateId = candidateId;
        this.jobDescription = jobDescription;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
}