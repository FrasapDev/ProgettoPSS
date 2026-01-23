package com.socialmedia.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "hashtags")
public class Hashtag {

    @Id
    private String tag; // e.g., "#Java", "#Programming"

    @Column(name = "usage_count")
    private int usageCount = 0;

    // Many-to-Many: Hashtag used in many Posts
    @ManyToMany(mappedBy = "hashtags")
    private Set<Post> posts = new HashSet<>();

    // Constructors
    public Hashtag() {
    }

    public Hashtag(String tag) {
        this.tag = tag;
    }

    // Helper method to increment usage
    public void incrementUsage() {
        this.usageCount++;
    }

    // Getters and Setters
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    @Override
    public String toString() {
        return "Hashtag{tag='" + tag + "', usageCount=" + usageCount + "}";
    }
}