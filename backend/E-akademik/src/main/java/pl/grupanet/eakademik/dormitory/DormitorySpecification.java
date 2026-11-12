package pl.grupanet.eakademik.dormitory;

import jakarta.annotation.Nonnull;
import org.commons.DormitoryId;
import pl.grupanet.eakademik.dormitory.slots.ChooseSlotsCalculator;

public record DormitorySpecification(DormitoryId dormitoryId, ChooseSlotsCalculator chooseLaundrySlotsCalculator) {
    public DormitorySpecification(@Nonnull final DormitoryId dormitoryId,
                                  @Nonnull final ChooseSlotsCalculator chooseLaundrySlotsCalculator) {
        this.dormitoryId = dormitoryId;
        this.chooseLaundrySlotsCalculator = chooseLaundrySlotsCalculator;
    }
}
