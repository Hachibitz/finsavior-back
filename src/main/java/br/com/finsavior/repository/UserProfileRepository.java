package br.com.finsavior.repository;

import br.com.finsavior.model.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    @Query(value = "SELECT * FROM user_profile WHERE user_id = :userId", nativeQuery = true)
    public UserProfile getByUserId(Long userId);
}
