package lk.apiit.eirlss.bnco_vehicle_rental_backend.Payment.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "payment" ,schema = "public")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long paymentId;

    private double amount;

    private LocalDateTime timeStamp;

    @OneToOne
    @JoinColumn(name = "payMethod_id",nullable = false)
    private PaymentMode paymentMode;

}
