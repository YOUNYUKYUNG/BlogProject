package org.example.blogproject.repository;

import org.example.blogproject.domain.PostDraft;
import org.example.blogproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostDraftRepository extends JpaRepository<PostDraft, Long> {
    List<PostDraft> findByUser(User user);
}
