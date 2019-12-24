package lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    //not encrypting since they are admin given passwords
    private String password;

    private String email;

    private String contactNo;

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id",nullable = false)
    private AllUsers user;

//    @ManyToOne
//    @JoinColumn(name = "role_id",nullable = false)
//    private Role role;
}
