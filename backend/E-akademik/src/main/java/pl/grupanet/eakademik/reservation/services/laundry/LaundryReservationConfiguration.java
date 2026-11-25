package pl.grupanet.eakademik.reservation.services.laundry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.grupanet.eakademik.dormitory.repositories.DormitoryRepository;
import pl.grupanet.eakademik.reservation.LaundryReservationRepository;
import pl.grupanet.eakademik.user.UserProfileRepository;

@Configuration
public class LaundryReservationConfiguration {
    private final UserProfileRepository userProfileRepository;
    private final LaundryReservationRepository laundryReservationRepository;
    private final DormitoryRepository dormitoryRepository;

    @Autowired
    public LaundryReservationConfiguration(UserProfileRepository userProfileRepository, LaundryReservationRepository laundryReservationRepository, DormitoryRepository dormitoryRepository) {
        this.userProfileRepository = userProfileRepository;
        this.laundryReservationRepository = laundryReservationRepository;
        this.dormitoryRepository = dormitoryRepository;
    }

    @Bean
    public LaundryReservationService laundryReservationService() {
        ReservationContext reservationContext = new ReservationContext(userProfileRepository, dormitoryRepository);
        return new AppLaundryReservationService(userProfileRepository, laundryReservationRepository, reservationContext,
                dormitoryRepository);
    }
}
