package com.darwgom.tradibankapi.domain.repositories;

import com.darwgom.tradibankapi.domain.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    @NonNull
    @Override
    List<User> findAll();
}