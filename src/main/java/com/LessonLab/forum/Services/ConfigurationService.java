package com.LessonLab.forum.Services;

import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {
    private int voteThreshold = 10;  // Default value

    public int getVoteThreshold() {
        return voteThreshold;
    }

    public void setVoteThreshold(int newThreshold) {
        voteThreshold = newThreshold;
    }
}
