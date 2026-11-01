# IntelliHire - AI-Powered ATS Backend

IntelliHire is a full-stack applicant tracking system backend built with **Java**, **Spring Boot**, **Spring Security (JWT)**, and the **Groq LLM API**. The application processes multi-page PDF resumes, extracts structured candidate profiles, persists candidate records in MySQL, and evaluates candidate fit against job descriptions using large language models.

---

## Key Features

- **Stateless Authentication**: Role-based access control (`ROLE_RECRUITER`) secured via Spring Security and JSON Web Tokens (JWT).
- **PDF Resume Processing**: Parses and extracts raw text from PDF uploads using **Apache PDFBox**.
- **AI-Driven Information Extraction**: Communicates with the **Groq Cloud API** (`llama-3.3-70b-versatile`) via REST client to convert raw resume text into structured fields (skills, experience, education).
- **ATS Job Matching**: Compares candidate skillsets against target job descriptions, outputting a match percentage score, matched competencies, missing skills, and evaluation feedback.
- **Talent Pool Filtering**: Filter candidate profiles dynamically by specific technical skills and minimum years of experience.

---

## Tech Stack

- **Backend Framework**: Spring Boot 3.x
- **Language**: Java 17 / 21
- **Security**: Spring Security 6.x, JJWT (io.jsonwebtoken)
- **Database & Persistence**: MySQL 8.x, Spring Data JPA, Hibernate ORM
- **Document & AI Processing**: Apache PDFBox, Groq Cloud API (`llama-3.3-70b-versatile`)
- **Build Tool**: Apache Maven

---

## Architecture Flow

```text
Client (React / Postman)
          │
          ▼
   [Security Filter]  ── (Validates JWT Bearer Token)
          │
          ▼
  [REST Controllers]  ── (AuthController, ResumeController)
          │
          ▼
  [Service Layer]     ── (ResumeService, AiService)
       ├── Apache PDFBox  -> Extracts text from multipart PDF file
       └── Groq Cloud API -> Converts text to JSON structure & scores match
          │
          ▼
  [Spring Data JPA]   ── (UserRepository, CandidateProfileRepository)
          │
          ▼
     [MySQL DB]       ── (users, candidate_profiles)
