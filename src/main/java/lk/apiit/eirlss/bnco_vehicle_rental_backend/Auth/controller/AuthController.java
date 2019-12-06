package lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.controller;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.DTO.CustomerDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.JwtAuthenticationResponse;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.JwtTokenProvider;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.Service.CustomUserDetailService;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
@ResponseBody
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailService customUserDetailsService;

    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @RequestMapping(value = "/register",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerNewUser(@Valid @RequestBody CustomerDTO customerDTO){
        return ResponseEntity.ok(customUserDetailsService.saveCustomer(customerDTO));
    }

    @RequestMapping(value = "/updatepwd",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePwd(@RequestBody CustomerDTO customerDTO){
        return ResponseEntity.ok(customUserDetailsService.updatePwd(customerDTO));
    }
}
