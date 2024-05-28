package br.com.finsavior.repository;

import br.com.finsavior.model.entities.PlanChangeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanHistoryRepository extends JpaRepository<PlanChangeHistory, Long> {
}
