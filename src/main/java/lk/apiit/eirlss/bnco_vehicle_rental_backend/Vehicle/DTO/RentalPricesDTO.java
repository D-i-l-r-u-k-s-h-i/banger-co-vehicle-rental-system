package lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RentalPricesDTO {

    private String vehicleName;

    private String rentalPrice;

}
