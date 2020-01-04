package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Repository;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.Booking;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.TimeSlots;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TimeSlotsRepository extends JpaRepository<TimeSlots,Long> {
    @Query("SELECT DISTINCT new TimeSlots(c.booking)  FROM TimeSlots AS c")
    List<TimeSlots> getDistinctBookings();

    List<TimeSlots> getTimeSlotsByBooking(Booking booking);
}
