package com.intellihire.controller;

import com.intellihire.model.CandidateProfile;
import com.intellihire.service.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("email") String email
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File cannot be empty.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only PDF files are supported.");
        }

        try {
            CandidateProfile profile = resumeService.uploadAndExtractResume(file, name, email);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Resume uploaded, extracted, and parsed by AI successfully!");
            response.put("profileId", profile.getId());
            response.put("candidateName", profile.getCandidateName());
            response.put("extractedCharacters", profile.getRawResumeText().length());
            response.put("education", profile.getEducation());
            response.put("experienceYears", profile.getYearsOfExperience());
            response.put("skills", profile.getExtractedSkills());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process resume: " + e.getMessage());
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<CandidateProfile>> getAllResumes() {
        return ResponseEntity.ok(resumeService.getAllCandidates());
    }

    @GetMapping("/search")
    public ResponseEntity<List<CandidateProfile>> searchCandidates(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) Integer minExp
    ) {
        return ResponseEntity.ok(resumeService.searchCandidates(skill, minExp));
    }
    @PostMapping("/match-job")
    public ResponseEntity<com.intellihire.dto.JobMatchResponse> matchJob(
            @RequestBody com.intellihire.dto.JobMatchRequest request
    ) {
        return ResponseEntity.ok(resumeService.matchJobDescription(request.getCandidateId(), request.getJobDescription()));
    }
}