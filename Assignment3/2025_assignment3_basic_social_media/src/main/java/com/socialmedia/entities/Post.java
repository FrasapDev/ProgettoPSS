package com.socialmedia.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", length = 2000)
    private String content;

    @Column(name = "post_type")
    private String postType; // "TEXT", "IMAGE", "VIDEO"

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "is_public")
    private boolean isPublic = true;

    // Many-to-One: Post belongs to a User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // One-to-Many: Post has Comments
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    // MANY-TO-MANY: Post has Hashtags
    @ManyToMany
    @JoinTable(
            name = "post_hashtags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    private Set<Hashtag> hashtags = new HashSet<>();

    // MANY-TO-MANY: Post liked by Users
    @ManyToMany
    @JoinTable(
            name = "post_likes",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likedBy = new HashSet<>();

    // Constructors
    public Post() {
        this.timestamp = LocalDateTime.now();
    }

    public Post(String content, String postType, User author) {
        this.content = content;
        this.postType = postType;
        this.author = author;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Set<Hashtag> getHashtags() {
        return hashtags;
    }

    public void setHashtags(Set<Hashtag> hashtags) {
        this.hashtags = hashtags;
    }

    public Set<User> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(Set<User> likedBy) {
        this.likedBy = likedBy;
    }

    // Helper methods
    public void addComment(Comment comment) {
        comment.setPost(this);
        this.comments.add(comment);
    }

    public void addHashtag(Hashtag hashtag) {
        this.hashtags.add(hashtag);
        hashtag.getPosts().add(this);
        hashtag.incrementUsage();
    }

    public void like(User user) {
        this.likedBy.add(user);
    }

    public void unlike(User user) {
        this.likedBy.remove(user);
    }

    public int getLikeCount() {
        return this.likedBy.size();
    }

    public int getCommentCount() {
        return this.comments.size();
    }

    @Override
    public String toString() {
        return "Post{id=" + id + ", content='" + content.substring(0, Math.min(content.length(), 50)) +
                "...', type='" + postType + "', author=" + (author != null ? author.getUsername() : "null") + "}";
    }
}