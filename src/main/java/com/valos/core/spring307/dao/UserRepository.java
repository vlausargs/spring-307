package com.valos.core.spring307.dao;

import com.valos.core.spring307.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findById(String email);

}