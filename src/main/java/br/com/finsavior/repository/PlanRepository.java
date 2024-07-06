package br.com.finsavior.repository;

import br.com.finsavior.model.entities.Plan;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    @NotNull
    @Query(value = "SELECT * FROM plans WHERE plan_id = :planId AND del_fg <> 'S'", nativeQuery = true)
    public Plan getById(@NotNull String planId);
}
