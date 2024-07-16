package org.example.blogproject.post.repository;

import org.example.blogproject.login.domain.User;
import org.example.blogproject.post.domain.PostDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostDraftRepository extends JpaRepository<PostDraft, Long> {
    List<PostDraft> findByUser(User user);
}
