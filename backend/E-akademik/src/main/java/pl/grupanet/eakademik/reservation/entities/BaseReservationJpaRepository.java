package pl.grupanet.eakademik.reservation.entities;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@NoRepositoryBean
public interface BaseReservationJpaRepository<E extends BaseReservationEntity>
        extends JpaRepository<E, ReservationEmbeddableId> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT r FROM #{#entityName} r
    WHERE r.id.universityName = :uni
      AND r.id.dormitoryName = :dorm
      AND r.id.roomNumber = :room
      AND r.id.slotNumber = :slot
      AND r.id.reservationDate = :date
""")
    List<E> findAllForSlotWithLock(
            @Param("uni") UniversityNames uni,
            @Param("dorm") String dorm,
            @Param("room") String room,
            @Param("slot") int slot,
            @Param("date") LocalDate date
    );

    List<E> findAllById_UniversityNameAndId_DormitoryNameAndId_RoomNumberAndId_SlotNumberAndId_ReservationDate(
            UniversityNames universityName,
            String dormitoryName,
            String roomNumber,
            Integer slotNumber,
            LocalDate reservationDate
    );

    List<E> findAllByStatusInAndId_ReservationDateIn(
            Collection<ReservationStatus> statuses,
            Collection<LocalDate> reservationDates
    );
}