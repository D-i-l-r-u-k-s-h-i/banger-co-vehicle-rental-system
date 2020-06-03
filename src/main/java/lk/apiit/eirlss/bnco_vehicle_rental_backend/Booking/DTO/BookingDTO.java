package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.DTO;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.AdditionalEquipment.entity.AdditionalEquipment;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.Customer;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.BookingAdditionalEquipment;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.BookingStatus;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.BookingVehicle;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.entity.Vehicle;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class BookingDTO {
    private long bookingId;

    private BookingStatus bookingStatus;

    private String pickupDate;

    private String returnDate;

    private String rentalType;

    private int rentalPeriod;

    private Customer customer;

    private List<BookingVehicle> vehicleList;

    private List<BookingAdditionalEquipment> additionalEquipmentList;

    private boolean isActive;

    private boolean isExtended;

    private boolean isLatePickup;

    private boolean isLateReturn;

    private double total;

    private String lisenceNo;

//    private long vehicleId;

//    private long equipmentId;

//    private Date pickuppDatee;
//
//    private Date returnnDatee;
}
