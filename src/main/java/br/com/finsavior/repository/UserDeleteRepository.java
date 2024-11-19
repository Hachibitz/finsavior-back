package br.com.finsavior.repository;

import br.com.finsavior.model.entities.UserDelete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserDeleteRepository extends JpaRepository<UserDelete, Integer> {

    @Query(value = "SELECT * FROM user_delete WHERE user_id = :userId AND del_fg <> 'S'", nativeQuery = true)
    public UserDelete findByUserId(Long userId);
}
