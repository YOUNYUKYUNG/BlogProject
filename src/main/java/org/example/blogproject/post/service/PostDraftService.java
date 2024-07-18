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

    public PostDraft saveDraft(PostDraft draft) {
        return postDraftRepository.save(draft);
    }
}