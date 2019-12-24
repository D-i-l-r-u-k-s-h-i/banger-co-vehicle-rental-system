package lk.apiit.eirlss.bnco_vehicle_rental_backend.AdditionalEquipment.Repository;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.AdditionalEquipment.entity.AdditionalEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalEquipmentRepository extends JpaRepository<AdditionalEquipment,Long> {
    AdditionalEquipment findAdditionalEquipmentByEquipmentId(long id);
}
