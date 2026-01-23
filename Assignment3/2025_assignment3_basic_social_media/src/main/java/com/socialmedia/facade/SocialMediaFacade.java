package com.socialmedia.facade;

import com.socialmedia.dao.*;
import com.socialmedia.entities.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class SocialMediaFacade {

    private EntityManagerFactory emf;
    private EntityManager em;

    private UserDAO userDAO;
    private PostDAO postDAO;
    private CommentDAO commentDAO;
    private HashtagDAO hashtagDAO;

    public SocialMediaFacade() {
        emf = Persistence.createEntityManagerFactory("socialmedia-pu");
        em = emf.createEntityManager();

        userDAO = new UserDAO(em);
        postDAO = new PostDAO(em);
        commentDAO = new CommentDAO(em);
        hashtagDAO = new HashtagDAO(em);
    }

    // ========== USER OPERATIONS ==========

    // CREATE
    public RegularUser createRegularUser(String username, String email, String bio,
                                         LocalDate birthDate, String location) {
        RegularUser user = new RegularUser(username, email, bio, birthDate, location);
        userDAO.create(user);
        return user;
    }

    public AdminUser createAdminUser(String username, String email, String adminLevel, String department) {
        AdminUser user = new AdminUser(username, email, adminLevel, department);
        userDAO.create(user);
        return user;
    }

    // READ
    public User findUserById(Long id) {
        return userDAO.findById(id);
    }

    public User findUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    public List<User> findAllUsers() {
        return userDAO.findAll();
    }

    // UPDATE
    public User updateUser(User user) {
        return userDAO.update(user);
    }

    // DELETE
    public void deleteUser(Long id) {
        userDAO.deleteById(id);
    }

    // ========== POST OPERATIONS ==========

    // CREATE
    public Post createPost(String content, String postType, Long authorId) {
        User author = userDAO.findById(authorId);
        if (author == null) {
            throw new IllegalArgumentException("Author not found with id: " + authorId);
        }

        Post post = new Post(content, postType, author);
        postDAO.create(post);
        author.addPost(post);
        return post;
    }

    public Post createPostWithHashtags(String content, String postType, Long authorId, List<String> hashtagStrings) {
        Post post = createPost(content, postType, authorId);

        for (String tag : hashtagStrings) {
            Hashtag hashtag = hashtagDAO.findOrCreate(tag);
            post.addHashtag(hashtag);
        }

        postDAO.update(post);
        return post;
    }

    // READ
    public Post findPostById(Long id) {
        return postDAO.findById(id);
    }

    public List<Post> findPostsByAuthor(Long authorId) {
        return postDAO.findByAuthor(authorId);
    }

    public List<Post> findTrendingPosts(int limit) {
        return postDAO.findTrendingPosts(limit);
    }

    // UPDATE
    public Post updatePost(Post post) {
        return postDAO.update(post);
    }

    // DELETE
    public void deletePost(Long id) {
        postDAO.deleteById(id);
    }

    // ========== COMMENT OPERATIONS ==========

    // CREATE
    public Comment createComment(String content, Long postId, Long authorId) {
        Post post = postDAO.findById(postId);
        User author = userDAO.findById(authorId);

        if (post == null || author == null) {
            throw new IllegalArgumentException("Post or Author not found");
        }

        Comment comment = new Comment(content, post, author);
        commentDAO.create(comment);
        post.addComment(comment);
        author.addComment(comment);

        return comment;
    }

    public Comment createReply(String content, Long parentCommentId, Long authorId) {
        Comment parent = commentDAO.findById(parentCommentId);
        User author = userDAO.findById(authorId);

        if (parent == null || author == null) {
            throw new IllegalArgumentException("Parent comment or Author not found");
        }

        Comment reply = new Comment(content, parent.getPost(), author);
        reply.setParentComment(parent);
        commentDAO.create(reply);
        parent.addReply(reply);

        return reply;
    }

    // READ
    public Comment findCommentById(Long id) {
        return commentDAO.findById(id);
    }

    public List<Comment> findCommentsByPost(Long postId) {
        return commentDAO.findByPost(postId);
    }

    // ========== RELATIONSHIP OPERATIONS ==========

    public void followUser(Long followerId, Long followedId) {
        User follower = userDAO.findById(followerId);
        User followed = userDAO.findById(followedId);

        if (follower != null && followed != null) {
            follower.follow(followed);
            userDAO.update(follower);
            userDAO.update(followed);
        }
    }

    public void likePost(Long userId, Long postId) {
        User user = userDAO.findById(userId);
        Post post = postDAO.findById(postId);

        if (user != null && post != null) {
            post.like(user);
            postDAO.update(post);
        }
    }

    // ========== COMPLEX SEARCH OPERATIONS ==========

    // 1. Find active users (posted recently and have followers)
    public List<User> findActiveUsers(LocalDateTime since) {
        String jpql = "SELECT DISTINCT u FROM User u " +
                "WHERE u.id IN (" +
                "  SELECT p.author.id FROM Post p WHERE p.timestamp > :since" +
                ")";

        return em.createQuery(jpql, User.class)
                .setParameter("since", since)
                .getResultList();
    }

    // 2. Find popular posts by hashtag and likes
    public List<Post> findPopularPostsByHashtag(String hashtag, int minLikes) {
        String jpql = "SELECT p FROM Post p " +
                "JOIN p.hashtags h " +
                "WHERE h.tag = :hashtag " +
                "AND SIZE(p.likedBy) >= :minLikes " +
                "ORDER BY SIZE(p.likedBy) DESC, p.timestamp DESC";

        return em.createQuery(jpql, Post.class)
                .setParameter("hashtag", hashtag)
                .setParameter("minLikes", minLikes)
                .getResultList();
    }

    // 3. Find users engaging with specific hashtags
    public List<User> findUsersEngagingWithHashtags(List<String> hashtags) {
        String jpql = "SELECT DISTINCT u FROM User u " +
                "JOIN u.posts p " +
                "JOIN p.hashtags h " +
                "WHERE h.tag IN :hashtags " +
                "GROUP BY u " +
                "HAVING COUNT(DISTINCT h.tag) >= :minTags ";

        return em.createQuery(jpql, User.class)
                .setParameter("hashtags", hashtags)
                .setParameter("minTags", (long) (hashtags.size() / 2 + 1))
                .getResultList();
    }

    // 4. Find discussions (posts with many comments and replies)
    public List<Post> findActiveDiscussions(int minComments, int minReplies) {
        String jpql = "SELECT p FROM Post p " +
                "WHERE p.id IN (" +
                "  SELECT p2.id FROM Post p2 " +
                "  WHERE (" +
                "    SELECT COUNT(c) FROM Comment c WHERE c.post = p2" +
                "  ) >= :minComments " +
                "  AND (" +
                "    SELECT COUNT(c2) FROM Comment c2 " +
                "    WHERE c2.post = p2 AND (" +
                "      SELECT COUNT(r) FROM Comment r WHERE r.parentComment = c2" +
                "    ) > 0" +
                "  ) >= :minReplies" +
                ")";

        return em.createQuery(jpql, Post.class)
                .setParameter("minComments", (long) minComments)  // CAST to Long!
                .setParameter("minReplies", (long) minReplies)    // CAST to Long!
                .getResultList();
    }

    // ========== UTILITY METHODS ==========

    public void close() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    // Test method
    public void testAllOperations() {
        System.out.println("=== Testing Social Media Facade ===");

        // Create users
        RegularUser user1 = createRegularUser("john_doe", "john@example.com",
                "Software developer", LocalDate.of(1990, 5, 15), "New York");
        RegularUser user2 = createRegularUser("jane_smith", "jane@example.com",
                "Graphic designer", LocalDate.of(1992, 8, 22), "London");

        // Follow each other
        followUser(user1.getId(), user2.getId());

        // Create posts with hashtags
        Post post1 = createPostWithHashtags("Learning JPA with Hibernate is fun!", "TEXT",
                user1.getId(), Arrays.asList("#Java", "#JPA", "#Hibernate"));
        Post post2 = createPostWithHashtags("Beautiful day for coding!", "TEXT",
                user2.getId(), Arrays.asList("#Programming", "#Java"));

        // Like posts
        likePost(user2.getId(), post1.getId());
        likePost(user1.getId(), post2.getId());

        // Create comments
        Comment comment1 = createComment("Great post about JPA!", post1.getId(), user2.getId());
        Comment reply1 = createReply("Thanks! JPA is indeed powerful.", comment1.getId(), user1.getId());

        // Search operations
        System.out.println("\n=== Search Results ===");
        System.out.println("Active users: " + findActiveUsers(LocalDateTime.now().minusDays(7)).size());
        System.out.println("Popular #Java posts: " + findPopularPostsByHashtag("#Java", 1).size());

        System.out.println("\n✅ All operations completed successfully!");
    }
}