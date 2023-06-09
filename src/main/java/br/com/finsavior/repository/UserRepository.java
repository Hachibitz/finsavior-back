package br.com.finsavior.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.finsavior.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}