package lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.controller;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.DTO.AdminDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.DTO.CustomerDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.Service.CustomUserDetailService;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Controller
@RequestMapping("/admin")
@ResponseBody
public class UserController {

    @Autowired
    CustomUserDetailService customUserDetailService;

    @RequestMapping(value = "/register",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addAdmin(@RequestBody AdminDTO adminDTO,@RequestHeader(value = "Authorization") String token) throws Exception {
        Utils.checkToken(token);
        return ResponseEntity.ok(customUserDetailService.saveAdmin(adminDTO));
    }

    @RequestMapping(value = "/search/{name}", method = RequestMethod.GET)
    public ResponseEntity<?> searchUser(@RequestHeader(value = "Authorization") String token, @PathVariable(name = "name") String name){
        return ResponseEntity.ok(customUserDetailService.searchUser(name));
    }

    @RequestMapping(value = "/updatecustomer",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@RequestHeader(value = "Authorization") String token,@RequestBody CustomerDTO customerDTO) throws Exception {
        Utils.checkToken(token);
        customUserDetailService.updateCustomerDetails(customerDTO);
        return ResponseEntity.ok("Successfully Updated User Details");
    }
}
