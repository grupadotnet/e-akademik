package pl.grupanet.eakademik.dormitory.specifications.pk.bartek;

import org.commons.Name;
import org.commons.RoomNumber;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.slots.ChooseSlotsCalculator;
import pl.grupanet.eakademik.dormitory.slots.SlotsRoomSpecification;
import pl.grupanet.eakademik.dormitory.spec.pk.bartek.BartekCalculator;
import pl.grupanet.eakademik.dormitory.spec.pk.bartek.BartekLaundryRoomFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BartekLaundryRoomFactoryTest {

    @Test
    void shouldAssignBartekCalculatorToAllLaundryRooms() {
        BartekLaundryRoomFactory factory = new BartekLaundryRoomFactory();
        SlotsRoomSpecification rooms = factory.create();

        ChooseSlotsCalculator expectedChooser = new ChooseSlotsCalculator.Builder(new BartekCalculator()).build();
        List<String> expectedRooms = List.of("211", "411", "611", "811", "1011");

        SlotsRoomSpecification.Builder expectedSpecificationBuilder = new SlotsRoomSpecification.Builder();
        for (String roomNumber : expectedRooms) {
            ChooseSlotsCalculator chooser = rooms.getCalculatorByRoomName(new RoomNumber(roomNumber));
            assertNotNull(chooser);
            assertEquals(expectedChooser, chooser);
            expectedSpecificationBuilder.add(roomNumber, expectedChooser);
        }

        assertEquals(expectedSpecificationBuilder.build(), rooms);
    }
}