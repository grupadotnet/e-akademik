package pl.grupanet.eakademik.dormitory;

import org.commons.DormitoryId;
import org.commons.Name;
import pl.grupanet.eakademik.dormitory.slots.SlotsRoomSpecification;

public class DormitorySpecificationFactory {
    protected final DormitoryId dormitoryId;

    public DormitorySpecificationFactory(String universityName, String dormitoryName) {
        this.dormitoryId = new DormitoryId(new Name(universityName), new Name(dormitoryName));
    }

    public DormitorySpecification create() {
        return new DormitorySpecification(dormitoryId, buildSlotsRoomSpecification());
    }

    public SlotsRoomSpecification buildSlotsRoomSpecification() {
        return SlotsRoomSpecification.defaultRoomSpecification;
    }
}
