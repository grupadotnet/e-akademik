package pl.grupanet.eakademik.dormitory.spec.pk.bartek;

import pl.grupanet.eakademik.dormitory.DormitorySpecificationFactory;
import pl.grupanet.eakademik.dormitory.slots.SlotsRoomSpecification;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;

public class BartekSpecificationFactory extends DormitorySpecificationFactory {

    public BartekSpecificationFactory() {
        super(UniversityNames.POLITECHNIKA_KRAKOWSKA, "Ds-3 Bartek");
    }

    @Override
    public SlotsRoomSpecification buildSlotsRoomSpecification() {
        return new BartekLaundryRoomFactory().create();
    }
}