package pl.grupanet.eakademik.reservation;

import org.commons.DormitoryId;
import org.commons.RoomNumber;
import org.commons.SlotNumber;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;
import pl.grupanet.eakademik.reservation.dto.TermSnapshot;
import pl.grupanet.eakademik.reservation.entities.BaseReservationEntity;
import pl.grupanet.eakademik.reservation.entities.BaseReservationJpaRepository;
import pl.grupanet.eakademik.reservation.entities.ReservationEmbeddableId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppReservationRepositoryTest {

    @Mock
    private BaseReservationJpaRepository<TestReservationEntity> jpaRepository;

    private AppReservationRepository<TestReservationEntity> repository;

    private ReservationId reservationId;
    private ResidentId residentId;
    private StaffId staffId;
    private LocalDate reservationDate;

    private static class TestReservationEntity extends BaseReservationEntity {
    }

    @BeforeEach
    void setUp() {
        repository = new AppReservationRepository<>(jpaRepository, TestReservationEntity::new);

        DormitoryId dormitoryId = new DormitoryId.Builder(UniversityNames.values()[0], "Akademik 1").build();
        reservationId = new ReservationId(dormitoryId, new RoomNumber("101"), new SlotNumber(1));
        residentId = new ResidentId(UUID.randomUUID());
        staffId = new StaffId(UUID.randomUUID());
        reservationDate = LocalDate.of(2026, 11, 20);
    }

    // --- findAllTermsForSlotAndDate ---

    @Test
    void shouldReturnSnapshotsWhenSearchingByStatusesAndDates() {
        TestReservationEntity entity = createEntity(reservationId, residentId, reservationDate, ReservationStatus.ACCEPTED, staffId);
        List<ReservationStatus> statuses = List.of(ReservationStatus.ACCEPTED);
        List<LocalDate> dates = List.of(reservationDate);

        when(jpaRepository.findAllByStatusInAndId_ReservationDateIn(statuses, dates))
                .thenReturn(List.of(entity));

        List<TermSnapshot> result = repository.findAllTermsForSlotAndDate(statuses, dates);

        assertEquals(1, result.size());
        TermSnapshot snapshot = result.getFirst();

        assertEquals(reservationId, snapshot.id());
        assertEquals(ReservationStatus.ACCEPTED, snapshot.status());
        assertEquals(residentId, snapshot.ownerId());
        assertEquals(staffId, snapshot.acceptedById());


    }

    @Test
    void shouldReturnEmptyListWhenStatusesOrDatesAreEmpty() {
        assertTrue(repository.findAllTermsForSlotAndDate(List.of(), List.of(reservationDate)).isEmpty());
        assertTrue(repository.findAllTermsForSlotAndDate(List.of(ReservationStatus.RESERVED), List.of()).isEmpty());
        assertTrue(repository.findAllTermsForSlotAndDate(null, List.of(reservationDate)).isEmpty());
        assertTrue(repository.findAllTermsForSlotAndDate(List.of(ReservationStatus.RESERVED), null).isEmpty());

        verifyNoInteractions(jpaRepository);
    }

    @Test
    void shouldThrowExceptionWhenSearchingForOpenStatus() {
        List<ReservationStatus> statuses = List.of(ReservationStatus.OPEN);
        List<LocalDate> dates = List.of(reservationDate);

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.findAllTermsForSlotAndDate(statuses, dates)
        );
        verifyNoInteractions(jpaRepository);
    }

    // --- reserve ---

    @Test
    void shouldReserveSlotWhenNoExistingReservations() {
        stubSlotQuery(List.of());

        TermSnapshot snapshot = repository.reserve(reservationId, residentId, reservationDate);

        ArgumentCaptor<TestReservationEntity> entityCaptor = ArgumentCaptor.forClass(TestReservationEntity.class);
        verify(jpaRepository).save(entityCaptor.capture());

        TestReservationEntity savedEntity = entityCaptor.getValue();
        assertEquals(ReservationEmbeddableId.from(reservationId, residentId, reservationDate), savedEntity.getId());
        assertEquals(ReservationStatus.RESERVED, savedEntity.getStatus());
        assertNull(savedEntity.getAcceptedById());

        assertEquals(reservationId, snapshot.id());
        assertEquals(ReservationStatus.RESERVED, snapshot.status());
        assertEquals(residentId, snapshot.ownerId());
        assertNull(snapshot.acceptedById());
    }

    @Test
    void shouldAllowAnotherResidentToReserveSameSlot() {
        ResidentId otherResident = new ResidentId(UUID.randomUUID());
        TestReservationEntity existingEntity = createEntity(reservationId, otherResident, reservationDate, ReservationStatus.RESERVED, null);
        stubSlotQuery(List.of(existingEntity));

        TermSnapshot snapshot = repository.reserve(reservationId, residentId, reservationDate);

        verify(jpaRepository).save(any(TestReservationEntity.class));
        assertEquals(ReservationStatus.RESERVED, snapshot.status());
        assertEquals(residentId, snapshot.ownerId());
    }

    @Test
    void shouldThrowExceptionWhenResidentAlreadyReservedSameSlot() {
        TestReservationEntity existingEntity = createEntity(reservationId, residentId, reservationDate, ReservationStatus.RESERVED, null);
        stubSlotQuery(List.of(existingEntity));

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.reserve(reservationId, residentId, reservationDate)
        );
        verify(jpaRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenSlotIsAlreadyAccepted() {
        ResidentId otherResident = new ResidentId(UUID.randomUUID());
        TestReservationEntity acceptedEntity = createEntity(reservationId, otherResident, reservationDate, ReservationStatus.ACCEPTED, staffId);
        stubSlotQuery(List.of(acceptedEntity));

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.reserve(reservationId, residentId, reservationDate)
        );
        verify(jpaRepository, never()).save(any());
    }

    // --- accept ---

    @Test
    void shouldAcceptReservedTerm() {
        ReservationEmbeddableId embeddableId = ReservationEmbeddableId.from(reservationId, residentId, reservationDate);
        TestReservationEntity existingEntity = createEntity(reservationId, residentId, reservationDate, ReservationStatus.RESERVED, null);

        when(jpaRepository.findAllByStatusInAndId_ReservationDateIn(List.of(ReservationStatus.ACCEPTED), List.of(reservationDate)))
                .thenReturn(List.of());
        when(jpaRepository.findById(embeddableId)).thenReturn(Optional.of(existingEntity));

        TermSnapshot snapshot = repository.accept(reservationId, residentId, staffId, reservationDate);

        verify(jpaRepository).save(existingEntity);
        assertEquals(ReservationStatus.ACCEPTED, existingEntity.getStatus());
        assertEquals(staffId.getIdAsUUID(), existingEntity.getAcceptedById());

        assertEquals(ReservationStatus.ACCEPTED, snapshot.status());
        assertEquals(residentId, snapshot.ownerId());
        assertEquals(staffId, snapshot.acceptedById());
    }

    @Test
    void shouldThrowExceptionWhenAcceptingAlreadyAcceptedSlot() {
        ResidentId otherResident = new ResidentId(UUID.randomUUID());
        TestReservationEntity acceptedEntity = createEntity(reservationId, otherResident, reservationDate, ReservationStatus.ACCEPTED, staffId);

        when(jpaRepository.findAllByStatusInAndId_ReservationDateIn(List.of(ReservationStatus.ACCEPTED), List.of(reservationDate)))
                .thenReturn(List.of(acceptedEntity));

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.accept(reservationId, residentId, staffId, reservationDate)
        );
        verify(jpaRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenAcceptingNonExistentReservation() {
        ReservationEmbeddableId embeddableId = ReservationEmbeddableId.from(reservationId, residentId, reservationDate);
        when(jpaRepository.findAllByStatusInAndId_ReservationDateIn(List.of(ReservationStatus.ACCEPTED), List.of(reservationDate)))
                .thenReturn(List.of());
        when(jpaRepository.findById(embeddableId)).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.accept(reservationId, residentId, staffId, reservationDate)
        );
        verify(jpaRepository, never()).save(any());
    }

    // --- cancelByStaff ---

    @Test
    void shouldCancelAcceptedReservationByStaffAndDeleteEntity() {
        ReservationEmbeddableId embeddableId = ReservationEmbeddableId.from(reservationId, residentId, reservationDate);
        TestReservationEntity acceptedEntity = createEntity(reservationId, residentId, reservationDate, ReservationStatus.ACCEPTED, staffId);
        when(jpaRepository.findById(embeddableId)).thenReturn(Optional.of(acceptedEntity));

        TermSnapshot snapshot = repository.cancelByStaff(reservationId, residentId, staffId, reservationDate);

        verify(jpaRepository).delete(acceptedEntity);
        assertEquals(reservationId, snapshot.id());
        assertEquals(ReservationStatus.OPEN, snapshot.status());
        assertNull(snapshot.ownerId());
        assertNull(snapshot.acceptedById());
    }

    @Test
    void shouldThrowExceptionWhenStaffCancelsNonAcceptedReservation() {
        ReservationEmbeddableId embeddableId = ReservationEmbeddableId.from(reservationId, residentId, reservationDate);
        TestReservationEntity reservedEntity = createEntity(reservationId, residentId, reservationDate, ReservationStatus.RESERVED, null);
        when(jpaRepository.findById(embeddableId)).thenReturn(Optional.of(reservedEntity));

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.cancelByStaff(reservationId, residentId, staffId, reservationDate)
        );
        verify(jpaRepository, never()).delete(any());
    }

    // --- cancelByOwner ---

    @Test
    void shouldCancelReservedReservationByOwnerAndDeleteEntity() {
        ReservationEmbeddableId embeddableId = ReservationEmbeddableId.from(reservationId, residentId, reservationDate);
        TestReservationEntity reservedEntity = createEntity(reservationId, residentId, reservationDate, ReservationStatus.RESERVED, null);
        when(jpaRepository.findById(embeddableId)).thenReturn(Optional.of(reservedEntity));

        TermSnapshot snapshot = repository.cancelByOwner(reservationId, residentId, reservationDate);

        verify(jpaRepository).delete(reservedEntity);
        assertEquals(ReservationStatus.OPEN, snapshot.status());
        assertNull(snapshot.ownerId());
    }

    @Test
    void shouldCancelAcceptedReservationByOwnerAndDeleteEntity() {
        ReservationEmbeddableId embeddableId = ReservationEmbeddableId.from(reservationId, residentId, reservationDate);
        TestReservationEntity acceptedEntity = createEntity(reservationId, residentId, reservationDate, ReservationStatus.ACCEPTED, staffId);
        when(jpaRepository.findById(embeddableId)).thenReturn(Optional.of(acceptedEntity));

        TermSnapshot snapshot = repository.cancelByOwner(reservationId, residentId, reservationDate);

        verify(jpaRepository).delete(acceptedEntity);
        assertEquals(ReservationStatus.OPEN, snapshot.status());
        assertNull(snapshot.ownerId());
    }

    @Test
    void shouldThrowExceptionWhenOwnerCancelsNonExistentReservation() {
        ReservationEmbeddableId embeddableId = ReservationEmbeddableId.from(reservationId, residentId, reservationDate);
        when(jpaRepository.findById(embeddableId)).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.cancelByOwner(reservationId, residentId, reservationDate)
        );
        verify(jpaRepository, never()).delete(any());
    }

    private void stubSlotQuery(List<TestReservationEntity> entities) {
        when(jpaRepository.findAllById_UniversityNameAndId_DormitoryNameAndId_RoomNumberAndId_SlotNumberAndId_ReservationDate(
                reservationId.dormitoryId().getUniversityName(),
                reservationId.dormitoryId().getDormitoryName().toString(),
                reservationId.roomNumber().toString(),
                reservationId.slotNumber().getNumberAsInt(),
                reservationDate
        )).thenReturn(entities);
    }

    private TestReservationEntity createEntity(ReservationId id,
                                               ResidentId owner,
                                               LocalDate date,
                                               ReservationStatus status,
                                               StaffId acceptedBy) {
        TestReservationEntity entity = new TestReservationEntity();
        entity.setId(ReservationEmbeddableId.from(id, owner, date));
        entity.setStatus(status);
        entity.setAcceptedById(acceptedBy != null ? acceptedBy.getIdAsUUID() : null);
        return entity;
    }
}