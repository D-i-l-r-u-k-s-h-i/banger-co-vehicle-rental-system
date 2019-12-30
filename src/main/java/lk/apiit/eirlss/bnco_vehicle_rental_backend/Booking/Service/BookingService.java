package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Service;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.Repository.CustomerRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.UserSession;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.Customer;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.DTO.TimeSlotsDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Repository.BookingRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Repository.TimeSlotsRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.Booking;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.DTO.BookingDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.BookingStatus;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.BookingStatusType;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.TimeSlots;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BookingService {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    TimeSlotsRepository timeSlotsRepository;

    @Transactional
    public String saveBooking(BookingDTO bookingDTO){
        UserSession userSession = (UserSession) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer= customerRepository.findByUserId(userSession.getId());

        //get current Bookings of user
        //if(currentBookings!==null){check the return date of each of the current bookings.
        // if return date matches, get that booking and add the vehicle/additional equipment here}

        Date start=bookingDTO.getPickupDate();
        Date end=bookingDTO.getReturnDate();

        System.out.println(start +" , "+end);

        //for saving in the DB purposes. so it can be displayed without having to convert again
        SimpleDateFormat formatter = new SimpleDateFormat("E, dd-M-yyyy hh:mm:ss a");
        String startDate = formatter.format(start);
        String endDate=formatter.format(end);

        //for duration calculation and saving in another table
        LocalDateTime startDateTime=start.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime endDateTime=end.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        Duration duration=Duration.between(startDateTime,endDateTime);

        List< LocalDateTime > slots = new ArrayList<>() ;
        LocalDateTime ldt = startDateTime ;
        while (
                ldt.isBefore( endDateTime )
        ) {
            slots.add( ldt ) ;
            // Prepare for the next loop.
            ldt = ldt.plusHours( 1 ) ;
        }

        String ret="";

        if(duration.toHours()>5 && duration.toHours()<336){
            ModelMapper modelMapper = new ModelMapper();
            Booking booking=modelMapper.map(bookingDTO,Booking.class);

            booking.setPickupDate(startDate);
            booking.setReturnDate(endDate);
            booking.setCustomer(customer);
            booking.setBookingStatus(new BookingStatus(BookingStatusType.PENDING));
            booking.setRentalPeriod(duration.toHours());
            booking.setActive(true);

            bookingRepository.save(booking);

            for (LocalDateTime datetime:slots) {
                TimeSlots timeSlot=new TimeSlots();
                timeSlot.setBooking(booking);
                timeSlot.setTimeSlot(datetime);

                timeSlotsRepository.save(timeSlot);
            }

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

        //delete from booking slots
    }

    public void updateBookingStatus(){
    }

    public List<BookingDTO> getCurrentBookingsOfUser(long uid){
        List<BookingDTO> bookingDTOList=new ArrayList<>();


        return bookingDTOList;
    }

    public void extendBooking(long booking_id){

    }

    public List<BookingDTO> getPastBookingsOfUser(long uid){
        List<BookingDTO> bookingDTOList=new ArrayList<>();


        return bookingDTOList;
    }

    public void addAdditionalEquipmentForBooking(){

    }

    public List<TimeSlotsDTO> getReservedDatesforVehicle(){
        List<TimeSlotsDTO> timeSlotsDTOList=new ArrayList<>();


        return timeSlotsDTOList;
    }

    public List<TimeSlotsDTO> getReservedDatesfoAdditionalEquipment(){
        List<TimeSlotsDTO> timeSlotsDTOList=new ArrayList<>();


        return timeSlotsDTOList;
    }
}
