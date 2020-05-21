package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.Customer;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Payment.entity.Payment;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.entity.Vehicle;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "booking", schema = "public")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long bookingId;

    private String pickupDate;

    private  String returnDate;

    private String rentalType;

    private long rentalPeriod;

    private boolean isActive;

    private boolean isExtended;

    private boolean isLatePickup;

    private boolean isLateReturn;

    @ManyToOne
    @JoinColumn(name = "booking_status_type")
    private BookingStatus bookingStatus;

    @ManyToOne
    @JoinColumn(name = "customer_id",nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;
}
