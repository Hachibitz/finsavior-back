package br.com.finsavior.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "plan_history")
@Data
@Builder
public class PlanChangeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "external_user_id")
    private String externalUserId;

    @Column(name = "plan_id")
    private String planId;

    @Column(name = "plan_type")
    private String planType;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

}
