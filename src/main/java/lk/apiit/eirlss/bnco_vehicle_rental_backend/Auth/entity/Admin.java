package lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "admin" ,schema = "public")
public class Admin implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long adminId;

    private String username;

    private String password;

    private String email;

    private String contactNo;

    @ManyToOne
    @JoinColumn(name = "role_id",nullable = false)
    private Role role;
}
