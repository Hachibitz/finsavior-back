package br.com.finsavior.repository;

import br.com.finsavior.model.entities.TermsAndConditions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TermsAndConditionsRepository extends JpaRepository<TermsAndConditions, Long> {
    @Query(value = "SELECT TOP(1) * " +
            "FROM terms_and_conditions " +
            "WHERE del_fg <> 'S' " +
            "AND validity_start_date < GETDATE() " +
            "ORDER BY validity_start_date DESC", nativeQuery = true)
    public TermsAndConditions getCurrentTerm();
}
