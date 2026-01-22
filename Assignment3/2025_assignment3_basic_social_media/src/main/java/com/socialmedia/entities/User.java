package com.socialmedia.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users") // "user" is reserved in SQL
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true, nullable = false, length = 50)
	private String username;
	
	@Column(unique = true, nullable = false)
	private String email;
	
	private LocalDate joinDate;
	
	// Self-loop relationship for following
	@ManyToMany
	@JoinTable(
		name = "user_following",
		joinColumns = @JoinColumn(name = "follower_id"),
		inverseJoinColumns = @JoinColumn(name = "followed_id")
	)
	private Set<User> following = new HashSet<>();
	
	@ManyToMany(mappedBy = "following")
	private Set<User> followers = new HashSet<>();
	
	// Default constructor (REQUIRED by JPA)
    public User() {
        this.joinDate = LocalDate.now();
    }
    
    // Parameterized constructor
    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.joinDate = LocalDate.now();
    }
    
    // Getters and Setters (REQUIRED)
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDate getJoinDate() {
        return joinDate;
    }
    
    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }
    
    public Set<User> getFollowing() {
        return following;
    }
    
    public void setFollowing(Set<User> following) {
        this.following = following;
    }
    
    public Set<User> getFollowers() {
        return followers;
    }
    
    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }
    
    // Helper methods
    public void follow(User userToFollow) {
        this.following.add(userToFollow);
        userToFollow.getFollowers().add(this);
    }
    
    public void unfollow(User userToUnfollow) {
        this.following.remove(userToUnfollow);
        userToUnfollow.getFollowers().remove(this);
    }
    
    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", email=" + email + "]";
    }
}
