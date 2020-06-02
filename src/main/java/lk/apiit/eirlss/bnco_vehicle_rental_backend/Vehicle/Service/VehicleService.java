package lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.Service;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.UserSession;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.RoleName;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Repository.BookingVehicleRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.BookingStatus;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.BookingStatusType;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.BookingVehicle;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Util.Utils;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.DTO.DeleteStatusMessageDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.DTO.RentalPricesDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.DTO.VehicleDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.Repository.VehicleRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.entity.Vehicle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

//            vehicle.setIndex();

            vehicleRepository.save(vehicle);
            ret="Vehicle added successfully";
        }else{
            ret="You are not authorized to perform this action";
        }
        return ret;
    }

    public List<VehicleDTO> getAllVehicles(){
        List<Vehicle> vehicles=vehicleRepository.findAll();

        List<VehicleDTO> vehicleList= Utils.mapAll(vehicles,VehicleDTO.class);

        HashMap<Integer, VehicleDTO> indexedVehicleMAP = vehicleList
                .stream()
                .collect(HashMap<Integer, VehicleDTO>::new,
                        (map, streamValue) -> map.put(map.size(), streamValue),
                        (map, map2) -> {
                        });

        indexedVehicleMAP.forEach((k, v) -> v.setIndex(k));
        Collection<VehicleDTO> values = indexedVehicleMAP.values();
        List<VehicleDTO> indexedVehicleList=new ArrayList<VehicleDTO>(values);

        return indexedVehicleList;
    }

    public List<VehicleDTO> getVehiclesHomePage(){
        //gets only the available vehicles
        List<Vehicle> vehicles=vehicleRepository.findAllByAvailabilityStatus(true);

        List<VehicleDTO> vehicleList= Utils.mapAll(vehicles,VehicleDTO.class);

        HashMap<Integer, VehicleDTO> indexedVehicleMAP = vehicleList
                .stream()
                .collect(HashMap<Integer, VehicleDTO>::new,
                        (map, streamValue) -> map.put(map.size(), streamValue),
                        (map, map2) -> {
                        });

        indexedVehicleMAP.forEach((k, v) -> v.setIndex(k));
        Collection<VehicleDTO> values = indexedVehicleMAP.values();
        List<VehicleDTO> indexedVehicleList=new ArrayList<VehicleDTO>(values);

        return indexedVehicleList;
    }

    public DeleteStatusMessageDTO deleteVehicle(long id){
        DeleteStatusMessageDTO messageDTO=new DeleteStatusMessageDTO();

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
            messageDTO.setMessage("Vehicle deleted successfully.");
            messageDTO.setStatus("DELETE");
        }
        else{
            messageDTO.setMessage("Cannot Delete, there is a pending booking");
            messageDTO.setStatus("CANNOT_DELETE");
        }

        return messageDTO;
    }

    public void updateVehicle(VehicleDTO vehicleDTO){

        Vehicle vehicle=vehicleRepository.findVehicleByVehicleId(vehicleDTO.getVehicleId());

        if(vehicleDTO.getGearboxType()!=null){
            vehicle.setGearboxType(vehicleDTO.getGearboxType());
        }
        if(vehicleDTO.getVehicleName()!=null){
            vehicle.setVehicleName(vehicleDTO.getVehicleName());
        }
        if(vehicleDTO.getImgFile()!=null){
            vehicle.setImgFile(vehicleDTO.getImgFile());
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

        if(vehicleDTO.getAvailabilityStatus()!=null && vehicleDTO.getAvailabilityStatus()){
            vehicle.setAvailabilityStatus(true);
        }
        else if(vehicleDTO.getAvailabilityStatus()!=null && vehicleDTO.getAvailabilityStatus()==false){
            vehicle.setAvailabilityStatus(false);
        }


        vehicleRepository.save(vehicle);
    }

    public List<RentalPricesDTO> compareRentalRates(List<RentalPricesDTO> rentalRatesDTO){
        List<Vehicle> vehicles=vehicleRepository.findAll();

        for (RentalPricesDTO dto:rentalRatesDTO) {
            for (Vehicle vehicle: vehicles) {
                if(dto.getVehicleName().contains(vehicle.getVehicleName())){
                    double price=vehicle.getVehicleRentalPrice()*168;
                    NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
                    dto.setOurPrice(nf.format(price));
                }
            }
        }

        return rentalRatesDTO;
    }
}
