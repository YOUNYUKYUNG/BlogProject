package org.example.blogproject.post.service;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.login.domain.User;
import org.example.blogproject.post.domain.PostDraft;
import org.example.blogproject.post.repository.PostDraftRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostDraftService {
    private final PostDraftRepository postDraftRepository;

    @Transactional
    public PostDraft save(PostDraft postDraft) {
        return postDraftRepository.save(postDraft);
    }

    @Transactional(readOnly = true)
    public List<PostDraft> findByUser(User user) {
        return postDraftRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public PostDraft findById(Long id) {
        return postDraftRepository.findById(id).orElseThrow(() -> new RuntimeException("Draft not found"));
    }

    @Transactional
    public void deleteById(Long id) {
        postDraftRepository.deleteById(id);
    }
}
