package com.socialmedia.main;

import com.socialmedia.facade.SocialMediaFacade;
import com.socialmedia.entities.AdminUser;
import com.socialmedia.entities.RegularUser;
import com.socialmedia.entities.Post;
import com.socialmedia.entities.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class MainApp {
    public static void main(String[] args) {
        System.out.println("Social Media Application");
        SocialMediaFacade facade = new SocialMediaFacade();

        try {
            System.out.println("\nCreating Users");
            RegularUser user1 = facade.createRegularUser(
                    "john_doe",
                    "john@example.com",
                    "Software developer passionate about Java",
                    LocalDate.of(1990, 5, 15),
                    "New York"
            );
            System.out.println("Created Regular User: " + user1.getUsername());

            RegularUser user2 = facade.createRegularUser(
                    "jane_smith",
                    "jane@example.com",
                    "Graphic designer and frontend enthusiast",
                    LocalDate.of(1992, 8, 22),
                    "London"
            );
            System.out.println("Created Regular User: " + user2.getUsername());

            AdminUser admin = facade.createAdminUser(
                    "sys_admin",
                    "admin@socialmedia.com",
                    "SUPER_ADMIN",
                    "Platform Management"
            );
            System.out.println("Created Admin User: " + admin.getUsername());

            System.out.println("\nCreating Posts");
            Post post1 = facade.createPostWithHashtags(
                    "Just learned about JPA",
                    "TEXT",
                    user1.getId(),
                    Arrays.asList("#Java", "#JPA", "#Hibernate", "#Backend")
            );
            System.out.println("Created Post by " + user1.getUsername());

            Post post2 = facade.createPostWithHashtags(
                    "Working on a new UI design system",
                    "TEXT",
                    user2.getId(),
                    Arrays.asList("#React", "#TypeScript", "#Frontend", "#Design")
            );
            System.out.println("Created Post by " + user2.getUsername());

            System.out.println("\nCreating Comments");
            facade.createComment(
                    "Great post! JPA makes database operations so much easier.",
                    post1.getId(),
                    user2.getId()
            );
            System.out.println(user2.getUsername() + " commented on " + user1.getUsername() + "'s post");

            facade.createComment(
                    "Nice design work! Love the color palette.",
                    post2.getId(),
                    user1.getId()
            );
            System.out.println(user1.getUsername() + " commented on " + user2.getUsername() + "'s post");

            System.out.println("\nCreating Follow Relationships");
            facade.followUser(user1.getId(), user2.getId());
            System.out.println(user1.getUsername() + " started following " + user2.getUsername());

            facade.followUser(user2.getId(), user1.getId());
            System.out.println(user2.getUsername() + " started following " + user1.getUsername());

            System.out.println("\nLiking Posts");
            facade.likePost(user2.getId(), post1.getId());
            System.out.println(user2.getUsername() + " liked " + user1.getUsername() + "'s post");

            facade.likePost(user1.getId(), post2.getId());
            System.out.println(user1.getUsername() + " liked " + user2.getUsername() + "'s post");

            System.out.println("\nRunning Complex Searches");
            List<User> activeUsers = facade.findActiveUsers(LocalDateTime.now().minusDays(7));
            System.out.println("Active users (last 7 days): " + activeUsers.size());

            List<Post> popularJavaPosts = facade.findPopularPostsByHashtag("#Java", 1);
            System.out.println("Popular #Java posts (min 1 like): " + popularJavaPosts.size());

            List<Post> discussions = facade.findActiveDiscussions(1, 0);
            System.out.println("Active discussions (min 1 comment): " + discussions.size());

            List<User> programmingUsers = facade.findUsersEngagingWithHashtags(
                    Arrays.asList("#Java", "#Programming", "#Backend")
            );
            System.out.println("Users engaging with programming tags: " + programmingUsers.size());

            System.out.println("\nFind Operations");
            User foundUser = facade.findUserByUsername("john_doe");
            if (foundUser != null) {
                System.out.println("Found user: " + foundUser.getUsername() + " - " + foundUser.getEmail());
            }

            List<Post> userPosts = facade.findPostsByAuthor(user1.getId());
            System.out.println(user1.getUsername() + " has " + userPosts.size() + " posts");

            System.out.println("\nRunning Comprehensive Test");
            facade.testAllOperations();

        } catch (Exception e) {
            System.err.println("Error during execution: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\nCleaning Up Resources");
            facade.close();
            System.out.println("Application completed successfully!");
        }
    }
}