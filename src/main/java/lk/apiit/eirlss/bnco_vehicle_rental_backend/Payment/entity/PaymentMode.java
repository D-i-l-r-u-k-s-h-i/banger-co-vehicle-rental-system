package lk.apiit.eirlss.bnco_vehicle_rental_backend.Payment.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "payment_mode" ,schema = "public")
public class PaymentMode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long payMethodId;

    private String payMethod;
}
