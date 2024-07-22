package org.example.blogproject.repository;

import org.example.blogproject.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(String roleName);  // 역할 이름으로 역할을 찾는 메서드
}
