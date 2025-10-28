package com.loopers.domain.user;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface UserRepository {

    Optional<User> findByUserId(String id);

    void save(User user);
}
