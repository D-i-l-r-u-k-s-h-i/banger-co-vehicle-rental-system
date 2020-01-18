package lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "customer" ,schema = "public")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long customerId;

    private String customerFName;

    private String customerLName;

    private String customerContactNo;

    private String emergencyContactNo;

//    private String customerAddress;

    private String customerStatus;

    private String customerUserName;

    private String customerEmail;

    private boolean isBlacklisted;

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id",nullable = false)
    private AllUsers user;


//    @ManyToOne
//    @JoinColumn(name = "role_id",nullable = false)
//    private Role role;
}
