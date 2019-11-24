package lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity;

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
    private long Id;

    private String username;

    private String password;//encrypted string

    @ManyToOne
    @JoinColumn(name = "role_id",nullable = false)
    private Role role;

}
