package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Repository;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.Customer;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.Booking;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.BookingAdditionalEquipment;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingAdditionalEquipsRepository extends JpaRepository<BookingAdditionalEquipment,Long> {
    List<BookingAdditionalEquipment> getAllByBooking(Booking booking);

    List<BookingAdditionalEquipment> getAllByBooking_CustomerAndBooking_BookingStatus(Customer customer, BookingStatus bookingStatus);

    BookingAdditionalEquipment getByBooking_BookingIdAndAndEquipment_EquipmentId(long bookingId,long equipmentId);
}
