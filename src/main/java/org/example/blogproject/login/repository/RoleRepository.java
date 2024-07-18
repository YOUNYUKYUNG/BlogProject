package org.example.blogproject.login.repository;

import org.example.blogproject.login.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(String roleName); // 메서드 이름 수정
}
