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

    @Query(value="SELECT * FROM booking AS cc WHERE cc.booking_status_type='PICKED_UP' AND TO_DATE(TO_CHAR(now(),'DD-M-YYYY HH:MI:SS'),'DD-M-YYYY HH:MI:SS')> TO_DATE(SUBSTRING (cc.return_date FROM 6 FOR 23),'DD-M-YYYY HH:MI:SS')",nativeQuery = true)
    List<Booking> getLateReturns();

    @Query(value="SELECT * FROM booking AS cc WHERE cc.booking_status_type='PENDING' AND TO_DATE(TO_CHAR(now(),'DD-M-YYYY'),'DD-M-YYYY')> TO_DATE(SUBSTRING (cc.pickup_date FROM 6 FOR 14),'DD-M-YYYY')",nativeQuery = true)
    List<Booking> getLatePickups();
}
