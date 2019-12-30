package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class BookingDTO {
    private long bookingId;

    private String bookingStatus;

    private Date pickupDate;

    private Date returnDate;

    private String rentalType;

    private int rentalPeriod;

    private boolean isActive;

    private boolean isExtended;

    private boolean isLate;

    private long vehicleId;
}
