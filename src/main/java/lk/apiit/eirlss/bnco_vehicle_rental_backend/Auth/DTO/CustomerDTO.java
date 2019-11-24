package lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDTO {
    private long customerId;

    private String customerName;

    private String customerContactNo;

    private String customerAddress;

    private String customerStatus;

    private String customerUserName;

    private String customerPassword;

    private String customerEmail;

    private boolean isBlacklisted;

    private long roleId;
}
