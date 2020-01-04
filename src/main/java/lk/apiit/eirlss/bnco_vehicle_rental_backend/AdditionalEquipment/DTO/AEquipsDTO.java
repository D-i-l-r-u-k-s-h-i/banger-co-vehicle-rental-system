package lk.apiit.eirlss.bnco_vehicle_rental_backend.AdditionalEquipment.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AEquipsDTO {
    private long equipmentId;

    private String equipmentName;

    private String imgLink;

    private double aeRentalPrice;

    private String description;

    private String type;
    //for frontend
    private int index;
}
