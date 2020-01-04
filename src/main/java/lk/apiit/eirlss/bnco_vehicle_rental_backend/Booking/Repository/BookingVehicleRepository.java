package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Repository;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.Customer;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.Booking;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.BookingStatus;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.BookingVehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingVehicleRepository extends JpaRepository<BookingVehicle,Long> {

    List<BookingVehicle> getAllByBooking(Booking booking);

    List<BookingVehicle> getAllByBooking_CustomerAndBooking_BookingStatus(Customer customer,BookingStatus bookingStatus);

    BookingVehicle getByBooking_BookingIdAndVehicle_VehicleId(long bookingId,long vehicleId);
}
