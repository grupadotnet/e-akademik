package pl.grupanet.eakademik.reservation;

import org.commons.DormitoryId;
import org.commons.RoomNumber;
import org.commons.SlotNumber;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.reservation.dto.TermSnapshot;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ReservationTest {
    private static class DummyReservation extends Term {
        public DummyReservation(ReservationId reservationId, ReservationStatus reservationStatus, LocalDate localDate) {
            super(reservationId, reservationStatus, localDate);
        }

        @Override
        protected TermSnapshot toDto() {
            return null;
        }
    }

    private ReservationId createDummyReservationId() {
        return new ReservationId(
                new DormitoryId.Builder(
                        UniversityNames.POLITECHNIKA_KRAKOWSKA, "sth")
                        .build(),
                new RoomNumber("A001"),
                new SlotNumber(0));
    }

    @ParameterizedTest
    @EnumSource(ReservationStatus.class)
    void shouldBuildReservation(ReservationStatus reservationStatus) {
        ReservationId reservationId = createDummyReservationId();
        LocalDate localDate = LocalDate.now();
        DummyReservation dummyReservation = new DummyReservation(reservationId, reservationStatus, localDate);

        assertEquals(dummyReservation.getReservationId(), reservationId);
        assertEquals(dummyReservation.getStatus(), reservationStatus);
        assertNull(dummyReservation.toDto());
        assertEquals(dummyReservation.getLocalDate(), localDate);
    }
}