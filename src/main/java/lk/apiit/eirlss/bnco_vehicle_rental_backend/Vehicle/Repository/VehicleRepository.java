package lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.Repository;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository  extends JpaRepository<Vehicle, Long> {
}
