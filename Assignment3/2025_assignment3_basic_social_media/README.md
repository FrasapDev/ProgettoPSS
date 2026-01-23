# Social Media JPA Assignment

## Members
Francesco Saponara 886465
Mattia Chittoni 886462

for any questions f.saponara@campus.unimib.it

## Project Overview
Java backend application implementing JPA with Hibernate for a social media platform.

## Requirements
- Java 11+
- Maven 3.6+
- H2 Database (in-memory)

## Setup Instructions
1. git clone https://gitlab.com/f.saponara/2025_assignment3_social_media_group32
2. cd 2025_assignment3_social_media_group32
3. mvn clean compile
4. mvn exec:java -D"exec.mainClass=com.socialmedia.main.MainApp"
5. mvn test

## Entities
- User (abstract with inheritance)
- RegularUser
- AdminUser
- Post
- Comment
- Hashtag