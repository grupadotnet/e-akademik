package pl.grupanet.eakademik.reservation;

import pl.grupanet.eakademik.reservation.entities.LaundryReservationEntity;
import pl.grupanet.eakademik.reservation.entities.LaundryReservationJpaRepository;

public class JpaLaundryReservationRepository
        extends AppReservationRepository<LaundryReservationEntity>
        implements LaundryReservationRepository {

    public JpaLaundryReservationRepository(LaundryReservationJpaRepository jpaRepository) {
        super(jpaRepository, LaundryReservationEntity::new);
    }
}