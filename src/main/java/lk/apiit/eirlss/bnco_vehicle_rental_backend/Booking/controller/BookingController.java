package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.controller;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.DTO.BookingDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.DTO.MakeBookingDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Controller
@RequestMapping(path = "/bookings")
public class BookingController {
    @Autowired
    BookingService bookingService;

    @RequestMapping(value = "/save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addBooking(@RequestHeader(value = "Authorization") String token, @RequestBody MakeBookingDTO bookingDTO){
        return ResponseEntity.ok(bookingService.saveBooking(bookingDTO));
    }

    @RequestMapping(value = "/cancel/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> cancelBooking(@RequestHeader(value = "Authorization") String token, @PathVariable(name = "id") long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.ok("");
    }

    @RequestMapping(value = "/timeslots/vehicle/{id}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> timeslotsForVehicle(@RequestHeader(value = "Authorization") String token,@PathVariable(name = "id") long vehicle_id) {
        return ResponseEntity.ok(bookingService.getReservedDatesforVehicle(vehicle_id));
    }

    @RequestMapping(value = "/timeslots/equipment/{id}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> timeslotsForEquipment(@RequestHeader(value = "Authorization") String token,@PathVariable(name = "id") long equipment_id) {
        return ResponseEntity.ok(bookingService.getReservedDatesforEquipment(equipment_id));
    }

    @RequestMapping(value = "/current", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> currentBookingsByUser(@RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(bookingService.getCurrentBookingsOfUser());
    }

    @RequestMapping(value = "/past", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> pastBookingsByUser(@RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(bookingService.getPastBookingsOfUser());
    }

    @RequestMapping(value = "/cancelled", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> cancelledBookingsByUser(@RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(bookingService.getCancelledBookingsOfUser());
    }

    @RequestMapping(value = "/cancelitem", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteItemFromBooking(@RequestHeader(value = "Authorization") String token, @RequestBody MakeBookingDTO dto) {
        bookingService.removeItemFromBooking(dto);
        return ResponseEntity.ok("");
    }
}
