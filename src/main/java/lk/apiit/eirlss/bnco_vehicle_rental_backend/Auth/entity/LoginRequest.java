package lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    private String username;

    private String password;
}
