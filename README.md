# Job Search Orchestrator 🚀

### 🛠 Status: Work-in-Progress (WIP)
This project is currently under active development. I am building the core scraping engine and data models.

### 🎯 Project Overview
An automated job aggregation service built with **Spring Boot**. The goal is to parse job listings from multiple platforms using **Jsoup** to help users find opportunities more efficiently.

### 🏗 Tech Stack
* **Java 21**
* **Spring Boot 4.x**
* **Maven** (Dependency Management)
* **Jsoup** (HTML Parsing)

### 📈 Current Roadmap
- [x] Initial Project Setup & Spring Boot Configuration
- [ ] Implement Jsoup Scraper Service for RemoteOK, WeworkRemotely and Remotive
- [ ] Create Data Models for Job Postings
- [ ] Implement Scheduled Tasks for Automated Scraping
- [ ] Database Integration (H2 or PostgreSQL)

### 🚀 How to Run (Local)
1. Clone the repository.
2. Run `./mvnw spring-boot:run`.