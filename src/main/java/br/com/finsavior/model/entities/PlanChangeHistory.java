package br.com.finsavior.model.entities;

import br.com.finsavior.model.enums.Flag;
import jakarta.persistence.*;

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
    private Long userId;

    @Column(name = "external_user_id")
    private String externalUserId;

    @Column(name = "plan_id")
    private String planId;

    @Column(name = "plan_type")
    private String planType;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "del_fg")
    @Enumerated(EnumType.STRING)
    private Flag delFg;
    @Column(name = "eu_insert_dtm")
    private LocalDateTime userInsDtm;
    @Column(name = "eu_insert_id")
    private String userInsId;
    @Column(name = "eu_update_dtm")
    private LocalDateTime userUpdDtm;
    @Column(name = "eu_update_id")
    private String userUpdId;
}
