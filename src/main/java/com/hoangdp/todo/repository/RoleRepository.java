package com.hoangdp.todo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hoangdp.todo.entity.Role;
import com.hoangdp.todo.enums.RoleEnum;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleEnum name);

    @Query(value = """
            SELECT r.*
            FROM roles r
                JOIN user_role ur on r.id = ur.role_id
                    JOIN users u on u.id = ur.user_id
            WHERE u.id= :userId
            """, nativeQuery = true)
    List<Role> findAllByUserId(@Param("userId") Long userId);
}
