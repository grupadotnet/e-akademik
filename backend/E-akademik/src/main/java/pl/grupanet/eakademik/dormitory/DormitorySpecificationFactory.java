package pl.grupanet.eakademik.dormitory;

import lombok.Setter;
import org.commons.DormitoryId;
import org.commons.Name;
import pl.grupanet.eakademik.dormitory.slots.ChooseSlotsCalculator;

public class DormitorySpecificationFactory {
    protected final DormitoryId dormitoryId;
    @Setter
    protected ChooseSlotsCalculator chooseLaundrySlotsCalculator;

    public DormitorySpecificationFactory(String universityName, String dormitoryName) {
        this.dormitoryId = new DormitoryId(new Name(universityName), new Name(dormitoryName));
        this.chooseLaundrySlotsCalculator = ChooseSlotsCalculator.defaultChooseCalculator;
    }

    public DormitorySpecification create() {
        return new DormitorySpecification(dormitoryId, chooseLaundrySlotsCalculator);
    }
}
