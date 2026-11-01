package com.intellihire.service;

import com.intellihire.dto.ParsedResumeDTO;
import com.intellihire.model.CandidateProfile;
import com.intellihire.repository.CandidateProfileRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ResumeService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final AiService aiService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ResumeService(CandidateProfileRepository candidateProfileRepository, AiService aiService) {
        this.candidateProfileRepository = candidateProfileRepository;
        this.aiService = aiService;
    }

    public String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper textStripper = new PDFTextStripper();
            return textStripper.getText(document);
        }
    }

    public CandidateProfile uploadAndExtractResume(MultipartFile file, String name, String email) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String uniqueFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path targetPath = uploadPath.resolve(uniqueFileName);

        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // 1. Extract plain text with Apache PDFBox
        String extractedText = extractTextFromPdf(file);

        // 2. Parse text into structured fields via Gemini AI
        ParsedResumeDTO parsedData = aiService.parseResumeWithGemini(extractedText);

        // 3. Save profile with AI-extracted fields
        CandidateProfile profile = new CandidateProfile();
        profile.setCandidateName(name);
        profile.setCandidateEmail(email);
        profile.setResumeFileName(uniqueFileName);
        profile.setRawResumeText(extractedText);
        profile.setEducation(parsedData.getEducation());
        profile.setYearsOfExperience(parsedData.getYearsOfExperience());

        if (parsedData.getSkills() != null && !parsedData.getSkills().isEmpty()) {
            profile.setExtractedSkills(String.join(", ", parsedData.getSkills()));
        }

        return candidateProfileRepository.save(profile);
    }
    
    public List<CandidateProfile> getAllCandidates() {
        return candidateProfileRepository.findAll();
    }

    public List<CandidateProfile> searchCandidates(String skill, Integer minExp) {
        return candidateProfileRepository.searchCandidates(skill, minExp);
    }
    public com.intellihire.dto.JobMatchResponse matchJobDescription(Long candidateId, String jobDescription) {
        CandidateProfile profile = candidateProfileRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + candidateId));

        return aiService.matchResumeWithJob(profile.getRawResumeText(), jobDescription);
    }
}