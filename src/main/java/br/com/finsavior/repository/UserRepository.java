package br.com.finsavior.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.finsavior.model.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);

    User getById(Long id);
}