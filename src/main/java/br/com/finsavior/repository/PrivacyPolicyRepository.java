package br.com.finsavior.repository;

import br.com.finsavior.model.entities.PrivacyPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivacyPolicyRepository extends JpaRepository<PrivacyPolicy, Long> {
    @Query(value = "SELECT TOP(1) * " +
            "FROM privacy_policy " +
            "WHERE del_fg <> 'S' " +
            "AND validity_start_date < GETDATE() " +
            "ORDER BY validity_start_date DESC", nativeQuery = true)
    public PrivacyPolicy getCurrentPrivacyPolicy();
}
