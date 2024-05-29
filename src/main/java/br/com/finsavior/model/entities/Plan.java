package br.com.finsavior.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "user_plan")
@Data
public class Plan {
    @Id
    @Column(name = "plan_id")
    private String planId;
    @Column(name = "plan_ds")
    private String planDs;
}
