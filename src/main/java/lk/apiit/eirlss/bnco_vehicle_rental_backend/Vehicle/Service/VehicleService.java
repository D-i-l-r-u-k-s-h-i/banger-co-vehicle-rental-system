package lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.Service;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.UserSession;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.RoleName;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.DTO.VehicleDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.Repository.VehicleRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.entity.Vehicle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleService {
    @Autowired
    VehicleRepository vehicleRepository;

    public String addVehicle(VehicleDTO vehicleDTO){
        UserSession userSession = (UserSession) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String ret="";
        if(userSession.getRole().getRoleName().equals(RoleName.ROLE_ADMIN)){
            ModelMapper modelMapper = new ModelMapper();
            Vehicle vehicle=modelMapper.map(vehicleDTO,Vehicle.class);

            vehicleRepository.save(vehicle);
            ret="Vehicle added successfully";
        }else{
            ret="You are not authorized to perform this action";
        }
        return ret;
    }

    public List<VehicleDTO> getAllVehicles(){
        List<Vehicle> vehicles=vehicleRepository.findAll();
        List<VehicleDTO> vehicleList=new ArrayList<>();

        for (Vehicle vehicle:vehicles) {
            ModelMapper modelMapper = new ModelMapper();
            VehicleDTO v=modelMapper.map(vehicle,VehicleDTO.class);

            vehicleList.add(v);
        }
        return(vehicleList);
    }

    public String deleteVehicle(long id){
        String ret="";

        Vehicle vehicle=vehicleRepository.findVehicleByVehicleId(id);
        vehicleRepository.delete(vehicle);

        return ret;
    }

    public void updateVehicle(VehicleDTO vehicleDTO){

        Vehicle vehicle=vehicleRepository.findVehicleByVehicleId(vehicleDTO.getId());

        if(vehicleDTO.getGearboxType()!=null){
            vehicle.setGearboxType(vehicleDTO.getGearboxType());
        }
        if(vehicleDTO.getVehicleName()!=null){
            vehicle.setVehicleName(vehicleDTO.getVehicleName());
        }
        if(vehicleDTO.getVehicleImgLink()!=null){
            vehicle.setImgLink(vehicleDTO.getVehicleImgLink());
        }
        if(vehicleDTO.getVehicleType()!=null){
            vehicle.setVehicleType(vehicleDTO.getVehicleType());
        }
        if(vehicleDTO.getVehicleRentalPrice()!=null){
            vehicle.setVehicleRentalPrice(vehicleDTO.getVehicleRentalPrice());
        }

        vehicleRepository.save(vehicle);
    }
}
