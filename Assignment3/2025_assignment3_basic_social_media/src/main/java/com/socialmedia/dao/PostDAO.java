package com.socialmedia.dao;

import com.socialmedia.entities.Hashtag;
import com.socialmedia.entities.Post;
import com.socialmedia.entities.User;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

public class PostDAO extends GenericDAO<Post> {

    public PostDAO(EntityManager entityManager) {
        super(entityManager, Post.class);
    }

    // Find posts by author
    public List<Post> findByAuthor(Long userId) {
        TypedQuery<Post> query = entityManager.createQuery(
                "SELECT p FROM Post p WHERE p.author.id = :userId ORDER BY p.timestamp DESC", Post.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    // Find posts with specific hashtag
    public List<Post> findByHashtag(String hashtag) {
        TypedQuery<Post> query = entityManager.createQuery(
                "SELECT p FROM Post p JOIN p.hashtags h WHERE h.tag = :hashtag ORDER BY p.timestamp DESC", Post.class);
        query.setParameter("hashtag", hashtag);
        return query.getResultList();
    }

    // Find trending posts (most liked)
    public List<Post> findTrendingPosts(int limit) {
        TypedQuery<Post> query = entityManager.createQuery(
                "SELECT p FROM Post p LEFT JOIN p.likedBy l " +
                        "WHERE p.isPublic = true " +
                        "GROUP BY p " +
                        "ORDER BY COUNT(l) DESC, p.timestamp DESC", Post.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    // Find posts between dates
    public List<Post> findPostsBetweenDates(LocalDateTime start, LocalDateTime end) {
        TypedQuery<Post> query = entityManager.createQuery(
                "SELECT p FROM Post p WHERE p.timestamp BETWEEN :start AND :end ORDER BY p.timestamp DESC", Post.class);
        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getResultList();
    }

    // Find posts liked by a user
    public List<Post> findPostsLikedByUser(Long userId) {
        TypedQuery<Post> query = entityManager.createQuery(
                "SELECT p FROM Post p JOIN p.likedBy u WHERE u.id = :userId ORDER BY p.timestamp DESC", Post.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    // COMPLEX SEARCH: Find posts with multiple hashtags
    public List<Post> findPostsWithHashtags(List<String> hashtags) {
        TypedQuery<Post> query = entityManager.createQuery(
                "SELECT p FROM Post p JOIN p.hashtags h " +
                        "WHERE h.tag IN :hashtags " +
                        "GROUP BY p " +
                        "HAVING COUNT(DISTINCT h.tag) = :tagCount " +
                        "ORDER BY p.timestamp DESC", Post.class);
        query.setParameter("hashtags", hashtags);
        query.setParameter("tagCount", (long) hashtags.size());
        return query.getResultList();
    }
}