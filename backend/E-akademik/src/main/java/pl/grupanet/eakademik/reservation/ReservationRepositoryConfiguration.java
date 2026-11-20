package pl.grupanet.eakademik.reservation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.grupanet.eakademik.reservation.entities.LaundryReservationJpaRepository;

@Configuration
public class ReservationRepositoryConfiguration {
    @Bean
    public LaundryReservationRepository laundryReservationRepository(
            LaundryReservationJpaRepository jpaRepository
    ) {
        return new JpaLaundryReservationRepository(jpaRepository);
    }
}
