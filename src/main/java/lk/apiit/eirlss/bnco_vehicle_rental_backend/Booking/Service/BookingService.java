package lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.AdditionalEquipment.Repository.AdditionalEquipmentRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.AdditionalEquipment.entity.AdditionalEquipment;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.Repository.CustomerRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.UserSession;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.Customer;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.DTO.*;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Repository.BookingAdditionalEquipsRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Repository.BookingRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Repository.BookingVehicleRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.Repository.TimeSlotsRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Booking.entity.*;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Util.Utils;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.DTO.VehicleDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.Repository.VehicleRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.entity.Vehicle;
import org.apache.commons.io.FileUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    AdditionalEquipmentRepository additionalEquipmentRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Transactional
    public String saveBooking(MakeBookingDTO bookingDTO){
        UserSession userSession = (UserSession) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer= customerRepository.findByUserId(userSession.getId());

        String ret="";

        if(customer.isBlacklisted()){
            ret="Sorry you have been blacklisted due to a previous booking that was never picked up!";
        }
        else{
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
                        bookingAE.setBooking(booking);
                        //save additional equipment in booking
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

                //call method
                if(getPastBookingsOfUser()==null){
                    customer.setCustomerStatus("NEW");
                    customerRepository.save(customer);
                }
                else{
                    customer.setCustomerStatus("REPEAT");
                    customerRepository.save(customer);
                }
                ret="Successful booking";
            }
            else {
                ret="The selected time range should be between 5hrs and 2 weeks only.";
            }
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
    public String cancelBooking(long bookingId){

        Booking booking=bookingRepository.findBookingByBookingId(bookingId);
        booking.setBookingStatus(new BookingStatus(BookingStatusType.CANCELLED));
        booking.setPickupDate(null);
        booking.setReturnDate(null);
        booking.setRentalPeriod(0);

        //delete from booking slots
        List<TimeSlots> timeSlots=timeSlotsRepository.getTimeSlotsByBooking(booking);
        timeSlotsRepository.deleteInBatch(timeSlots);//test and see whether to use deleteAll()

        return "Successful";
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

    public void onPickup(long bookingId){
       Booking booking=bookingRepository.findBookingByBookingId(bookingId);
       booking.setBookingStatus(new BookingStatus(BookingStatusType.PICKED_UP));
       bookingRepository.save(booking);
    }

    public void onReturn(long bookingId){
        Booking booking=bookingRepository.findBookingByBookingId(bookingId);
        booking.setBookingStatus(new BookingStatus(BookingStatusType.RETURNED));
        bookingRepository.save(booking);
    }

    public void blacklistCustomer(long bookingId){
        //get customer from booking id
        Booking booking=bookingRepository.findBookingByBookingId(bookingId);
        Customer customer=booking.getCustomer();
        customer.setBlacklisted(true);
        customerRepository.save(customer);
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

    //true if can be extended
    public Boolean extendBooking(long booking_id) throws ParseException {
        Booking booking=bookingRepository.findBookingByBookingId(booking_id);

        UserSession userSession = (UserSession) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer= customerRepository.findByUserId(userSession.getId());

        SimpleDateFormat sdf = new SimpleDateFormat("E, dd-M-yyyy hh:mm:ss a");
        Date returnDate=sdf.parse(booking.getReturnDate());

        //vehicles and equipments for this booking
        List<BookingVehicle> bookingVehicles=bookingVehicleRepository.getAllByBooking(booking);
        List<BookingAdditionalEquipment> bookingAdditionalEquipments=bookingAdditionalEquipsRepository.getAllByBooking(booking);


        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
//        Date currentDate = new Date();

        //get return date and to calculate the next day
        LocalDateTime localDateTime = returnDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime nextDay=localDateTime.plusDays(1);

        Date nextDate=Date.from(nextDay.atZone(ZoneId.systemDefault()).toInstant());
        SimpleDateFormat sdf3 = new SimpleDateFormat("E, dd-M-yyyy");
        String next=sdf3.format(nextDate);

        Calendar cal = Calendar.getInstance();
        cal.setTime(returnDate);
        cal.set(Calendar.HOUR_OF_DAY,16);
        cal.set(Calendar.MINUTE,0);
        Date fourpm=cal.getTime(); //4pm of the return date


        Boolean canExtend=false;

        //if returndate is before 4.pm today //// sdf2.format(returnDate).equals(sdf2.format(new Date())) &&
        if(returnDate.getTime()<fourpm.getTime()){
            List<Booking> bookingsForNextDay=bookingRepository.findAllByPickupDateStartsWith(next);

            //check whether any of the booked vehicles or equipments are booked for tomorrow
            for (Booking b:bookingsForNextDay) {
                List<BookingVehicle> bvList=bookingVehicleRepository.getAllByBooking(b);
                List<BookingAdditionalEquipment> aebList=bookingAdditionalEquipsRepository.getAllByBooking(b);

                boolean areVehiclesBooked =  bvList.stream().anyMatch(element -> bookingVehicles.contains(element));
                boolean areEquipmentBooked = aebList.stream().anyMatch(element -> bookingAdditionalEquipments.contains(element));

                //if any of them are booked for tomorrow
                if(areVehiclesBooked || areEquipmentBooked){
                    return canExtend; //cannot extend (returns false)
                }
            }

            canExtend= true;
        }

        //set isExtended in booking to true if extended
        if(canExtend==true && customer.getCustomerStatus().equals("REPEAT")){
            booking.setReturnDate(sdf.format(fourpm));
            booking.setExtended(true);
            bookingRepository.save(booking);
        }

        return canExtend;
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

    //for admin
    public List<BookingDTO> viewAllBookingsForToday() throws ParseException {
        //pickups and returns for today
        Date currentDate = new Date();
        LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        Date todaysDate=Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        SimpleDateFormat sdf3 = new SimpleDateFormat("E, dd-M-yyyy");
        String today=sdf3.format(todaysDate);

        List<Booking> allBookingsForToday=bookingRepository.findAllByPickupDateStartsWithOrReturnDateStartsWith(today,today);

        List<BookingDTO> bookingsTodayDTOList=Utils.mapAll(allBookingsForToday,BookingDTO.class);
        bookingsTodayDTOList.forEach(bookingdto->{
            double total=calculateTotal(bookingdto.getBookingId());
            bookingdto.setTotal(total);
        });

        SimpleDateFormat sdf = new SimpleDateFormat("E, dd-M-yyyy hh:mm:ss a");

        for (BookingDTO bookingDTO:bookingsTodayDTOList) {
            Booking booking=bookingRepository.findBookingByBookingId(bookingDTO.getBookingId());

            Date pickupDate=sdf.parse(booking.getPickupDate());

            if(bookingDTO.getBookingStatus().getBookingStatusType().equals(BookingStatusType.PENDING) && pickupDate.getTime()<currentDate.getTime()){
                booking.setLatePickup(true);
                bookingRepository.save(booking);
                bookingDTO.setLatePickup(true);
            }

            List<BookingVehicle> vehicleList=bookingVehicleRepository.getAllByBooking(booking);
            List<BookingAdditionalEquipment> equipmentList=bookingAdditionalEquipsRepository.getAllByBooking(booking);

            bookingDTO.setVehicleList(vehicleList);
            bookingDTO.setAdditionalEquipmentList(equipmentList);
        }

        //also sending late returns and late pickups that doesnt include the today's bookings

        List<Booking> lateReturns=bookingRepository.getLateReturns();
        List<BookingDTO> lateReturnsDTOList=Utils.mapAll(lateReturns,BookingDTO.class);

        for(BookingDTO bookingDTO:lateReturnsDTOList){
            Booking booking=bookingRepository.findBookingByBookingId(bookingDTO.getBookingId());

            booking.setLateReturn(true);
            bookingRepository.save(booking);

            List<BookingVehicle> vehicleList=bookingVehicleRepository.getAllByBooking(booking);
            List<BookingAdditionalEquipment> equipmentList=bookingAdditionalEquipsRepository.getAllByBooking(booking);

            bookingDTO.setVehicleList(vehicleList);
            bookingDTO.setAdditionalEquipmentList(equipmentList);

            bookingDTO.setLateReturn(true);
        }

        List<Booking> latrPickups=bookingRepository.getLatePickups();
        List<BookingDTO> latePickupsDTOList=Utils.mapAll(latrPickups,BookingDTO.class);

        for(BookingDTO bookingDTO:latePickupsDTOList){
            Booking booking=bookingRepository.findBookingByBookingId(bookingDTO.getBookingId());

            booking.setLatePickup(true);
            bookingRepository.save(booking);

            List<BookingVehicle> vehicleList=bookingVehicleRepository.getAllByBooking(booking);
            List<BookingAdditionalEquipment> equipmentList=bookingAdditionalEquipsRepository.getAllByBooking(booking);

            bookingDTO.setVehicleList(vehicleList);
            bookingDTO.setAdditionalEquipmentList(equipmentList);

            bookingDTO.setLatePickup(true);
        }

        List<BookingDTO> bookingsDTOList= new ArrayList<>();
        bookingsDTOList.addAll(bookingsTodayDTOList);
        bookingsDTOList.addAll(lateReturnsDTOList);
        bookingsDTOList.addAll(latePickupsDTOList);

        return bookingsDTOList;
    }

    public List<VehicleDTO> pastVehiclesBookedByCustomer(){
        UserSession userSession = (UserSession) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer= customerRepository.findByUserId(userSession.getId());

        List<BookingVehicle> pastbookingVehicleList=bookingVehicleRepository.getAllByBooking_CustomerAndBooking_BookingStatus(customer,new BookingStatus(BookingStatusType.RETURNED));

        List<Vehicle> vehicleList=new ArrayList<>();

        for (BookingVehicle bv:pastbookingVehicleList) {
            vehicleList.add(bv.getVehicle());
        }

        List<Vehicle> distinctVehicleList= vehicleList.stream().distinct().collect(toList());

        return Utils.mapAll(distinctVehicleList,VehicleDTO.class);
    }

    public String getIsInsuranceOffence(String lisenceNo){
        return bookingRepository.getOffenses(lisenceNo);
    }

    public String getIsLisenceOffense(byte [] bytefile,String lisenceNo) throws IOException, CsvValidationException {

        String ret="";

        File newFile=new File("src/main/resources/templates/offences.csv");

        FileUtils.writeByteArrayToFile(newFile, bytefile);
        CSVReader reader = new CSVReaderBuilder(new FileReader("src/main/resources/templates/offences.csv")).withSkipLines(1).build();

        //Read CSV line by line and use the string array as you want
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            if (nextLine != null) {
                if(Arrays.asList(nextLine).get(0).equals(lisenceNo)){
                    ret=Arrays.asList(nextLine).get(1);
                    break;
                }
            }
        }

        return ret;
    }

    public BookingEligibilityDTO checkEligibilityToBook(String lisenceNo, byte [] bytefile) throws IOException, CsvValidationException {

        String lisenceOffence=getIsLisenceOffense(bytefile,lisenceNo);
        String insuranceOffence=getIsInsuranceOffence(lisenceNo);

        BookingEligibilityDTO dto=new BookingEligibilityDTO();
        dto.setInsuranceOffenseStatus(insuranceOffence);
        dto.setLisenceOffenseStatus(lisenceOffence);

        if(lisenceOffence.equals("LOST") || lisenceOffence.equals("SUSPENDED")||lisenceOffence.equals("STOLEN") || insuranceOffence.equals("FRADULENT_CLAIM")){
            dto.setBookingeligibility(false);
        }
        else{
            dto.setBookingeligibility(true);
        }

        if(lisenceOffence.equals("LOST") || lisenceOffence.equals("SUSPENDED")||lisenceOffence.equals("STOLEN")){
            //send email to DMV
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo("dilrukshi.p.perera@gmail.com");

            msg.setSubject("License Fraud alert");
            msg.setText("Hi, \n This is to inform that a license fraud was detected under no. "+lisenceNo+" ("+lisenceOffence+") that was trying to book a vehicle through Banger and co registered with DMV under 24356B.");

            javaMailSender.send(msg);
        }

        return dto;
    }

    public AvailableBookingItemsDTO getAvailableBookingItems(long bookingId){
        Booking booking=bookingRepository.findBookingByBookingId(bookingId);
        List<TimeSlots> timeSlotsList=timeSlotsRepository.getTimeSlotsByBooking(booking);

        List<Long> bookingIds=new ArrayList<>();

        for (TimeSlots timeSlot:timeSlotsList) {
            String dt="";
            if(timeSlot.getTimeSlot().toString().length()>10 && (!timeSlot.getTimeSlot().toString().substring(0,10).equals('T'))){
                dt=timeSlot.getTimeSlot().toString().substring(0,10).replace('T',' ');
            }
            else if(timeSlot.getTimeSlot().toString().length()>18 && (!timeSlot.getTimeSlot().toString().substring(0,18).equals('T'))){
                dt=timeSlot.getTimeSlot().toString().substring(0,18).replace('T',' ');
            }
            else {
                dt=timeSlot.getTimeSlot().toString();
            }

            //get booking ids whith the same time slots
            List<Long> bookingslots=timeSlotsRepository.getTimeSlotStartsWith(dt+"%");

            bookingIds.addAll(bookingslots);

        }

        List<Long> distinctBookingIds=bookingIds.stream().distinct().collect(Collectors.toList());

        List<Vehicle> vehiclesInBookings=new ArrayList<>();
        List<AdditionalEquipment> equipmentInBookings=new ArrayList<>();

        for (Long bid:distinctBookingIds) {
            Booking bookingg=bookingRepository.findBookingByBookingId(bid);

            List<BookingVehicle> vehicleListForThisBooking=bookingVehicleRepository.getAllByBooking(bookingg);

            vehiclesInBookings.addAll(vehicleListForThisBooking.stream().map(BookingVehicle::getVehicle).collect(toList()));

            List<BookingAdditionalEquipment> equipmentListForThisBooking=bookingAdditionalEquipsRepository.getAllByBooking(bookingg);

            equipmentInBookings.addAll(equipmentListForThisBooking.stream().map(BookingAdditionalEquipment::getEquipment).collect(toList()));
        }
        List<Vehicle> allVehicles=vehicleRepository.findAll();
        List<AdditionalEquipment> allAdditionalEquipment=additionalEquipmentRepository.findAll();

        List<Vehicle> distinctVehiclesInBookings=vehiclesInBookings.stream().distinct().collect(Collectors.toList());
        List<AdditionalEquipment> distinctEquipmentInBookings=equipmentInBookings.stream().distinct().collect(Collectors.toList());

        List<Vehicle> availableVehicles=new ArrayList<>();
        List<AdditionalEquipment> availableEquipment=new ArrayList<>();

        for (Vehicle vehicle:allVehicles) {
            if(!distinctVehiclesInBookings.contains(vehicle)){
                availableVehicles.add(vehicle);
            }
        }

        for (AdditionalEquipment equipment:allAdditionalEquipment) {
            if(!distinctEquipmentInBookings.contains(equipment)){
                availableEquipment.add(equipment);
            }
        }


        AvailableBookingItemsDTO dto=new AvailableBookingItemsDTO();
        dto.setVehicleList(availableVehicles);
        dto.setAdditionalEquipmentList(availableEquipment);

        return dto;
    }

    public void addItemForBooking(AvailableBookingItemsDTO dto){

        Booking booking=bookingRepository.findBookingByBookingId(dto.getBookingId());

        if(dto.getVehicleId()!=0){
            Vehicle vehicle=vehicleRepository.findVehicleByVehicleId(dto.getVehicleId());

            BookingVehicle bookingVehicle=new BookingVehicle();
            bookingVehicle.setBooking(booking);
            bookingVehicle.setVehicle(vehicle);

            bookingVehicleRepository.save(bookingVehicle);
        }

        if(dto.getEquipmentId()!=0){
            AdditionalEquipment additionalEquipment=additionalEquipmentRepository.findAdditionalEquipmentByEquipmentId(dto.getEquipmentId());

            BookingAdditionalEquipment bookingAdditionalEquipment=new BookingAdditionalEquipment();
            bookingAdditionalEquipment.setBooking(booking);
            bookingAdditionalEquipment.setEquipment(additionalEquipment);

            bookingAdditionalEquipsRepository.save(bookingAdditionalEquipment);
        }

    }
}
