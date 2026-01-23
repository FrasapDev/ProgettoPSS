package com.socialmedia.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "regular_users")
@PrimaryKeyJoinColumn(name = "user_id")
public class RegularUser extends User {

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "location")
    private String location;

    public RegularUser() {
        super();
    }

    public RegularUser(String username, String email) {
        super(username, email);
    }

    public RegularUser(String username, String email, String bio, LocalDate birthDate, String location) {
        super(username, email);
        this.bio = bio;
        this.birthDate = birthDate;
        this.location = location;
    }

    // Getters and Setters
    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "RegularUser{" + super.toString() +
                ", bio='" + bio + "', birthDate=" + birthDate +
                ", location='" + location + "'}";
    }
}