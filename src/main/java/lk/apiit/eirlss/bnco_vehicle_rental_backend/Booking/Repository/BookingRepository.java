package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Repository;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.Customer;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.Booking;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {
    Booking findBookingByBookingId(long id);

    List<Booking> findBookingsByCustomerAndBookingStatus(Customer customer, BookingStatus bookingStatus);

    List<Booking> findAllByPickupDateStartsWith(String date);

    List<Booking> findAllByPickupDateStartsWithOrReturnDateStartsWith(String date1,String date2);

    @Query(value="SELECT cc.offense_status AS status FROM insurance_offenses AS cc WHERE cc.lisence_no=?1",nativeQuery = true)
    String getOffenses(String lisence);
}
