package org.example.blogproject.post.repository;


import org.example.blogproject.post.domain.PostDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostDraftRepository extends JpaRepository<PostDraft, Long> {

}
