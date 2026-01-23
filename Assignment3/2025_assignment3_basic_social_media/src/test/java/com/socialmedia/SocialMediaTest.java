package com.socialmedia;

import com.socialmedia.entities.*;
import com.socialmedia.facade.SocialMediaFacade;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SocialMediaTest {

    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static SocialMediaFacade facade;

    private static Long regularUserId;
    private static Long adminUserId;
    private static Long postId;
    private static Long commentId;

    @BeforeAll
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("socialmedia-pu");
        em = emf.createEntityManager();
        facade = new SocialMediaFacade();
    }

    @AfterAll
    public static void tearDownClass() {
        facade.close();
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Test
    @Order(1)
    public void testCreateRegularUser() {
        RegularUser user = facade.createRegularUser(
                "testuser", "test@example.com",
                "Test bio", LocalDate.of(1995, 1, 1), "Test City"
        );

        assertNotNull(user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());

        regularUserId = user.getId();
        System.out.println("✅ Created RegularUser with ID: " + regularUserId);
    }

    @Test
    @Order(2)
    public void testCreateAdminUser() {
        AdminUser admin = facade.createAdminUser(
                "adminuser", "admin@example.com",
                "SUPER_ADMIN", "IT Department"
        );

        assertNotNull(admin.getId());
        assertEquals("adminuser", admin.getUsername());
        assertEquals("SUPER_ADMIN", admin.getAdminLevel());

        adminUserId = admin.getId();
        System.out.println("✅ Created AdminUser with ID: " + adminUserId);
    }

    @Test
    @Order(3)
    public void testFindUserById() {
        User user = facade.findUserById(regularUserId);
        assertNotNull(user);
        assertEquals(regularUserId, user.getId());
        System.out.println("✅ Found user by ID: " + user.getUsername());
    }

    @Test
    @Order(4)
    public void testCreatePostWithHashtags() {
        Post post = facade.createPostWithHashtags(
                "This is a test post about JPA and Hibernate!",
                "TEXT",
                regularUserId,
                Arrays.asList("#Java", "#JPA", "#Testing")
        );

        assertNotNull(post.getId());
        assertEquals(regularUserId, post.getAuthor().getId());
        assertEquals(3, post.getHashtags().size());

        postId = post.getId();
        System.out.println("✅ Created Post with ID: " + postId + " and hashtags");
    }

    @Test
    @Order(5)
    public void testCreateComment() {
        Comment comment = facade.createComment(
                "Great post about JPA!",
                postId,
                adminUserId
        );

        assertNotNull(comment.getId());
        assertEquals(postId, comment.getPost().getId());
        assertEquals(adminUserId, comment.getAuthor().getId());

        commentId = comment.getId();
        System.out.println("✅ Created Comment with ID: " + commentId);
    }

    @Test
    @Order(6)
    public void testCreateReply() {
        Comment reply = facade.createReply(
                "Thanks! JPA is really useful.",
                commentId,
                regularUserId
        );

        assertNotNull(reply.getId());
        assertEquals(commentId, reply.getParentComment().getId());
        System.out.println("✅ Created Reply to comment");
    }

    @Test
    @Order(7)
    public void testFollowUser() {
        facade.followUser(regularUserId, adminUserId);

        User regularUser = facade.findUserById(regularUserId);
        User adminUser = facade.findUserById(adminUserId);

        assertTrue(regularUser.getFollowing().contains(adminUser));
        assertTrue(adminUser.getFollowers().contains(regularUser));
        System.out.println("✅ User following relationship established");
    }

    @Test
    @Order(8)
    public void testLikePost() {
        facade.likePost(adminUserId, postId);

        Post post = facade.findPostById(postId);
        User admin = facade.findUserById(adminUserId);

        assertTrue(post.getLikedBy().contains(admin));
        assertEquals(1, post.getLikeCount());
        System.out.println("✅ Post liked by user");
    }

    @Test
    @Order(9)
    public void testComplexSearchActiveUsers() {
        List<User> activeUsers = facade.findActiveUsers(
                java.time.LocalDateTime.now().minusDays(1)
        );

        assertNotNull(activeUsers);
        assertTrue(activeUsers.size() >= 1);
        System.out.println("✅ Found " + activeUsers.size() + " active users");
    }

    @Test
    @Order(10)
    public void testComplexSearchPopularPostsByHashtag() {
        List<Post> popularPosts = facade.findPopularPostsByHashtag("#Java", 0);

        assertNotNull(popularPosts);
        assertTrue(popularPosts.size() >= 1);
        System.out.println("✅ Found " + popularPosts.size() + " popular posts with #Java");
    }

    @Test
    @Order(11)
    public void testComplexSearchUsersEngagingWithHashtags() {
        List<User> engagingUsers = facade.findUsersEngagingWithHashtags(
                Arrays.asList("#Java", "#JPA")
        );

        assertNotNull(engagingUsers);
        System.out.println("✅ Found " + engagingUsers.size() + " users engaging with hashtags");
    }

    @Test
    @Order(12)
    public void testComplexSearchActiveDiscussions() {
        List<Post> discussions = facade.findActiveDiscussions(1, 1);

        assertNotNull(discussions);
        assertTrue(discussions.size() >= 1);
        System.out.println("✅ Found " + discussions.size() + " active discussions");
    }

    @Test
    @Order(13)
    public void testUpdateUser() {
        User user = facade.findUserById(regularUserId);
        String originalEmail = user.getEmail();

        user.setEmail("updated@example.com");
        User updatedUser = facade.updateUser(user);

        assertEquals("updated@example.com", updatedUser.getEmail());

        // Restore original email
        updatedUser.setEmail(originalEmail);
        facade.updateUser(updatedUser);
        System.out.println("✅ User updated successfully");
    }

    @Test
    @Order(14)
    public void testAllOperations() {
        facade.testAllOperations();
        System.out.println("✅ All operations test completed");
    }
}