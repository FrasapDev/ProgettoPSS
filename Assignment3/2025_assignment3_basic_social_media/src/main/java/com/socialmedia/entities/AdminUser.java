package com.socialmedia.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "admin_users")
@PrimaryKeyJoinColumn(name = "user_id")
public class AdminUser extends User {

    @Column(name = "admin_level")
    private String adminLevel; // "SUPER_ADMIN", "MODERATOR", "SUPPORT"

    @Column(name = "department")
    private String department;

    @Column(name = "promotion_date")
    private LocalDate promotionDate;

    public AdminUser() {
        super();
    }

    public AdminUser(String username, String email, String adminLevel, String department) {
        super(username, email);
        this.adminLevel = adminLevel;
        this.department = department;
        this.promotionDate = LocalDate.now();
    }

    // Getters and Setters
    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDate getPromotionDate() {
        return promotionDate;
    }

    public void setPromotionDate(LocalDate promotionDate) {
        this.promotionDate = promotionDate;
    }

    @Override
    public String toString() {
        return "AdminUser{" + super.toString() +
                ", adminLevel='" + adminLevel + "', department='" + department +
                "', promotionDate=" + promotionDate + "}";
    }
}