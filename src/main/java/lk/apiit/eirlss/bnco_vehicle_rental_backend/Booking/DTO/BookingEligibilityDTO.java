package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingEligibilityDTO {

    private String lisenceOffenseStatus;

    private String insuranceOffenseStatus;

    private boolean bookingeligibility;
}
