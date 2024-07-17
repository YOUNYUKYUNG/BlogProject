package org.example.blogproject.post.service;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.post.domain.Post;
import org.example.blogproject.post.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    public Optional<Post> findPostById(Long id) {
        return postRepository.findById(id);
    }
    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }
}
