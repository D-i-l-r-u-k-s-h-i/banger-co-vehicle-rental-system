package lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VehicleDTO {

    private long id;

    private String vehicleImgLink;

    private String vehicleName;

    private String gearboxType;

    private String plateNumber;

    private String vehicleRentalPrice;

    private String availabilityStatus;

    private String vehicleType;

    private String vehicleRating;

    private List<String> reviews;

    private int index;
}
