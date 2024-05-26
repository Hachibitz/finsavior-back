package br.com.finsavior.model.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    private String username;
    private String password;
    private boolean enabled;
	@Column(name = "del_fg")
    private boolean delFg;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinTable(
			name = "user_plan",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "plan_id")
	)
	private UserPlan userPlan;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private UserProfile userProfile;

    public String getFirstAndLastName() {
        String completeName = (this.firstName+this.lastName).trim();

        String[] nameParts = completeName.split("\\s+");

        if (nameParts.length < 2) {
            return completeName;
        }

        String singleLastName = nameParts[nameParts.length - 1];

        return this.firstName + " " + singleLastName;
    }
}