package com.loopers.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String userId);

    boolean existsUserByUserId(String userId);
}
