package lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.Repository;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
