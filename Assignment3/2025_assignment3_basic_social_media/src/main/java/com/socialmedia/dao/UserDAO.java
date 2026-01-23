package com.socialmedia.dao;

import com.socialmedia.entities.User;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

public class UserDAO extends GenericDAO<User> {

    public UserDAO(EntityManager entityManager) {
        super(entityManager, User.class);
    }

    // Find by username
    public User findByUsername(String username) {
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    // Find by email
    public User findByEmail(String email) {
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    // Find users who joined after a specific date
    public List<User> findUsersJoinedAfter(java.time.LocalDate date) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.joinDate > :date ORDER BY u.joinDate DESC", User.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    // Find users by location (RegularUsers only)
    public List<User> findRegularUsersByLocation(String location) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM RegularUser u WHERE u.location = :location", User.class);
        query.setParameter("location", location);
        return query.getResultList();
    }

    // Find followers of a user
    public List<User> findFollowers(Long userId) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u JOIN u.following f WHERE f.id = :userId", User.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    // Find users following a user
    public List<User> findFollowing(Long userId) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT f FROM User u JOIN u.following f WHERE u.id = :userId", User.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
}