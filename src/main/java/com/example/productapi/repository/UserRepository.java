package com.example.productapi.repository;

import com.example.productapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA is smart enough to automatically write the SQL query
    // for this method just by reading its name!
    Optional<User> findByEmail(String email);

}