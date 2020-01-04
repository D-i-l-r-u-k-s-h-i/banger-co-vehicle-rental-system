package lk.apiit.eirlss.bnco_vehicle_rental_backend.AdditionalEquipment.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "additional_equipment" ,schema = "public")
public class AdditionalEquipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long equipmentId;

    private String equipmentName;

    private String imgLink;

    private double aeRentalPrice;

    private String description;

    private String type;
    //for frontend
    private int index;

}
