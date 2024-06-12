package br.com.finsavior.model.entities;

import br.com.finsavior.model.enums.Flag;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "terms_and_conditions")
@Data
public class TermsAndConditions {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "terms_and_conditions_content")
    private String content;

    @Column(name = "version")
    private String version;

    @Column(name = "validity_start_date")
    private LocalDateTime validityStartDate;

    @Column(name = "del_fg")
    @Enumerated(EnumType.STRING)
    private Flag delFg;
    @Column(name = "tc_insert_dtm")
    private LocalDateTime userInsDtm;
    @Column(name = "tc_insert_id")
    private String userInsId;
    @Column(name = "tc_update_dtm")
    private LocalDateTime userUpdDtm;
    @Column(name = "tc_update_id")
    private String userUpdId;
}
