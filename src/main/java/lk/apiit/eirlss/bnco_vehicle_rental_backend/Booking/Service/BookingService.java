package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Service;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.AdditionalEquipment.entity.AdditionalEquipment;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.Repository.CustomerRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.UserSession;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.Customer;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.DTO.MakeBookingDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.DTO.TimeSlotsDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Repository.BookingAdditionalEquipsRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Repository.BookingRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Repository.BookingVehicleRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Repository.TimeSlotsRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.*;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.DTO.BookingDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Util.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
public class BookingService {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    TimeSlotsRepository timeSlotsRepository;

    @Autowired
    BookingVehicleRepository bookingVehicleRepository;

    @Autowired
    BookingAdditionalEquipsRepository bookingAdditionalEquipsRepository;



    @Transactional
    public String saveBooking(MakeBookingDTO bookingDTO){
        UserSession userSession = (UserSession) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer= customerRepository.findByUserId(userSession.getId());

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
        while (ldt.isBefore( endDateTime )) {
            slots.add( ldt ) ;
            // Prepare for the next loop.
            ldt = ldt.plusMinutes(30);
        }

        String ret="";

        if(duration.toHours()>5 && duration.toHours()<336){
            //get current Bookings of user
            List<BookingDTO> currentBookings=getCurrentBookingsOfUser();
            boolean isMatch=false;
            BookingDTO addToThisBooking=null;

            //if(currentBookings!==null){check the return date of each of the current bookings.}
            if(currentBookings!=null){
                // if return date matches, get that booking and add the vehicle/additional equipment here}
                for (BookingDTO booking:currentBookings) {
                    if(booking.getReturnDate().equals(endDate)){
                        isMatch=true;
                        addToThisBooking=booking;
                        //break out of loop
                    }
                }

                //add to an existing booking
                if(addToThisBooking!=null){
                    if(bookingDTO.getEquipmentId()!=0){
                        ModelMapper mapper=new ModelMapper();
                        BookingAdditionalEquipment bookingAE=mapper.map(bookingDTO,BookingAdditionalEquipment.class);

                        Booking booking=bookingRepository.findBookingByBookingId(addToThisBooking.getBookingId());
                        bookingAE.setBooking(booking);
//                    bookingAE.setAdditionalEquipment();
                        //save additioonal equipment in booking
                        bookingAdditionalEquipsRepository.save(bookingAE);

                    }
                    if(bookingDTO.getVehicleId()!=0){
                        ModelMapper mapper=new ModelMapper();
                        BookingVehicle bookingVehicle=mapper.map(bookingDTO,BookingVehicle.class);

                        Booking booking=bookingRepository.findBookingByBookingId(addToThisBooking.getBookingId());
                        bookingVehicle.setBooking(booking);

                        //save vehicle under this booking
                        bookingVehicleRepository.save(bookingVehicle);
                    }
                }
            }

            if(currentBookings==null || isMatch==false ){
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

                //a new booking for the additional equipment
                if(bookingDTO.getEquipmentId()!=0){
                    ModelMapper mapper=new ModelMapper();
                    BookingAdditionalEquipment bookingAE=mapper.map(bookingDTO,BookingAdditionalEquipment.class);

                    //save additioonal equipment in booking
                    bookingAdditionalEquipsRepository.save(bookingAE);
                }

                if(bookingDTO.getVehicleId()!=0){
                    ModelMapper mapper=new ModelMapper();
                    BookingVehicle bookingVehicle=mapper.map(bookingDTO,BookingVehicle.class);
                    bookingVehicle.setBooking(booking);

                    //save vehicle under this booking
                    bookingVehicleRepository.save(bookingVehicle);
                }
            }
            ret="Successful booking";
        }
        else {
            ret="The selected time range should be between 5hrs and 2 weeks only.";
        }
        return ret;
    }

    public List<TimeSlotsDTO> getReservedDatesforVehicle(long vehicle_id){

        List<TimeSlots> allTimeSlots=timeSlotsRepository.findAll();

        for (TimeSlots timeSlot: allTimeSlots) {
            if(timeSlot.getTimeSlot().isBefore(LocalDateTime.now())){
                timeSlotsRepository.delete(timeSlot);
            }
        }

        List<TimeSlots> distinctBookings=timeSlotsRepository.getDistinctBookings();

        List<TimeSlots> bookingsWithVehicle=new ArrayList<>();

        for (TimeSlots timeSlot:distinctBookings) {
            Booking booking=timeSlot.getBooking();
            //vehicle list in booking
            List<BookingVehicle> bookedvehicleList=bookingVehicleRepository.getAllByBooking(booking);

            bookedvehicleList.forEach(bookedvehicle->{
                if(bookedvehicle.getVehicle().getVehicleId()==vehicle_id){
                    bookingsWithVehicle.add(timeSlot);
                }
            });
        }

        List<TimeSlots> allBookingSlotsForVehicle=new ArrayList<>();

        for (TimeSlots timeSlots:bookingsWithVehicle) {
            List<TimeSlots> timeSlotsList=timeSlotsRepository.getTimeSlotsByBooking(timeSlots.getBooking());
            allBookingSlotsForVehicle.addAll(timeSlotsList);
        }

        List<TimeSlotsDTO> timeSlotsDTOList= Utils.mapAll(allBookingSlotsForVehicle,TimeSlotsDTO.class);

        return timeSlotsDTOList;
    }

    public List<TimeSlotsDTO> getReservedDatesforEquipment(long equipment_id){

        List<TimeSlots> allTimeSlots=timeSlotsRepository.findAll();

        for (TimeSlots timeSlot: allTimeSlots) {
            if(timeSlot.getTimeSlot().isBefore(LocalDateTime.now())){
                timeSlotsRepository.delete(timeSlot);
            }
        }

        List<TimeSlots> distinctBookings=timeSlotsRepository.getDistinctBookings();

        List<TimeSlots> bookingsWithEquipment=new ArrayList<>();

        for (TimeSlots timeSlot:distinctBookings) {
            Booking booking=timeSlot.getBooking();
            //vehicle list in booking
            List<BookingAdditionalEquipment> bookedEquipmentList=bookingAdditionalEquipsRepository.getAllByBooking(booking);

            bookedEquipmentList.forEach(bookedEquipment->{
                if(bookedEquipment.getEquipment().getEquipmentId()==equipment_id){
                    bookingsWithEquipment.add(timeSlot);
                }
            });
        }

        List<TimeSlots> allBookingSlotsForEquipment=new ArrayList<>();

        for (TimeSlots timeSlots:bookingsWithEquipment) {
            List<TimeSlots> timeSlotsList=timeSlotsRepository.getTimeSlotsByBooking(timeSlots.getBooking());
            allBookingSlotsForEquipment.addAll(timeSlotsList);
        }

        List<TimeSlotsDTO> timeSlotsDTOList= Utils.mapAll(allBookingSlotsForEquipment,TimeSlotsDTO.class);

        return timeSlotsDTOList;
    }

    //not deleting the whole booking from the bookings list, but delete the time slots
    public void cancelBooking(long bookingId){
        Booking booking=bookingRepository.findBookingByBookingId(bookingId);
        booking.setBookingStatus(new BookingStatus(BookingStatusType.CANCELLED));
        booking.setPickupDate(null);
        booking.setReturnDate(null);
        booking.setRentalPeriod(0);

        //delete from booking slots
        List<TimeSlots> timeSlots=timeSlotsRepository.getTimeSlotsByBooking(booking);
        timeSlotsRepository.deleteInBatch(timeSlots);//test and see whether to use deleteAll()
    }

    //remove item from equipmentBooking and vehicleBooking tables
    public void removeItemFromBooking(MakeBookingDTO dto){

        //dto has the booking_id, equipment_id/vehicle_id to be removed
        if(dto.getVehicleId()!=0){
            BookingVehicle bookingVehicle=bookingVehicleRepository.getByBooking_BookingIdAndVehicle_VehicleId(dto.getBookingId(),dto.getVehicleId());
            bookingVehicleRepository.delete(bookingVehicle);

        }

        if(dto.getEquipmentId()!=0){
            BookingAdditionalEquipment bookingAE=bookingAdditionalEquipsRepository.getByBooking_BookingIdAndAndEquipment_EquipmentId(dto.getBookingId(),dto.getEquipmentId());
            bookingAdditionalEquipsRepository.delete(bookingAE);
        }

        //if its the last item for booking then cancel the whole booking

    }

    public void updateBookingStatus(String status){
        //if picked up or returned
    }

    public List<BookingDTO> getCurrentBookingsOfUser(){

        UserSession userSession = (UserSession) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer= customerRepository.findByUserId(userSession.getId());

        List<Booking> pendingBookings=bookingRepository.findBookingsByCustomerAndBookingStatus(customer,new BookingStatus(BookingStatusType.PENDING));

        List<Booking> pickedUpBookings=bookingRepository.findBookingsByCustomerAndBookingStatus(customer,new BookingStatus(BookingStatusType.PICKED_UP));

        List<Booking> currentBookings=new ArrayList<>();
        currentBookings.addAll(pendingBookings);
        currentBookings.addAll(pickedUpBookings);

        //get vehicleBookings and equipBookings
        List<BookingVehicle> pendingbookingVehicleList=bookingVehicleRepository.getAllByBooking_CustomerAndBooking_BookingStatus(customer,new BookingStatus(BookingStatusType.PENDING));
        List<BookingVehicle> pickedupbookingVehicleList=bookingVehicleRepository.getAllByBooking_CustomerAndBooking_BookingStatus(customer,new BookingStatus(BookingStatusType.PICKED_UP));
        List<BookingAdditionalEquipment> pendingpbookingEquipmentList=bookingAdditionalEquipsRepository.getAllByBooking_CustomerAndBooking_BookingStatus(customer,new BookingStatus(BookingStatusType.PENDING));
        List<BookingAdditionalEquipment> pickeduppbookingEquipmentList=bookingAdditionalEquipsRepository.getAllByBooking_CustomerAndBooking_BookingStatus(customer,new BookingStatus(BookingStatusType.PICKED_UP));

        List<BookingVehicle> currentVehicleBookings=new ArrayList<>();
        currentVehicleBookings.addAll(pendingbookingVehicleList);
        currentVehicleBookings.addAll(pickedupbookingVehicleList);

        List<BookingAdditionalEquipment> currentEquipmentBookings=new ArrayList<>();
        currentEquipmentBookings.addAll(pendingpbookingEquipmentList);
        currentEquipmentBookings.addAll(pickeduppbookingEquipmentList);

        List<BookingDTO> bookingDTOList=Utils.mapAll(currentBookings,BookingDTO.class);

        bookingDTOList.forEach(bookingdto->{
            double total=calculateTotal(bookingdto.getBookingId());
            bookingdto.setTotal(total);
        });

//        boolean var = currentVehicleBookings.stream().anyMatch(element -> currentBookings.contains(element.getBooking()));
        //setting Vehicles in the booking for dto
        for (BookingVehicle cvb:currentVehicleBookings) {
            if(currentBookings.contains(cvb.getBooking())){
                List<BookingVehicle> vehicleList=bookingVehicleRepository.getAllByBooking(cvb.getBooking());

                bookingDTOList.forEach(dto->{if(dto.getBookingId()==cvb.getBooking().getBookingId()){
                    dto.setVehicleList(vehicleList);
                }
                });
            }
        }
        //setting AdditionalEquipment in the booking for dto
        for(BookingAdditionalEquipment aeb:currentEquipmentBookings){
            if(currentBookings.contains(aeb.getBooking())){
                List<BookingAdditionalEquipment> equipmentList=bookingAdditionalEquipsRepository.getAllByBooking(aeb.getBooking());

                bookingDTOList.forEach(dto->{if(dto.getBookingId()==aeb.getBooking().getBookingId()){
                    dto.setAdditionalEquipmentList(equipmentList);
                }
                });
            }
        }

        return bookingDTOList;
    }

    public void extendBooking(long booking_id){

    }

    public double calculateTotal(long booking_id){
        double total=0;

        Booking booking=bookingRepository.findBookingByBookingId(booking_id);

        List<BookingVehicle> vehicleList=bookingVehicleRepository.getAllByBooking(booking);
        List<BookingAdditionalEquipment> equipmentList=bookingAdditionalEquipsRepository.getAllByBooking(booking);

        if(vehicleList!=null){
            for (BookingVehicle bv:vehicleList) {
                total+=bv.getVehicle().getVehicleRentalPrice()*booking.getRentalPeriod();
            }
        }

        if(equipmentList!=null){
            for (BookingAdditionalEquipment aeb:equipmentList) {
                total+=(aeb.getEquipment().getAeRentalPrice()*booking.getRentalPeriod());
            }
        }

        return total;
    }

    public List<BookingDTO> getPastBookingsOfUser(){
        UserSession userSession = (UserSession) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer= customerRepository.findByUserId(userSession.getId());

        List<Booking> pastBookings=bookingRepository.findBookingsByCustomerAndBookingStatus(customer,new BookingStatus(BookingStatusType.RETURNED));

        List<BookingVehicle> pastbookingVehicleList=bookingVehicleRepository.getAllByBooking_CustomerAndBooking_BookingStatus(customer,new BookingStatus(BookingStatusType.RETURNED));
        List<BookingAdditionalEquipment> pastpbookingEquipmentList=bookingAdditionalEquipsRepository.getAllByBooking_CustomerAndBooking_BookingStatus(customer,new BookingStatus(BookingStatusType.RETURNED));

        List<BookingDTO> bookingDTOList=Utils.mapAll(pastBookings,BookingDTO.class);

        bookingDTOList.forEach(bookingdto->{
            double total=calculateTotal(bookingdto.getBookingId());
            bookingdto.setTotal(total);
        });

        //setting Vehicles in the booking for dto
        for (BookingVehicle pvb:pastbookingVehicleList) {
            if(pastBookings.contains(pvb.getBooking())){
                List<BookingVehicle> vehicleList=bookingVehicleRepository.getAllByBooking(pvb.getBooking());

                bookingDTOList.forEach(dto->{if(dto.getBookingId()==pvb.getBooking().getBookingId()){
                    dto.setVehicleList(vehicleList);
                }
                });
            }
        }
        //setting AdditionalEquipment in the booking for dto
        for(BookingAdditionalEquipment aeb:pastpbookingEquipmentList){
            if(pastBookings.contains(aeb.getBooking())){
                List<BookingAdditionalEquipment> equipmentList=bookingAdditionalEquipsRepository.getAllByBooking(aeb.getBooking());

                bookingDTOList.forEach(dto->{if(dto.getBookingId()==aeb.getBooking().getBookingId()){
                    dto.setAdditionalEquipmentList(equipmentList);
                }
                });
            }
        }

        return bookingDTOList;
    }
    //this needs to change as above
    public List<BookingDTO> getCancelledBookingsOfUser(){
        UserSession userSession = (UserSession) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer= customerRepository.findByUserId(userSession.getId());

        List<Booking> cancelledBookings=bookingRepository.findBookingsByCustomerAndBookingStatus(customer,new BookingStatus(BookingStatusType.CANCELLED));

        List<BookingDTO> bookingDTOList=Utils.mapAll(cancelledBookings,BookingDTO.class);
        return bookingDTOList;
    }

}
