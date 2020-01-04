package lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "vehicle" ,schema = "public")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long vehicleId;

    private String vehicleName;

    private String gearboxType;

    private String imgLink;

    private String plateNumber;

    private double vehicleRentalPrice;

    private String availabilityStatus;

    private String vehicleType;

    private String fuelType; //fuel type and hybrid

    //for frontend
    private int index;

    private double vehicleRating;

}
