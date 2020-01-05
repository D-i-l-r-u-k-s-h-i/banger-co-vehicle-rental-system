package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Repository;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.Customer;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.Booking;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {
    Booking findBookingByBookingId(long id);

    List<Booking> findBookingsByCustomerAndBookingStatus(Customer customer, BookingStatus bookingStatus);

    List<Booking> findAllByPickupDateStartsWith(String date);
}
