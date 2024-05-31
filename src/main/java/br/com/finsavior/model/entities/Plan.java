package br.com.finsavior.model.entities;

import br.com.finsavior.model.enums.Flag;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_plan")
@Data
public class Plan {
    @Id
    @Column(name = "plan_id")
    private String planId;
    @Column(name = "plan_ds")
    private String planDs;

    @Column(name = "del_fg")
    @Enumerated(EnumType.STRING)
    private Flag delFg;
    @Column(name = "pl_insert_dtm")
    private LocalDateTime userInsDtm;
    @Column(name = "pl_insert_id")
    private String userInsId;
    @Column(name = "pl_update_dtm")
    private LocalDateTime userUpdDtm;
    @Column(name = "pl_update_id")
    private String userUpdId;
}
