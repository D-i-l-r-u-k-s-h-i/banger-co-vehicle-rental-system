package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.controller;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.DTO.BookingDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/bookings")
public class BookingController {
    @Autowired
    BookingService bookingService;

    @RequestMapping(value = "/save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addVehicle(@RequestHeader(value = "Authorization") String token, @RequestBody BookingDTO bookingDTO) {
        return ResponseEntity.ok(bookingService.saveBooking(bookingDTO));
    }

    @RequestMapping(value = "/cancel/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateVehicle(@RequestHeader(value = "Authorization") String token, @PathVariable(name = "id") long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.ok("");
    }
}
