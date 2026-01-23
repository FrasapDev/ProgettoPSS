package com.socialmedia.dao;

import com.socialmedia.entities.Comment;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class CommentDAO extends GenericDAO<Comment> {

    public CommentDAO(EntityManager entityManager) {
        super(entityManager, Comment.class);
    }

    // Find comments by post
    public List<Comment> findByPost(Long postId) {
        TypedQuery<Comment> query = entityManager.createQuery(
                "SELECT c FROM Comment c WHERE c.post.id = :postId AND c.parentComment IS NULL " +
                        "ORDER BY c.timestamp DESC", Comment.class);
        query.setParameter("postId", postId);
        return query.getResultList();
    }

    // Find comments by author
    public List<Comment> findByAuthor(Long userId) {
        TypedQuery<Comment> query = entityManager.createQuery(
                "SELECT c FROM Comment c WHERE c.author.id = :userId ORDER BY c.timestamp DESC", Comment.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    // Find replies to a comment
    public List<Comment> findReplies(Long commentId) {
        TypedQuery<Comment> query = entityManager.createQuery(
                "SELECT c FROM Comment c WHERE c.parentComment.id = :commentId ORDER BY c.timestamp", Comment.class);
        query.setParameter("commentId", commentId);
        return query.getResultList();
    }
}