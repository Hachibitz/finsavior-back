package br.com.finsavior.repository;

import br.com.finsavior.model.entities.ExternalUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalUserRepository extends JpaRepository<ExternalUser, Long> {
    ExternalUser findBySubscriptionId(String subscriptionId);
}
