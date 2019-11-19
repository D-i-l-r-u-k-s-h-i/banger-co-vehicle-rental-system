package lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "admin" ,schema = "public")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long adminId;

    private String adminName;

    private String adminPassword;//encrypted string

    @OneToOne
    @JoinColumn(name = "role_id",nullable = false)
    private Role role;

}
