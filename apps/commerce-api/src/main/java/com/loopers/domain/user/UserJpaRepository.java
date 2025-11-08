package com.loopers.domain.user;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.userId = :userId")
    Optional<User> findByUserIdForUpdate(String userId);

    boolean existsUserByUserId(String userId);
}
