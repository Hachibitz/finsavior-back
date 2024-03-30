package br.com.finsavior.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "account_type")
@Data
public class AccountType {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "type_desc")
    private String typeDesc;
    @Column(name = "del_fg")
    private char delFg;
    @Column(name = "user_insert_dtm")
    private LocalDateTime userInsDtm;
    @Column(name = "user_insert_id")
    private String userInsId;
    @Column(name = "user_update_dtm")
    private LocalDateTime userUpdDtm;
    @Column(name = "user_update_id")
    private String userUpdId;
}
