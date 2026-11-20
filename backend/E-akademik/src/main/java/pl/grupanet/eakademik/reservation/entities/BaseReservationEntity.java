package pl.grupanet.eakademik.reservation.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;

import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseReservationEntity {
    @EmbeddedId
    private ReservationEmbeddableId id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(name = "accepted_by_id")
    private UUID acceptedById;

    @Version
    private Long version;
}