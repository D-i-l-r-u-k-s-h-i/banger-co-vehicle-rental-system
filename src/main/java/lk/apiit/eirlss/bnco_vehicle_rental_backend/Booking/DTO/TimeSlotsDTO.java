package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TimeSlotsDTO {
    private LocalDateTime date;

    private int hour;

    private int minute;
}
