package lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.Repository;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByCustomerEmail(String email);
    Customer findByUserId(long id);

    @Query("SELECT u FROM Customer u WHERE lower(u.customerFName) like lower(concat('%', ?1,'%')) or lower(u.customerLName) like lower(concat('%', ?1,'%')) or lower(u.customerUserName) like lower(concat('%', ?1,'%'))")
    List<Customer> searchCustomer(String name);
}
