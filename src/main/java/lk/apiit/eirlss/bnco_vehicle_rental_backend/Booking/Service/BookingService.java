package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Service;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.Repository.CustomerRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.UserSession;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.Customer;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Repository.BookingRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.Booking;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.DTO.BookingDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.BookingStatus;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.BookingStatusType;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class BookingService {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    CustomerRepository customerRepository;

    public String saveBooking(BookingDTO bookingDTO){
        UserSession userSession = (UserSession) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer= customerRepository.findByUserId(userSession.getId());

        LocalDateTime start=bookingDTO.getPickupDate();
        LocalDateTime end=bookingDTO.getReturnDate();

        Duration duration=Duration.between(start,end);

        String ret="";

        if(duration.toHours()>5 && duration.toHours()<336){
            ModelMapper modelMapper = new ModelMapper();
            Booking booking=modelMapper.map(bookingDTO,Booking.class);
            booking.setCustomer(customer);
            booking.setBookingStatus(new BookingStatus(BookingStatusType.PENDING));
            booking.setRentalPeriod(duration.toHours());
            booking.setActive(true);

            bookingRepository.save(booking);
            ret="Successful booking";
        }
        else {
            ret="The selected time range should be between 5hrs and 2 weeks only.";
        }
        return ret;
    }

    public void cancelBooking(long bookingId){
        Booking booking=bookingRepository.findBookingByBookingId(bookingId);
        booking.setBookingStatus(new BookingStatus(BookingStatusType.CANCELLED));
        booking.setPickupDate(null);
        booking.setReturnDate(null);
        booking.setRentalPeriod(0);
    }

    public void updateBookingStatus(){
    }
}
