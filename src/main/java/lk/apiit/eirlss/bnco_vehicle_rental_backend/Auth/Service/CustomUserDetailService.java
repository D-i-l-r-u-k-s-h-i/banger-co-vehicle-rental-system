package lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.Service;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.DTO.CustomerDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.Repository.CustomerRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.Repository.UserRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.UserSession;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.AllUsers;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.Customer;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.Role;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.RoleName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        AllUsers user = userRepository.findByUsername(username);
        return UserSession.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        Optional<AllUsers> user = userRepository.findById(id);
        return UserSession.create(user.get());
    }

    public String saveCustomer(CustomerDTO customerDTO){
        String ret="";

        AllUsers user=userRepository.findByUsername(customerDTO.getCustomerUserName());

        if(user!=null){
            ret = "User Already Exists";
        }
        else{
            String newPwd=customerDTO.getCustomerPassword();
            String pwd = new BCryptPasswordEncoder().encode(newPwd);

            AllUsers newuser=new AllUsers();
            newuser.setUsername(customerDTO.getCustomerUserName());
            newuser.setPassword(pwd);
            newuser.setRole(new Role(2, RoleName.ROLE_CUSTOMER));

            userRepository.save(newuser);

            Customer newCust=new Customer();
            newCust.setCustomerName(customerDTO.getCustomerName());
            newCust.setCustomerUserName(customerDTO.getCustomerUserName());
            newCust.setCustomerAddress(customerDTO.getCustomerAddress());
            newCust.setCustomerContactNo(customerDTO.getCustomerContactNo());
            newCust.setCustomerEmail(customerDTO.getCustomerEmail());
            newCust.setRole(new Role(2, RoleName.ROLE_CUSTOMER));
            newCust.setUserId(newuser.getId());

            customerRepository.save(newCust);
        }

        return ret;
    }
}
