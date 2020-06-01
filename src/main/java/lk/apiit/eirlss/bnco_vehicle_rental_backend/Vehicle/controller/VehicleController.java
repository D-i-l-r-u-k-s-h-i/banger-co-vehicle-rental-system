package lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.controller;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.DTO.RentalPricesDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.DTO.VehicleDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.Service.VehicleService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/vehicle")
@Controller
public class VehicleController {

    @Autowired
    VehicleService vehicleService;

    //only admin can perform this action
    @RequestMapping(value = "/save",method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addVehicle(@RequestHeader(value = "Authorization") String token, @RequestParam(value = "imgFile",required = false) MultipartFile imgFile, @RequestParam(value="vehicleType") String vehicleType,
                                        @RequestParam(value ="vehicleName",required = false)String vehicleName, @RequestParam(value ="vehicleRentalPrice",required = false) Double vehicleRentalPrice, @RequestParam(value ="fuelType",required = false)String fuelType,
                                        @RequestParam(value ="gearboxType",required = false) String gearboxType) throws IOException {

        VehicleDTO vehicleDTO=new VehicleDTO();
        vehicleDTO.setVehicleName(vehicleName);
        vehicleDTO.setVehicleRentalPrice(vehicleRentalPrice);
        vehicleDTO.setFuelType(fuelType);
        vehicleDTO.setGearboxType(gearboxType);
        vehicleDTO.setVehicleType(vehicleType);
        vehicleDTO.setImgFile(imgFile.getBytes());
        vehicleDTO.setAvailabilityStatus(true);

        vehicleService.addVehicle(vehicleDTO);

        return ResponseEntity.ok("");
    }

    @RequestMapping(value = "/",method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getVehicles(){ //@RequestHeader(value = "Authorization") String token
        return ResponseEntity.ok(vehicleService.getVehiclesHomePage());
    }

    @RequestMapping(value = "/admin",method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getVehiclesforAdmin(){ //@RequestHeader(value = "Authorization") String token
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateVehicle(@RequestHeader(value = "Authorization") String token,@RequestParam(value = "imgFile",required = false) MultipartFile imgFile,@RequestParam(value="vehicleId") Long vehicleId, @RequestParam(value="vehicleType",required = false) String vehicleType,
                                           @RequestParam(value ="vehicleName",required = false)String vehicleName, @RequestParam(value ="vehicleRentalPrice",required = false) Double vehicleRentalPrice, @RequestParam(value ="fuelType",required = false)String fuelType,
                                           @RequestParam(value ="gearboxType",required = false) String gearboxType,  @RequestParam(value ="availabilityStatus",required = false) Boolean availabilityStatus) throws IOException {

        VehicleDTO vehicleDTO=new VehicleDTO();
        vehicleDTO.setVehicleId(vehicleId);
        vehicleDTO.setVehicleName(vehicleName);
        vehicleDTO.setVehicleRentalPrice(vehicleRentalPrice== null ?0.0:vehicleRentalPrice);
        vehicleDTO.setFuelType(fuelType);
        vehicleDTO.setGearboxType(gearboxType);
        vehicleDTO.setVehicleType(vehicleType);
        vehicleDTO.setImgFile(imgFile== null ?null:imgFile.getBytes());
        if(availabilityStatus!=null)
            vehicleDTO.setAvailabilityStatus(availabilityStatus);

        vehicleService.updateVehicle(vehicleDTO);

        return ResponseEntity.ok("");
    }

    @RequestMapping(value = "/delete/{id}",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteVehicle(@RequestHeader(value = "Authorization") String token, @PathVariable(name="id") long id){

        return ResponseEntity.ok(vehicleService.deleteVehicle(id));
    }

    //UI level integration
    @RequestMapping(value = "/rentalrates",method = RequestMethod.GET)
    public ResponseEntity<?> getRentalRatesFromOthers() throws IOException {

        List<RentalPricesDTO> rentlPrceList=new ArrayList<>();

        Document document= Jsoup.connect("https://www.amerirentacar.com/self-drive-rates-in-sri-lanka/").get();

        Elements tds = document.select("tr");

        for (Element el:tds) {

            String vehicle=el.getElementsByClass("column-1").text();
            String prices=el.getElementsByClass("column-3").text();

            rentlPrceList.add(new RentalPricesDTO(vehicle,prices));
        }

        rentlPrceList.remove(0);

        return ResponseEntity.ok(rentlPrceList);
    }
}
