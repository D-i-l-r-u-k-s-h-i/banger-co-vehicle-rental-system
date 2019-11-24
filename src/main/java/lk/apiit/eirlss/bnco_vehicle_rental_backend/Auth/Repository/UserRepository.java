package lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.Repository;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.AllUsers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AllUsers, Long> {
    AllUsers findByUsername(String Username);
    AllUsers findById(long id);
}
