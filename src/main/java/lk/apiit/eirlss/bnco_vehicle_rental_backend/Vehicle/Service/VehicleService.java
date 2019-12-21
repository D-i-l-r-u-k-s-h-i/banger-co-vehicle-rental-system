package lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.Service;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.DTO.VehicleDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.Repository.VehicleRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.entity.Vehicle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleService {
    @Autowired
    VehicleRepository vehicleRepository;

    public void addVehicle(VehicleDTO vehicleDTO){
        ModelMapper modelMapper = new ModelMapper();
        Vehicle vehicle=modelMapper.map(vehicleDTO,Vehicle.class);

        vehicleRepository.save(vehicle);
    }
}
