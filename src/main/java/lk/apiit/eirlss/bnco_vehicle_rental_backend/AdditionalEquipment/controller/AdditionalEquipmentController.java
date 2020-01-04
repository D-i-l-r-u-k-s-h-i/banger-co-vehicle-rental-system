package lk.apiit.eirlss.bnco_vehicle_rental_backend.AdditionalEquipment.controller;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.AdditionalEquipment.DTO.AEquipsDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.AdditionalEquipment.Service.AdditionalEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/aequips")
public class AdditionalEquipmentController {
    @Autowired
    AdditionalEquipmentService additionalEquipmentService;

    //only admin can perform this action
    @RequestMapping(value = "/save",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addAdditionalEquipment(@RequestHeader(value = "Authorization") String token, @RequestBody AEquipsDTO aEquipsDTO){
        return ResponseEntity.ok(additionalEquipmentService.addAdditionalEquipment(aEquipsDTO));
    }

    @RequestMapping(value = "/",method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAdditionalEquipment(@RequestHeader(value = "Authorization") String token){
        return ResponseEntity.ok(additionalEquipmentService.getAdditionalEquipment());
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateAdditionalEquipment(@RequestHeader(value = "Authorization") String token,@RequestBody AEquipsDTO aeDTO){
        additionalEquipmentService.updateAdditionalEquipment(aeDTO);
        return ResponseEntity.ok("");
    }

    @RequestMapping(value = "/delete/{id}",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteAdditionalEquipment(@RequestHeader(value = "Authorization") String token, @PathVariable(name="id") long id){
        additionalEquipmentService.deleteAdditionalEquipment(id);
        return ResponseEntity.ok("");
    }
}
