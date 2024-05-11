package com.LessonLab.forum.RepositoryTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.UserExtension;
import com.LessonLab.forum.Models.Vote;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Repositories.ContentRepository;
import com.LessonLab.forum.Repositories.UserRepository;
import com.LessonLab.forum.Repositories.VoteRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class VoteRepositoryTest {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContentRepository contentRepository;

    private Vote testVote;
    private UserExtension testUser;
    private Post testPost;
    private Thread testThread;

    @BeforeEach
    public void setUp() {
        // Create a test user
        testUser = new UserExtension();
        testUser.setUsername("testUser");
        userRepository.save(testUser);

        // Create a test thread
        Thread testThread = new Thread();
        testThread.setTitle("Test Thread"); // Set the title, not the name
        contentRepository.save(testThread);

        // Create a test post
        testPost = new Post();
        testPost.setContent("Test content");
        testPost.setThread(testThread);
        contentRepository.save(testPost);

        // Create a test vote
        testVote = new Vote();
        testVote.setUser(testUser);
        testVote.setContent(testPost); // Set the content to testPost
        voteRepository.save(testVote);
    }

    @Test
    public void testFindByUserAndContent() {
        // Act
        Optional<Vote> vote = voteRepository.findByUserAndContent(testUser, testPost);

        // Assert
        if (vote.isPresent()) {
            assertEquals(testUser, vote.get().getUser());
            assertEquals(testPost, vote.get().getContent());
        } else {
            fail("Vote not found");
        }
    }

    @AfterEach
    public void tearDown() {
        // Delete the test vote
        if (testVote != null) {
            voteRepository.delete(testVote);
        }

        // Delete the test post
        if (testPost != null) {
            contentRepository.delete(testPost);
        }

        // Delete the test user
        if (testUser != null) {
            userRepository.delete(testUser);
        }

        // Delete the test thread
        if (testThread != null) {
            contentRepository.delete(testThread);
        }
    }
}
