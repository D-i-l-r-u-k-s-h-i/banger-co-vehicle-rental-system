package lk.apiit.eirlss.bnco_vehicle_rental_backend.AdditionalEquipment.Service;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.AdditionalEquipment.DTO.AEquipsDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.AdditionalEquipment.Repository.AdditionalEquipmentRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.AdditionalEquipment.entity.AdditionalEquipment;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.UserSession;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.RoleName;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdditionalEquipmentService {
    @Autowired
    AdditionalEquipmentRepository additionalEquipmentRepository;

    public String addAdditionalEquipment(AEquipsDTO AEquipsDTO){
        UserSession userSession = (UserSession) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String ret="";
        if(userSession.getRole().getRoleName().equals(RoleName.ROLE_ADMIN)){
            ModelMapper modelMapper = new ModelMapper();
            AdditionalEquipment equipment=modelMapper.map(AEquipsDTO,AdditionalEquipment.class);

            additionalEquipmentRepository.save(equipment);
            ret="Vehicle added successfully";
        }else{
            ret="You are not authorized to perform this action";
        }
        return ret;
    }

    public List<AEquipsDTO> getAdditionalEquipment(){
        List<AdditionalEquipment> equipments=additionalEquipmentRepository.findAll();
        List<AEquipsDTO> equipmentList=new ArrayList<>();

        for (AdditionalEquipment equipment:equipments) {
            ModelMapper modelMapper = new ModelMapper();
            AEquipsDTO v=modelMapper.map(equipment,AEquipsDTO.class);

            equipmentList.add(v);
        }
        return(equipmentList);
    }

    public String deleteAdditionalEquipment(long id){
        String ret="";

        AdditionalEquipment vehicle = additionalEquipmentRepository.findAdditionalEquipmentByEquipmentId(id);
        additionalEquipmentRepository.delete(vehicle);

        return ret;
    }

    public void updateAdditionalEquipment(AEquipsDTO equipsDTO){

        AdditionalEquipment vehicle=additionalEquipmentRepository.findAdditionalEquipmentByEquipmentId(equipsDTO.getEquipmentId());
        ModelMapper modelMapper = new ModelMapper();
        AdditionalEquipment v=modelMapper.map(equipsDTO,AdditionalEquipment.class);

        additionalEquipmentRepository.save(vehicle);
    }
}
