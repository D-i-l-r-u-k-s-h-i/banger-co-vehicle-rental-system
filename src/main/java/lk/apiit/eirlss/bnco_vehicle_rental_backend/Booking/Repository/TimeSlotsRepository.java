package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Repository;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.Booking;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.TimeSlots;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface TimeSlotsRepository extends JpaRepository<TimeSlots,Long> {
    @Query("SELECT DISTINCT new TimeSlots(c.booking)  FROM TimeSlots AS c")
    List<TimeSlots> getDistinctBookings();

    List<TimeSlots> getTimeSlotsByBooking(Booking booking);

    @Query(value="SELECT DISTINCT c.booking_id AS bookings FROM time_slots AS c WHERE CAST(c.time_slot AS character varying) LIKE ?1 ",nativeQuery = true)
    List<Long> getTimeSlotStartsWith(String timeSlot);
}
