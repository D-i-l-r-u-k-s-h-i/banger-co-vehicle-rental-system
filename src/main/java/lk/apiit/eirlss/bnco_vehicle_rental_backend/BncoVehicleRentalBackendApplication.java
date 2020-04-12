package lk.apiit.eirlss.bnco_vehicle_rental_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class BncoVehicleRentalBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BncoVehicleRentalBackendApplication.class, args);
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
