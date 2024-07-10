package org.example.blogproject.repository;

import org.example.blogproject.domain.PostSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostSeriesRepository extends JpaRepository<PostSeries, Long> {
}
