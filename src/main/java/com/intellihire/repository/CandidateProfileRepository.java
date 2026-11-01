package com.intellihire.repository;

import com.intellihire.model.CandidateProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateProfileRepository extends JpaRepository<CandidateProfile, Long> {
    
    Optional<CandidateProfile> findByCandidateEmail(String candidateEmail);

    // Search by skill keyword and minimum years of experience
    @Query("SELECT c FROM CandidateProfile c WHERE " +
           "(:skill IS NULL OR LOWER(c.extractedSkills) LIKE LOWER(CONCAT('%', :skill, '%'))) AND " +
           "(:minExp IS NULL OR c.yearsOfExperience >= :minExp)")
    List<CandidateProfile> searchCandidates(@Param("skill") String skill, @Param("minExp") Integer minExp);
}