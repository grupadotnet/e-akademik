package pl.grupanet.eakademik.dormitory;

import jakarta.annotation.Nonnull;
import org.commons.DormitoryId;
import pl.grupanet.eakademik.dormitory.slots.SlotsRoomSpecification;

public record DormitorySpecification(DormitoryId dormitoryId, SlotsRoomSpecification slotsRoomSpecification) {
    public DormitorySpecification(@Nonnull final DormitoryId dormitoryId,
                                  @Nonnull final SlotsRoomSpecification slotsRoomSpecification) {
        this.dormitoryId = dormitoryId;
        this.slotsRoomSpecification = slotsRoomSpecification;
    }
}
