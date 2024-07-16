package org.example.blogproject.post.repository;

import org.example.blogproject.post.service.PostSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostSeriesRepository extends JpaRepository<PostSeries, Long> {
}
