package lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.controller;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.DTO.VehicleDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.Service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class VehicleController {

    @Autowired
    VehicleService vehicleService;

    //should give admin rights
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/add_vehicle",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePwd(@RequestHeader(value = "Authorization") String token,@RequestBody VehicleDTO vehicleDTO){
        vehicleService.addVehicle(vehicleDTO);
        return ResponseEntity.ok("");
    }
}
