package pl.grupanet.eakademik.dormitory.specifications.pk.bartek;

import org.commons.Name;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.slots.ChooseSlotsCalculator;
import pl.grupanet.eakademik.dormitory.slots.SlotsRoomSpecification;
import pl.grupanet.eakademik.dormitory.spec.pk.bartek.BartekCalculator;
import pl.grupanet.eakademik.dormitory.spec.pk.bartek.BartekLaundryRoomFactory;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BartekLaundryRoomFactoryTest {

    @Test
    void shouldAssignBartekCalculatorToAllLaundryRooms() {
        BartekLaundryRoomFactory factory = new BartekLaundryRoomFactory();
        SlotsRoomSpecification rooms = factory.create();

        List<String> expectedRooms = List.of("211", "411", "611", "811", "1011");

        for (String roomNumber : expectedRooms) {
            ChooseSlotsCalculator chooser = rooms.getCalculatorByRoomName(new Name(roomNumber));
            assertNotNull(chooser);
            assertInstanceOf(BartekCalculator.class, chooser.choose(LocalDateTime.now()));
        }
    }
}