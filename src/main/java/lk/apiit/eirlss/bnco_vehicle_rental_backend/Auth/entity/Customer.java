package lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "customer" ,schema = "public")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long customerId;

    private String customerName;

    private String customerContactNo;

    private String customerAddress;

    private String customerStatus;

    private String customerUserName;

    private String customerPassword; //encrypted string

    private String customerEmail;

    private boolean isBlacklisted;

    @ManyToOne
    @JoinColumn(name = "role_id",nullable = false)
    private Role role;
}
