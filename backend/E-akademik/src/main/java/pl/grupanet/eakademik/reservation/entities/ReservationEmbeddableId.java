package pl.grupanet.eakademik.reservation.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.commons.DormitoryId;
import org.commons.RoomNumber;
import org.commons.SlotNumber;
import org.commons.users.ResidentId;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.reservation.ReservationId;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class ReservationEmbeddableId implements Serializable {

    @Enumerated(EnumType.STRING)
    @Column(name = "university_name", nullable = false)
    private UniversityNames universityName;

    @Column(name = "dormitory_name", nullable = false)
    private String dormitoryName;

    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @Column(name = "slot_number", nullable = false)
    private Integer slotNumber;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    public static ReservationEmbeddableId from(ReservationId id, ResidentId residentId, LocalDate reservationDate) {
        DormitoryId dormId = id.dormitoryId();
        return new ReservationEmbeddableId(
                dormId.getUniversityName(),
                dormId.getDormitoryName().toString(),
                id.roomNumber().toString(),
                id.slotNumber().getNumberAsInt(),
                residentId.getIdAsUUID(),
                reservationDate
        );
    }

    public ReservationId toDomain() {
        DormitoryId dormId = new DormitoryId.Builder(universityName, dormitoryName).build();
        return new ReservationId(
                dormId,
                new RoomNumber(roomNumber),
                new SlotNumber(slotNumber)
        );
    }
}