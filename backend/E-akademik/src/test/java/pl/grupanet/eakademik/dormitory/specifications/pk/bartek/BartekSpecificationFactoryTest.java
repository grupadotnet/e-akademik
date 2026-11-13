package pl.grupanet.eakademik.dormitory.spec.pk.bartek;

import org.commons.DormitoryId;
import org.commons.Name;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.DormitorySpecification;
import pl.grupanet.eakademik.dormitory.slots.ChooseSlotsCalculator;
import pl.grupanet.eakademik.dormitory.slots.SlotsRoomSpecification;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BartekSpecificationFactoryTest {

    @Test
    void shouldCreateBartekSpecificationWithCorrectRoomsAndSlots() {
        BartekSpecificationFactory factory = new BartekSpecificationFactory();
        DormitorySpecification specification = factory.create();

        DormitoryId expectedId = new DormitoryId(
                new Name(UniversityNames.POLITECHNIKA_KRAKOWSKA.getUniversityName()),
                new Name("Ds-3 Bartek")
        );
        assertEquals(expectedId, specification.dormitoryId());

        SlotsRoomSpecification rooms = specification.slotsRoomSpecification();
        List<String> expectedRooms = List.of("211", "411", "611", "811", "1011");

        for (String roomNumber : expectedRooms) {
            ChooseSlotsCalculator calculator = rooms.getCalculatorByRoomName(new Name(roomNumber));
            assertNotNull(calculator);
            assertEquals(8, calculator.choose(LocalDateTime.now()).generateSlotIds().size());
        }
    }
}