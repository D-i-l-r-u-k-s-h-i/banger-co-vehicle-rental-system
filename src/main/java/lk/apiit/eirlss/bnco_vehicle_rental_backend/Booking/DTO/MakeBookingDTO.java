package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.DTO;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.AdditionalEquipment.entity.AdditionalEquipment;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.entity.Vehicle;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MakeBookingDTO {
    private long id;

    private long bookingId;

    private Date pickupDate;

    private Date returnDate;

    private long vehicleId;

    private long equipmentId;

}
