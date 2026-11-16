package pl.grupanet.eakademik.dormitory.specifications.pk.bartek;

import org.commons.DormitoryId;
import org.commons.Name;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.DormitorySpecification;
import pl.grupanet.eakademik.dormitory.slots.SlotsRoomSpecification;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.dormitory.spec.pk.bartek.BartekSpecificationFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BartekSpecificationTest {

    @Test
    void shouldBuildSpecification() {
        BartekSpecificationFactory factory = new BartekSpecificationFactory();
        DormitorySpecification dormitorySpecification = factory.create();

        assertEquals(new DormitoryId(
                new Name(UniversityNames.POLITECHNIKA_KRAKOWSKA.getUniversityName()),
                new Name("Ds-3 Bartek")
        ), dormitorySpecification.dormitoryId());
    }

    @Test
    void shouldBuildSpecificationWithLaundryRoomCalculators() {
        BartekSpecificationFactory factory = new BartekSpecificationFactory();
        SlotsRoomSpecification rooms = factory.create().slotsRoomSpecification();

        assertNotNull(rooms);
        List<String> expectedRooms = List.of("211", "411", "611", "811", "1011");

        for (String roomNumber : expectedRooms) {
            assertNotNull(rooms.getCalculatorByRoomName(new Name(roomNumber)));
        }
    }
}