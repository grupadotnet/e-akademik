package pl.grupanet.eakademik.dormitory;

import lombok.NonNull;
import org.commons.DormitoryId;
import org.commons.Name;
import pl.grupanet.eakademik.dormitory.slots.SlotsRoomSpecification;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;

public class DormitorySpecificationFactory {
    protected final DormitoryId dormitoryId;

    public DormitorySpecificationFactory(@NonNull UniversityNames universityName, String dormitoryName) {
        this.dormitoryId = new DormitoryId(new Name(universityName.toString()), new Name(dormitoryName));
    }

    public DormitorySpecification create() {
        return new DormitorySpecification(dormitoryId, buildSlotsRoomSpecification());
    }

    public SlotsRoomSpecification buildSlotsRoomSpecification() {
        return SlotsRoomSpecification.defaultRoomSpecification;
    }
}
