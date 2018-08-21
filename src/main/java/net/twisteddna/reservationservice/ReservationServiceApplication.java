package net.twisteddna.reservationservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ReservationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationServiceApplication.class, args);
    }
}

@RestController
class ReservationRestController {

    private ReservationRepository rr;

    public ReservationRestController(ReservationRepository rr) {
        this.rr = rr;
    }

    @GetMapping("/reservations")
    Flux<Reservation> reservationFlux() {
        return this.rr.findAll();
    }
}

@Component
class DataWriter implements ApplicationRunner {

    private ReservationRepository reservationRepository;

    public DataWriter(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        this.reservationRepository
                .deleteAll()
                .thenMany(Flux.just("Aleksandra", "Beatrix", "Clare", "Diana")
                        .map(name -> new Reservation(null, name))
                        .flatMap(this.reservationRepository::save))
                .thenMany(this.reservationRepository.findAll())
                .subscribe(System.out::println);
    }
}

interface ReservationRepository extends ReactiveMongoRepository<Reservation, String> {
}

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
class Reservation {
    @Id
    String id;
    String reservationName;
}