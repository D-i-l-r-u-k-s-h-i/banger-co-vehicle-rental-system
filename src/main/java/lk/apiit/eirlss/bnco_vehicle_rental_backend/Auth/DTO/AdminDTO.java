package lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDTO {

    private String username;

    private String password;

    private String email;

    private String contactNo;

    private int role;

    private String currentAdminPass;
}
