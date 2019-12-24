package lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.controller;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.DTO.AdminDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.Service.CustomUserDetailService;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

}
