package com.socialmedia.dao;

import com.socialmedia.entities.Hashtag;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

public class HashtagDAO extends GenericDAO<Hashtag> {

    public HashtagDAO(EntityManager entityManager) {
        super(entityManager, Hashtag.class);
    }

    // Find or create hashtag
    public Hashtag findOrCreate(String tag) {
        Hashtag hashtag = findByTag(tag);
        if (hashtag == null) {
            hashtag = new Hashtag(tag);
            create(hashtag);
        }
        return hashtag;
    }

    // Find by tag
    public Hashtag findByTag(String tag) {
        try {
            return entityManager.find(Hashtag.class, tag);
        } catch (NoResultException e) {
            return null;
        }
    }

    // Find trending hashtags
    public List<Hashtag> findTrendingHashtags(int limit) {
        TypedQuery<Hashtag> query = entityManager.createQuery(
                "SELECT h FROM Hashtag h ORDER BY h.usageCount DESC", Hashtag.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    // Find hashtags used by a user
    public List<Hashtag> findHashtagsUsedByUser(Long userId) {
        TypedQuery<Hashtag> query = entityManager.createQuery(
                "SELECT DISTINCT h FROM Hashtag h JOIN h.posts p " +
                        "WHERE p.author.id = :userId " +
                        "ORDER BY h.usageCount DESC", Hashtag.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
}