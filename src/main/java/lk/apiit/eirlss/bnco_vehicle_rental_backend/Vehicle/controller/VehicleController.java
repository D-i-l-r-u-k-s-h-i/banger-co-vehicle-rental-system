package lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.controller;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.DTO.PropertiesDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.DTO.VehicleDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.Service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/vehicle")
@Controller
public class VehicleController {

    @Autowired
    VehicleService vehicleService;

    //only admin can perform this action
    @RequestMapping(value = "/save",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addVehicle(@RequestHeader(value = "Authorization") String token,@RequestBody VehicleDTO vehicleDTO){
        return ResponseEntity.ok(vehicleService.addVehicle(vehicleDTO));
    }

    @RequestMapping(value = "/",method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getVehicles(@RequestHeader(value = "Authorization") String token){
        PropertiesDTO propertiesDTO=new PropertiesDTO();
        propertiesDTO.setProperties(vehicleService.getAllVehicles());
        return ResponseEntity.ok(propertiesDTO);
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateVehicle(@RequestHeader(value = "Authorization") String token,@RequestBody VehicleDTO vehicleDTO){
        vehicleService.updateVehicle(vehicleDTO);
        return ResponseEntity.ok("");
    }

    @RequestMapping(value = "/delete/{id}",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateVehicle(@RequestHeader(value = "Authorization") String token, @PathVariable(name="id") long id){
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok("");
    }
}
