package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "booking_status" ,schema = "public")
@NoArgsConstructor
public class BookingStatus {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private long statusId;

    @Enumerated(EnumType.STRING)
    @NaturalId
    @Column(length = 60)
    private BookingStatusType bookingStatusType;

    public BookingStatus(BookingStatusType bookingStatusType) {
        this.bookingStatusType = bookingStatusType;
    }
}
