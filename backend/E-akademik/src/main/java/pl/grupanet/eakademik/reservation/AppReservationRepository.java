package pl.grupanet.eakademik.reservation;

import lombok.NonNull;
import org.commons.DormitoryId;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import org.springframework.transaction.annotation.Transactional;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;
import pl.grupanet.eakademik.reservation.dto.TermSnapshot;
import pl.grupanet.eakademik.reservation.entities.BaseReservationEntity;
import pl.grupanet.eakademik.reservation.entities.BaseReservationJpaRepository;
import pl.grupanet.eakademik.reservation.entities.ReservationEmbeddableId;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

class AppReservationRepository<E extends BaseReservationEntity> implements ReservationRepository {
    private final BaseReservationJpaRepository<E> jpaRepository;
    private final Supplier<E> entityFactory;

    public AppReservationRepository(BaseReservationJpaRepository<E> jpaRepository, Supplier<E> entityFactory) {
        this.jpaRepository = jpaRepository;
        this.entityFactory = entityFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TermSnapshot> findAllTermsForSlotAndDate(List<ReservationStatus> statuses, List<LocalDate> dates) {
        validateStatuses(statuses);
        if (statuses == null || statuses.isEmpty() || dates == null || dates.isEmpty()) {
            return List.of();
        }

        return jpaRepository.findAllByStatusInAndId_ReservationDateIn(statuses, dates).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public TermSnapshot reserve(@NonNull final ReservationId id,
                                @NonNull final ResidentId residentId,
                                @NonNull final LocalDate reservationDate) {
        ReservationEmbeddableId reservationEmbeddableId = ReservationEmbeddableId.from(id, residentId, reservationDate);
        List<E> entities = jpaRepository.findAllById_UniversityNameAndId_DormitoryNameAndId_RoomNumberAndId_SlotNumberAndId_ReservationDate(
                reservationEmbeddableId.getUniversityName(),
                reservationEmbeddableId.getDormitoryName(),
                reservationEmbeddableId.getRoomNumber(),
                reservationEmbeddableId.getSlotNumber(),
                reservationDate
        );
        List<Term> reservations = mapToReservationList(entities);

        validateReserveStatuses(reservations, residentId);

        OpenTerm openReservation = new OpenTerm(id);
        ReservedTerm reservedReservation = openReservation.reserve(residentId);

        E newEntity = entityFactory.get();
        newEntity.setId(ReservationEmbeddableId.from(
                reservedReservation.getReservationId(),
                reservedReservation.getOwnerId(),
                reservationDate
        ));
        newEntity.setStatus(ReservationStatus.RESERVED);
        jpaRepository.save(newEntity);

        return reservedReservation.toDto();
    }

    @Override
    @Transactional
    public TermSnapshot accept(@NonNull final ReservationId id,
                               @NonNull final ResidentId residentId,
                               @NonNull final StaffId staffId,
                               @NonNull final LocalDate reservationDate) {
        ReservationEmbeddableId reservationEmbeddableId = ReservationEmbeddableId.from(id, residentId, reservationDate);

        if (isSlotAlreadyAccepted(id, reservationDate)) {
            throw new IllegalArgumentException("Slot is already accepted.");
        }

        E entity = jpaRepository.findById(reservationEmbeddableId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        Term reservation = toDomain(entity);
        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            throw new IllegalArgumentException("Cannot accept a reservation with status " + reservation.getStatus());
        }

        ReservedTerm reservedReservation = (ReservedTerm) reservation;
        AcceptedTerm acceptedReservation = reservedReservation.accept(staffId);

        entity.setStatus(ReservationStatus.ACCEPTED);
        entity.setAcceptedById(acceptedReservation.getAcceptedId().getIdAsUUID());
        jpaRepository.save(entity);

        return acceptedReservation.toDto();
    }

    private boolean isSlotAlreadyAccepted(ReservationId reservationId, LocalDate reservationDate) {
        DormitoryId dormitoryId = reservationId.dormitoryId();
        return jpaRepository.findAllForSlotWithLock(
                        dormitoryId.getUniversityName(),
                        dormitoryId.getDormitoryName().toString(),
                        reservationId.roomNumber().toString(),
                        reservationId.slotNumber().getNumberAsInt(),
                        reservationDate
                ).stream().
                anyMatch(entity -> entity.getStatus() == ReservationStatus.ACCEPTED);
    }

    @Override
    @Transactional
    public TermSnapshot cancelByStaff(@NonNull final ReservationId id,
                                      @NonNull final ResidentId residentId,
                                      @NonNull final StaffId staffId,
                                      @NonNull final LocalDate reservationDate) {
        ReservationEmbeddableId reservationEmbeddableId = ReservationEmbeddableId.from(id, residentId, reservationDate);
        E entity = jpaRepository.findById(reservationEmbeddableId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        Term reservation = toDomain(entity);
        if (reservation.getStatus() != ReservationStatus.ACCEPTED) {
            throw new IllegalArgumentException("Cannot cancel a reservation with status " + reservation.getStatus());
        }

        AcceptedTerm acceptedReservation = (AcceptedTerm) reservation;
        OpenTerm openReservation = acceptedReservation.cancel(staffId);

        jpaRepository.delete(entity);
        return openReservation.toDto();
    }

    @Override
    @Transactional
    public TermSnapshot cancelByOwner(@NonNull final ReservationId id,
                                      @NonNull final ResidentId residentId,
                                      @NonNull final LocalDate reservationDate) {
        ReservationEmbeddableId reservationEmbeddableId = ReservationEmbeddableId.from(id, residentId, reservationDate);
        E entity = jpaRepository.findById(reservationEmbeddableId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        Term reservation = toDomain(entity);


        if (reservation.getStatus() == ReservationStatus.OPEN) {
            throw new IllegalArgumentException("Cannot cancel a reservation with status " + reservation.getStatus());
        }

        OpenTerm openReservation = null;
        if (reservation.getStatus() == ReservationStatus.RESERVED) {
            ReservedTerm reservedReservation = (ReservedTerm) reservation;
            openReservation = reservedReservation.cancel(residentId);
        } else if (reservation.getStatus() == ReservationStatus.ACCEPTED) {
            AcceptedTerm acceptedReservation = (AcceptedTerm) reservation;
            openReservation = acceptedReservation.cancel(residentId);
        }

        if (openReservation == null)
            throw new IllegalArgumentException("Cannot cancel a reservation with status " + reservation.getStatus());

        jpaRepository.delete(entity);
        return openReservation.toDto();
    }

    private void validateReserveStatuses(List<Term> reservations, ResidentId residentId) {
        for (Term reservation : reservations) {
            if (reservation.getStatus() == ReservationStatus.ACCEPTED) {
                throw new IllegalArgumentException("Reservation already accepted.");
            }
            if (reservation instanceof ReservedTerm reservedReservation) {
                if (reservedReservation.getOwnerId().equals(residentId)) {
                    throw new IllegalArgumentException("Term already reserved.");
                }
            }
        }
    }

    private void validateStatuses(List<ReservationStatus> statuses) {
        if (statuses != null && statuses.contains(ReservationStatus.OPEN)) {
            throw new IllegalArgumentException("OPEN terms are not stored in the database");
        }
    }

    private TermSnapshot toDto(E entity) {
        UUID ownerId = entity.getId().getOwnerId();
        UUID acceptedById = entity.getAcceptedById();
        return new TermSnapshot(
                entity.getId().toDomain(),
                entity.getStatus(),
                ownerId != null ? new ResidentId(ownerId) : null,
                acceptedById != null ? new StaffId(acceptedById) : null
        );
    }

    private List<Term> mapToReservationList(List<E> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    private Term toDomain(E entity) {
        ReservationEmbeddableId embeddableId = entity.getId();
        ReservationId domainId = embeddableId.toDomain();
        ResidentId ownerId = new ResidentId(embeddableId.getOwnerId());

        return switch (entity.getStatus()) {
            case RESERVED -> new ReservedTerm(domainId, ownerId);
            case ACCEPTED -> new AcceptedTerm(
                    domainId,
                    ownerId,
                    new StaffId(entity.getAcceptedById())
            );
            case OPEN -> throw new IllegalStateException("Open terms should not be stored in database");
        };
    }
}
