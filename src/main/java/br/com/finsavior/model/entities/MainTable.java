package br.com.finsavior.model.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "main_table")
public class MainTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long user_id;

    @Column(name = "bill_type")
    private String billType;

    @Column(name = "bill_date")
    private String billDate;

    @Column(name = "bill_name")
    private String billName;

    @Column(name = "bill_value")
    private double billValue;

    @Column(name = "bill_description")
    private String billDescription;

    @Column(name = "is_paid")
    private boolean isPaid;
}
