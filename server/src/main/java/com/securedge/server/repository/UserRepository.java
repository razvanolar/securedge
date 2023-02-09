package com.securedge.server.repository;

import com.securedge.server.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    List<User> findAll();

    Optional<User> findFirstByEmail(String email);
}
