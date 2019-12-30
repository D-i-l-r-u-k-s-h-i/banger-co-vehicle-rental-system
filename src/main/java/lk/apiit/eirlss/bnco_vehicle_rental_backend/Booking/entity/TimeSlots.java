package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "time_slots", schema = "public")
public class TimeSlots {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long slotId;

    private LocalDateTime timeSlot;

    @ManyToOne   //(cascade =  CascadeType.ALL)
    @JoinColumn(name = "booking_id")
    private Booking booking;
}
