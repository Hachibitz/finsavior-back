package br.com.finsavior.model.entities;

import br.com.finsavior.model.enums.ExternalService;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "external_user")
@Data
public class ExternalUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subscription_id")
    private String subscriptionId;

    @Column(name = "external_user_id")
    private String externalUserId;

    @Column(name = "service_name")
    private ExternalService service;

    @ManyToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;


}
