package lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.Service;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.DTO.AdminDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.DTO.CustomerDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.Repository.AdminRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.Repository.CustomerRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.Repository.UserRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.UserSession;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    AdminRepository  adminRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        AllUsers user = userRepository.findByUsername(username);
        return UserSession.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        Optional<AllUsers> user = userRepository.findById(id.longValue());
        return UserSession.create(user.get());
    }

    @Transactional
    public String saveCustomer(CustomerDTO customerDTO){
        String ret="";

        AllUsers user=userRepository.findByUsername(customerDTO.getCustomerUserName());

        if(user!=null){
            ret = "Sorry this name is taken";
        }
        else{
            Customer cust_by_email=customerRepository.findByCustomerEmail(customerDTO.getCustomerEmail());
            if(cust_by_email!=null){
                ret="Sorry, user with this Email already exists";
            }
            //check confirm password
            if(customerDTO.getCustomerPassword().equals(customerDTO.getConfirmPassword())){
                String custPwd=customerDTO.getCustomerPassword();
                String pwd = new BCryptPasswordEncoder().encode(custPwd);

                AllUsers newuser=new AllUsers();
                newuser.setUsername(customerDTO.getCustomerUserName());
                newuser.setPassword(pwd);

                newuser.setRole(new Role(2, RoleName.ROLE_CUSTOMER));

                Customer newCust=new Customer();
                newCust.setCustomerFName(customerDTO.getCustomerFirstName());
                newCust.setCustomerUserName(customerDTO.getCustomerUserName());
                newCust.setCustomerLName(customerDTO.getCustomerLastName());
                newCust.setCustomerContactNo(customerDTO.getCustomerContactNo());
                newCust.setEmergencyContactNo(customerDTO.getCustomerEmergencyContactNo());
                newCust.setCustomerEmail(customerDTO.getCustomerEmail());
//                newCust.setRole(new Role(2, RoleName.ROLE_CUSTOMER));
//                newCust.setUserId(newuser.getId());
                newCust.setUser(newuser);
                userRepository.save(newuser);
                customerRepository.save(newCust);

                ret="Successful registration";
            }
            else {
                ret="Passwords doesn't match";
            }

        }

        return ret;
    }

    public String updatePwd(CustomerDTO customerDTO){
        String response="";
        AllUsers u = userRepository.findByUsername(customerDTO.getCustomerUserName());
        if (u != null) {
//            if (bCryptPasswordEncoder.matches(pwdUpdate.getOld_password(), u.getPassword())) {
                if (customerDTO.getCustomerPassword().equals(customerDTO.getConfirmPassword())) {
                    String pwd = new BCryptPasswordEncoder().encode(customerDTO.getCustomerPassword());
                    u.setPassword(pwd);
                    userRepository.save(u);
                    response.concat("successful password update");
                } else {
                    response.concat("Password missmatch");
                }

        } else {
            response.concat("invalid User");
        }
        return response;
    }

    @Transactional
    public String saveAdmin(AdminDTO adminDTO){
        String ret="";

        AllUsers user=userRepository.findByUsername(adminDTO.getUsername());

        if(user!=null){
            ret = "Sorry this name is taken";
        }
        else{
            UserSession userSession = (UserSession) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if(bCryptPasswordEncoder.matches(adminDTO.getCurrentAdminPass(),userSession.getPassword()) && userSession.getRole().getRoleId()==1){
                String pwd = bCryptPasswordEncoder.encode(adminDTO.getPassword());

                ModelMapper modelMapper= new ModelMapper();
                Admin admin=modelMapper.map(adminDTO,Admin.class);
                AllUsers newuser=modelMapper.map(adminDTO,AllUsers.class);
                newuser.setPassword(pwd);
                newuser.setRole(new Role(1,RoleName.ROLE_ADMIN));
//                admin.setRole(new Role(1,RoleName.ROLE_ADMIN));

                adminRepository.save(admin);
                userRepository.save(newuser);

                ret="Successful Registration";
            }
            else {
                ret="Sorry, incorrect password";
            }

        }
        return ret;
    }
}
