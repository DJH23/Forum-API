package com.LessonLab.forum.ModelTests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Vote;

public class VoteTest {
    @Test
    public void testGettersAndSetters() {
        // Arrange
        Vote vote = new Vote();
        Long voteId = 1L;
        User user = new User();
        Content content = new Content() {
        }; // Create an anonymous subclass because Content is abstract
        boolean upVote = true;

        // Act
        vote.setVoteId(voteId);
        vote.setUser(user);
        vote.setContent(content);
        vote.setUpVote(upVote);

        // Assert
        assertEquals(voteId, vote.getVoteId());
        assertEquals(user, vote.getUser());
        assertEquals(content, vote.getContent());
        assertEquals(upVote, vote.isUpVote());
    }
}
