package pl.grupanet.eakademik.reservation;

import org.commons.DormitoryId;
import org.commons.RoomNumber;
import org.commons.SlotNumber;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;
import pl.grupanet.eakademik.reservation.dto.TermSnapshot;
import pl.grupanet.eakademik.reservation.entities.LaundryReservationJpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Testcontainers
class LaundryReservationRepositoryE2ETest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private LaundryReservationRepository laundryReservationRepository;

    @Autowired
    private LaundryReservationJpaRepository jpaRepository;

    @BeforeEach
    void cleanDatabase() {
        jpaRepository.deleteAll();
    }

    @Test
    void shouldCompleteFullLaundryReservationLifecycleAgainstPostgres() {
        DormitoryId dormitoryId = new DormitoryId.Builder(UniversityNames.values()[0], "Ds-3 Bartek").build();
        ReservationId slot1 = new ReservationId(dormitoryId, new RoomNumber("Pralnia-1"), new SlotNumber(1));
        ReservationId slot2 = new ReservationId(dormitoryId, new RoomNumber("Pralnia-1"), new SlotNumber(2));

        ResidentId resident1 = new ResidentId(UUID.randomUUID());
        ResidentId resident2 = new ResidentId(UUID.randomUUID());
        ResidentId resident3 = new ResidentId(UUID.randomUUID());
        StaffId staff = new StaffId(UUID.randomUUID());

        LocalDate date1 = LocalDate.of(2026, 11, 20);
        LocalDate date2 = LocalDate.of(2026, 11, 21);

        TermSnapshot firstReserved = laundryReservationRepository.reserve(slot1, resident1, date1);
        assertEquals(ReservationStatus.RESERVED, firstReserved.status());
        assertEquals(resident1, firstReserved.ownerId());
        assertNull(firstReserved.acceptedById());

        TermSnapshot secondReserved = laundryReservationRepository.reserve(slot1, resident2, date1);
        assertEquals(ReservationStatus.RESERVED, secondReserved.status());
        assertEquals(resident2, secondReserved.ownerId());

        TermSnapshot otherDayReserved = laundryReservationRepository.reserve(slot2, resident1, date2);
        assertEquals(ReservationStatus.RESERVED, otherDayReserved.status());

        assertThrows(
                IllegalArgumentException.class,
                () -> laundryReservationRepository.reserve(slot1, resident1, date1)
        );

        List<TermSnapshot> allReserved = laundryReservationRepository.findAllTermsForSlotAndDate(
                List.of(ReservationStatus.RESERVED),
                List.of(date1, date2)
        );
        assertEquals(3, allReserved.size());

        TermSnapshot accepted = laundryReservationRepository.accept(slot1, resident1, staff, date1);
        assertEquals(ReservationStatus.ACCEPTED, accepted.status());
        assertEquals(resident1, accepted.ownerId());
        assertEquals(staff, accepted.acceptedById());

        assertThrows(
                IllegalArgumentException.class,
                () -> laundryReservationRepository.accept(slot1, resident2, staff, date1)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> laundryReservationRepository.reserve(slot1, resident3, date1)
        );

        List<TermSnapshot> acceptedOnDate1 = laundryReservationRepository.findAllTermsForSlotAndDate(
                List.of(ReservationStatus.ACCEPTED),
                List.of(date1)
        );
        assertEquals(1, acceptedOnDate1.size());
        assertEquals(resident1, acceptedOnDate1.getFirst().ownerId());
        assertEquals(staff, acceptedOnDate1.getFirst().acceptedById());

        List<TermSnapshot> reservedOnDate1 = laundryReservationRepository.findAllTermsForSlotAndDate(
                List.of(ReservationStatus.RESERVED),
                List.of(date1)
        );
        assertEquals(1, reservedOnDate1.size());
        assertEquals(resident2, reservedOnDate1.getFirst().ownerId());

        TermSnapshot cancelledByOwner = laundryReservationRepository.cancelByOwner(slot1, resident2, date1);
        assertEquals(ReservationStatus.OPEN, cancelledByOwner.status());
        assertNull(cancelledByOwner.ownerId());

        TermSnapshot cancelledByStaff = laundryReservationRepository.cancelByStaff(slot1, resident1, staff, date1);
        assertEquals(ReservationStatus.OPEN, cancelledByStaff.status());
        assertNull(cancelledByStaff.ownerId());
        assertNull(cancelledByStaff.acceptedById());

        List<TermSnapshot> remainingOnDate1 = laundryReservationRepository.findAllTermsForSlotAndDate(
                List.of(ReservationStatus.RESERVED, ReservationStatus.ACCEPTED),
                List.of(date1)
        );
        assertTrue(remainingOnDate1.isEmpty());

        List<TermSnapshot> remainingOnDate2 = laundryReservationRepository.findAllTermsForSlotAndDate(
                List.of(ReservationStatus.RESERVED, ReservationStatus.ACCEPTED),
                List.of(date2)
        );
        assertEquals(1, remainingOnDate2.size());
        assertEquals(slot2, remainingOnDate2.getFirst().id());

        TermSnapshot reReserved = laundryReservationRepository.reserve(slot1, resident3, date1);
        assertEquals(ReservationStatus.RESERVED, reReserved.status());
        assertEquals(resident3, reReserved.ownerId());
    }
}