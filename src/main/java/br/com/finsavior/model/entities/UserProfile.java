package br.com.finsavior.model.entities;

import br.com.finsavior.model.enums.Flag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profile")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfile {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "profile_picture")
    @Lob
    private byte[] profilePicture;

    @Column(name = "plan_id")
    private String planId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "del_fg")
    @Enumerated(EnumType.STRING)
    private Flag delFg;
    @Column(name = "upf_insert_dtm")
    private LocalDateTime userInsDtm;
    @Column(name = "upf_insert_id")
    private String userInsId;
    @Column(name = "upf_update_dtm")
    private LocalDateTime userUpdDtm;
    @Column(name = "upf_update_id")
    private String userUpdId;
}
