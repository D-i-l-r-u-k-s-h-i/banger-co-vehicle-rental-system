package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.controller;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.DTO.MakeBookingDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Service.BookingService;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Util.Utils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(path = "/bookings")
public class BookingController {
    @Autowired
    BookingService bookingService;

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value = "/save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addBooking(@RequestHeader(value = "Authorization") String token, @RequestBody MakeBookingDTO bookingDTO) throws Exception {
        Utils.checkToken(token);
        return ResponseEntity.ok(bookingService.saveBooking(bookingDTO));
    }

    @RequestMapping(value = "/cancel/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> cancelBooking(@RequestHeader(value = "Authorization") String token, @PathVariable(name = "id") long id) throws Exception {
        Utils.checkToken(token);
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @RequestMapping(value = "/timeslots/vehicle/{id}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> timeslotsForVehicle(@RequestHeader(value = "Authorization") String token,@PathVariable(name = "id") long vehicle_id) throws Exception {
        Utils.checkToken(token);
        return ResponseEntity.ok(bookingService.getReservedDatesforVehicle(vehicle_id));
    }

    @RequestMapping(value = "/timeslots/equipment/{id}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> timeslotsForEquipment(@RequestHeader(value = "Authorization") String token,@PathVariable(name = "id") long equipment_id) throws Exception {
        Utils.checkToken(token);
        return ResponseEntity.ok(bookingService.getReservedDatesforEquipment(equipment_id));
    }

    @RequestMapping(value = "/current", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> currentBookingsByUser(@RequestHeader(value = "Authorization") String token) throws Exception {
        Utils.checkToken(token);
        return ResponseEntity.ok(bookingService.getCurrentBookingsOfUser());
    }

    @RequestMapping(value = "/past", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> pastBookingsByUser(@RequestHeader(value = "Authorization") String token) throws Exception {
        Utils.checkToken(token);
        return ResponseEntity.ok(bookingService.getPastBookingsOfUser());
    }
    //for the admin when viewing user
    @RequestMapping(value = "/cancelled", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> cancelledBookingsByUser(@RequestHeader(value = "Authorization") String token) throws Exception {
        Utils.checkToken(token);
        return ResponseEntity.ok(bookingService.getCancelledBookingsOfUser());
    }

    @RequestMapping(value = "/cancelitem", method = RequestMethod.POST)
    public ResponseEntity<?> deleteItemFromBooking(@RequestHeader(value = "Authorization") String token, @RequestBody MakeBookingDTO dto) throws Exception {
        Utils.checkToken(token);
        bookingService.removeItemFromBooking(dto);
        return ResponseEntity.ok("");
    }

    @RequestMapping(value = "/extend/{booking_id}", method = RequestMethod.POST)
    public ResponseEntity<?> extendBooking(@RequestHeader(value = "Authorization") String token, @PathVariable(name = "booking_id") long booking_id) throws Exception {
        Utils.checkToken(token);
        return ResponseEntity.ok(bookingService.extendBooking(booking_id));
    }

    @RequestMapping(value = "/today", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> bookingsForToday(@RequestHeader(value = "Authorization") String token) throws Exception {
        Utils.checkToken(token);
        return ResponseEntity.ok(bookingService.viewAllBookingsForToday());
    }

    @RequestMapping(value = "/pickup/{booking_id}", method = RequestMethod.POST)
    public ResponseEntity<?> pickupBooking(@RequestHeader(value = "Authorization") String token, @PathVariable(name = "booking_id") long booking_id) throws ParseException {
        bookingService.onPickup(booking_id);
        return ResponseEntity.ok("Pickup confirmed");
    }

    @RequestMapping(value = "/return/{booking_id}", method = RequestMethod.POST)
    public ResponseEntity<?> returnBooking(@RequestHeader(value = "Authorization") String token, @PathVariable(name = "booking_id") long booking_id) throws ParseException {
        bookingService.onReturn(booking_id);
        return ResponseEntity.ok("Return confirmed");
    }

    @RequestMapping(value = "/blacklist/{booking_id}", method = RequestMethod.POST)
    public ResponseEntity<?> blacklistUser(@RequestHeader(value = "Authorization") String token, @PathVariable(name = "booking_id") long booking_id) throws ParseException {
        bookingService.blacklistCustomer(booking_id);
        return ResponseEntity.ok("User Blacklisted Successfully");
    }

    @RequestMapping(value = "/pastvehicles", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> vehiclesBookedByUserPast(@RequestHeader(value = "Authorization") String token) throws ParseException {
        return ResponseEntity.ok(bookingService.pastVehiclesBookedByCustomer());
    }

    @RequestMapping(value = "/checklicence/{lisenceNo}", method = RequestMethod.POST)
    public ResponseEntity<?> checkLicenceValidity(@RequestHeader(value = "Authorization") String token,@PathVariable(name = "lisenceNo") String lisenceNo) throws Exception {
        Utils.checkToken(token);

        byte [] bytefile= restTemplate.getForObject("http://localhost:8090/csv/file", byte[].class);

        return ResponseEntity.ok(bookingService.checkEligibilityToBook(lisenceNo,bytefile));
    }
}
