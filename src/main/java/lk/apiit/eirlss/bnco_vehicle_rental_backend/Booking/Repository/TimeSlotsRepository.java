package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Repository;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.TimeSlots;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeSlotsRepository extends JpaRepository<TimeSlots,Long> {
}
