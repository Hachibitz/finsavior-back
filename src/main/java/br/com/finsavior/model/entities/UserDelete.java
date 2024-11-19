package br.com.finsavior.model.entities;

import br.com.finsavior.model.enums.Flag;
import br.com.finsavior.model.enums.UserAccountDeleteStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "user_delete")
public class UserDelete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "status_id")
    private Integer statusId;

    @Column(name = "del_fg")
    @Enumerated(EnumType.STRING)
    private Flag delFg;
    @Column(name = "ud_insert_dtm")
    private LocalDateTime userInsDtm;
    @Column(name = "ud_insert_id")
    private String userInsId;
    @Column(name = "ud_update_dtm")
    private LocalDateTime userUpdDtm;
    @Column(name = "ud_update_id")
    private String userUpdId;
}
