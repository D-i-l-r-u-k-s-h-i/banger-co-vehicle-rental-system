package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.DTO;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.AdditionalEquipment.entity.AdditionalEquipment;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.entity.Vehicle;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AvailableBookingItemsDTO {

    List<Vehicle> vehicleList;

    List<AdditionalEquipment> additionalEquipmentList;

    long vehicleId;

    long equipmentId;

    long bookingId;
}
