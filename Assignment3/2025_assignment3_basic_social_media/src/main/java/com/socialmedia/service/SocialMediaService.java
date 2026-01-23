package com.socialmedia.service;

import com.socialmedia.dao.*;
import com.socialmedia.entities.*;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class SocialMediaService {

    private UserDAO userDAO;
    private PostDAO postDAO;
    private CommentDAO commentDAO;
    private HashtagDAO hashtagDAO;

    public SocialMediaService(EntityManager em) {
        this.userDAO = new UserDAO(em);
        this.postDAO = new PostDAO(em);
        this.commentDAO = new CommentDAO(em);
        this.hashtagDAO = new HashtagDAO(em);
    }

    // Business Logic Methods

    public boolean isUsernameAvailable(String username) {
        return userDAO.findByUsername(username) == null;
    }

    public boolean isEmailAvailable(String email) {
        return userDAO.findByEmail(email) == null;
    }

    public void validateUserCreation(String username, String email) {
        if (!isUsernameAvailable(username)) {
            throw new IllegalArgumentException("Username '" + username + "' is already taken");
        }
        if (!isEmailAvailable(email)) {
            throw new IllegalArgumentException("Email '" + email + "' is already registered");
        }
    }

    public Post createPostWithValidation(String content, String postType, Long authorId, List<String> hashtags) {
        User author = userDAO.findById(authorId);
        if (author == null) {
            throw new IllegalArgumentException("User not found with id: " + authorId);
        }

        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }

        if (content.length() > 2000) {
            throw new IllegalArgumentException("Post content exceeds maximum length of 2000 characters");
        }

        // Create post
        Post post = new Post(content, postType, author);
        postDAO.create(post);

        // Add hashtags
        if (hashtags != null) {
            for (String tag : hashtags) {
                Hashtag hashtag = hashtagDAO.findOrCreate(tag);
                post.addHashtag(hashtag);
            }
            postDAO.update(post);
        }

        return post;
    }

    public Comment createCommentWithValidation(String content, Long postId, Long authorId) {
        Post post = postDAO.findById(postId);
        User author = userDAO.findById(authorId);

        if (post == null) {
            throw new IllegalArgumentException("Post not found with id: " + postId);
        }
        if (author == null) {
            throw new IllegalArgumentException("User not found with id: " + authorId);
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }
        if (content.length() > 1000) {
            throw new IllegalArgumentException("Comment content exceeds maximum length of 1000 characters");
        }

        Comment comment = new Comment(content, post, author);
        commentDAO.create(comment);
        post.addComment(comment);
        author.addComment(comment);

        return comment;
    }

    // Statistics Methods

    public long getUserCount() {
        return userDAO.count();
    }

    public long getPostCount() {
        return postDAO.count();
    }

    public long getCommentCount() {
        return commentDAO.count();
    }

    public User getMostActiveUser() {
        String jpql = "SELECT u FROM User u " +
                "WHERE u.id IN (" +
                "  SELECT p.author.id FROM Post p GROUP BY p.author.id " +
                "  ORDER BY COUNT(p) DESC" +
                ")";
        List<User> users = userDAO.entityManager.createQuery(jpql, User.class)
                .setMaxResults(1)
                .getResultList();
        return users.isEmpty() ? null : users.get(0);
    }

    public Post getMostLikedPost() {
        String jpql = "SELECT p FROM Post p " +
                "ORDER BY SIZE(p.likedBy) DESC";
        List<Post> posts = postDAO.entityManager.createQuery(jpql, Post.class)
                .setMaxResults(1)
                .getResultList();
        return posts.isEmpty() ? null : posts.get(0);
    }

    public List<Hashtag> getTrendingHashtags(int limit) {
        return hashtagDAO.findTrendingHashtags(limit);
    }

    // Cleanup Methods

    public void deleteInactiveUsers(LocalDateTime lastActiveBefore) {
        String jpql = "SELECT u FROM User u " +
                "WHERE u.id NOT IN (" +
                "  SELECT p.author.id FROM Post p WHERE p.timestamp > :date" +
                ") " +
                "AND u.id NOT IN (" +
                "  SELECT c.author.id FROM Comment c WHERE c.timestamp > :date" +
                ")";

        List<User> inactiveUsers = userDAO.entityManager.createQuery(jpql, User.class)
                .setParameter("date", lastActiveBefore)
                .getResultList();

        for (User user : inactiveUsers) {
            userDAO.delete(user);
        }
    }
}