package com.LessonLab.forum.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Repositories.PostRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;


}
