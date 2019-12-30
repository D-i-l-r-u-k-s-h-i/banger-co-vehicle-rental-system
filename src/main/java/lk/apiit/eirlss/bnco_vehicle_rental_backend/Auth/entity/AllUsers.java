package lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "all_users" ,schema = "public")
public class AllUsers implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    //encrypted string
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id",nullable = false)
    private Role role;

    @JsonIgnore
    @OneToOne(cascade =  CascadeType.ALL, mappedBy = "user")
    private Customer customer;

    @JsonIgnore
    @OneToOne(cascade =  CascadeType.ALL, mappedBy = "user")
    private Admin admin;
}
