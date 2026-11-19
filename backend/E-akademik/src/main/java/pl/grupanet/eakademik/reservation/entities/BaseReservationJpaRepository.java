package pl.grupanet.eakademik.reservation.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@NoRepositoryBean
public interface BaseReservationJpaRepository<E extends BaseReservationEntity>
        extends JpaRepository<E, ReservationEmbeddableId> {

    List<E> findAllByStatusIn(Collection<ReservationStatus> statuses);

    List<E> findAllByReservationDateIn(Collection<LocalDate> reservationDates);

    List<E> findAllById_UniversityNameAndId_DormitoryNameAndId_RoomNumberAndId_SlotNumberAndId_ReservationDate(
            UniversityNames universityName,
            String dormitoryName,
            String roomNumber,
            Integer slotNumber,
            LocalDate reservationDate
    );

    List<E> findAllByStatusInAndReservationDateIn(
            Collection<ReservationStatus> statuses,
            Collection<LocalDate> reservationDates
    );
}