package pl.grupanet.eakademik.dormitory.specifications.pk.bartek;

import org.commons.DormitoryId;
import org.commons.Name;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.DormitorySpecification;
import pl.grupanet.eakademik.dormitory.slots.SlotsRoomSpecification;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.dormitory.spec.pk.bartek.BartekLaundryRoomFactory;
import pl.grupanet.eakademik.dormitory.spec.pk.bartek.BartekSpecificationFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BartekSpecificationTest {

    @Test
    void shouldBuildSpecification() {
        BartekSpecificationFactory factory = new BartekSpecificationFactory();
        DormitorySpecification dormitorySpecification = factory.create();

        assertEquals(new DormitoryId(
                new Name(UniversityNames.POLITECHNIKA_KRAKOWSKA.toString()),
                new Name("Ds-3 Bartek")
        ), dormitorySpecification.dormitoryId());
    }

    @Test
    void shouldBuildSpecificationWithLaundryRoomCalculators() {
        BartekSpecificationFactory factory = new BartekSpecificationFactory();
        SlotsRoomSpecification rooms = factory.create().slotsRoomSpecification();

        assertEquals(new BartekLaundryRoomFactory().create(), rooms);
    }
}