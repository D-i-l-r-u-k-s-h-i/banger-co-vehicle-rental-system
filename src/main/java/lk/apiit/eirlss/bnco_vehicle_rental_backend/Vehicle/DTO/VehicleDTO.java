package lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VehicleDTO {

    private long vehicleId;

//    private String vehicleImgLink;

    private byte[] imgFile;

    private String vehicleName;

    private String gearboxType;

    private String plateNumber;

    private double vehicleRentalPrice;

    private Boolean availabilityStatus;

    private String vehicleType;

    private String fuelType;

    private double vehicleRating;

    private List<String> reviews;

    private int index;
}
