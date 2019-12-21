package lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.Repository;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin,Long> {
    Admin getByUsername(String username);
}
