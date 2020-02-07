package lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.Service;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.UserSession;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.RoleName;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Repository.BookingVehicleRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.BookingStatus;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.BookingStatusType;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.BookingVehicle;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Util.Utils;
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

    @Autowired
    BookingVehicleRepository bookingVehicleRepository;

    public String addVehicle(VehicleDTO vehicleDTO){
        UserSession userSession = (UserSession) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String ret="";
        if(userSession.getRole().getRoleName().equals(RoleName.ROLE_ADMIN)){
            ModelMapper modelMapper = new ModelMapper();
            Vehicle vehicle=modelMapper.map(vehicleDTO,Vehicle.class);

            vehicle.setIndex((int) vehicle.getVehicleId());

            vehicleRepository.save(vehicle);
            ret="Vehicle added successfully";
        }else{
            ret="You are not authorized to perform this action";
        }
        return ret;
    }

    public List<VehicleDTO> getAllVehicles(){
        List<Vehicle> vehicles=vehicleRepository.findAllByOrderByIndex();
        List<VehicleDTO> vehicleList= Utils.mapAll(vehicles,VehicleDTO.class);

        return(vehicleList);
    }

    public String deleteVehicle(long id){
        String ret="";

        Vehicle vehicle=vehicleRepository.findVehicleByVehicleId(id);
        List<BookingVehicle> bookingPendingVehicleList=bookingVehicleRepository.getAllByVehicle_VehicleIdAndBooking_BookingStatus(id,new BookingStatus(BookingStatusType.PENDING));
        List<BookingVehicle> bookingPickedUpVehicleList=bookingVehicleRepository.getAllByVehicle_VehicleIdAndBooking_BookingStatus(id,new BookingStatus(BookingStatusType.PICKED_UP));
        List<BookingVehicle> bookingExtendedVehicleList=bookingVehicleRepository.getAllByVehicle_VehicleIdAndBooking_BookingStatus(id,new BookingStatus(BookingStatusType.EXTENDED));

        List<BookingVehicle> bookingVehicleList=new ArrayList<>();
        bookingVehicleList.addAll(bookingExtendedVehicleList);
        bookingVehicleList.addAll(bookingPickedUpVehicleList);
        bookingVehicleList.addAll(bookingPendingVehicleList);

        if(bookingVehicleList.size()==0){
            vehicleRepository.delete(vehicle);
            ret="Vehicle deleted successfully.";
        }
        else{
            ret="Cannot Delete, there is a pending booking";
        }

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
        if(vehicleDTO.getVehicleRentalPrice()!=0){
            vehicle.setVehicleRentalPrice(vehicleDTO.getVehicleRentalPrice());
        }
        if(vehicleDTO.getFuelType()!=null){
            vehicle.setFuelType(vehicleDTO.getFuelType());
        }

        vehicleRepository.save(vehicle);
    }
}
