package pl.grupanet.eakademik.user;

import org.commons.DormitoryId;
import org.commons.Name;
import org.commons.RoomNumber;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.user.dto.ResidentDto;
import pl.grupanet.eakademik.user.dto.StaffDto;
import pl.grupanet.eakademik.user.entities.ResidentJpaRepository;
import pl.grupanet.eakademik.user.entities.StaffJpaRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Testcontainers
class UserProfileRepositoryE2ETest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private ResidentJpaRepository residentJpaRepository;

    @Autowired
    private StaffJpaRepository staffJpaRepository;

    @BeforeEach
    void cleanDatabase() {
        residentJpaRepository.deleteAll();
        staffJpaRepository.deleteAll();
    }

    @Test
    void shouldCompleteFullResidentAndStaffLifecycleAgainstPostgres() {
        DormitoryId existingDormitory = new DormitoryId.Builder(UniversityNames.values()[0], "Ds-3 Bartek").build();
        DormitoryId nonExistentDormitory = new DormitoryId.Builder(UniversityNames.values()[0], "Nieistniejacy Akademik").build();

        ResidentId residentId1 = new ResidentId(UUID.randomUUID());
        ResidentId residentId2 = new ResidentId(UUID.randomUUID());
        ResidentId missingResidentId = new ResidentId(UUID.randomUUID());

        ResidentDto resident1 = new ResidentDto(
                residentId1,
                new Name("Jan"),
                new Name("Kowalski"),
                existingDormitory,
                new RoomNumber("101A")
        );
        ResidentDto resident2 = new ResidentDto(
                residentId2,
                new Name("Adam"),
                new Name("Nowak"),
                existingDormitory,
                new RoomNumber("202B")
        );

        assertEquals(resident1, userProfileRepository.registerResident(resident1));
        assertEquals(resident2, userProfileRepository.registerResident(resident2));

        assertEquals(resident1, userProfileRepository.findByResidentId(residentId1));
        assertEquals(resident2, userProfileRepository.findByResidentId(residentId2));
        assertThrows(IllegalArgumentException.class, () -> userProfileRepository.findByResidentId(missingResidentId));

        assertTrue(userProfileRepository.findByResidentIds(List.of()).isEmpty());
        Map<ResidentId, ResidentDto> residentsMap = userProfileRepository.findByResidentIds(
                List.of(residentId1, residentId2, missingResidentId)
        );
        assertEquals(2, residentsMap.size());
        assertEquals(resident1, residentsMap.get(residentId1));
        assertEquals(resident2, residentsMap.get(residentId2));
        assertFalse(residentsMap.containsKey(missingResidentId));

        assertThrows(IllegalArgumentException.class, () -> userProfileRepository.registerResident(resident1));
        assertThrows(IllegalArgumentException.class, () -> userProfileRepository.registerResident(
                new ResidentDto(
                        new ResidentId(UUID.randomUUID()),
                        new Name("Piotr"),
                        new Name("Zielinski"),
                        nonExistentDormitory,
                        new RoomNumber("303C")
                )
        ));

        ResidentDto updatedResident1 = new ResidentDto(
                residentId1,
                new Name("Jan"),
                new Name("Nowak"),
                existingDormitory,
                new RoomNumber("308B")
        );
        assertEquals(updatedResident1, userProfileRepository.updateResident(updatedResident1));
        assertEquals(updatedResident1, userProfileRepository.findByResidentId(residentId1));

        assertThrows(IllegalArgumentException.class, () -> userProfileRepository.updateResident(
                new ResidentDto(
                        missingResidentId,
                        new Name("Ghost"),
                        new Name("User"),
                        existingDormitory,
                        new RoomNumber("100")
                )
        ));
        assertThrows(IllegalArgumentException.class, () -> userProfileRepository.updateResident(
                new ResidentDto(
                        residentId1,
                        new Name("Jan"),
                        new Name("Nowak"),
                        nonExistentDormitory,
                        new RoomNumber("308B")
                )
        ));

        StaffId staffId1 = new StaffId(UUID.randomUUID());
        StaffId staffId2 = new StaffId(UUID.randomUUID());
        StaffId missingStaffId = new StaffId(UUID.randomUUID());

        StaffDto staff1 = new StaffDto(staffId1, new Name("Anna"), new Name("Nowak"), existingDormitory);
        StaffDto staff2 = new StaffDto(staffId2, new Name("Piotr"), new Name("Wisniewski"), existingDormitory);

        assertEquals(staff1, userProfileRepository.registerStaff(staff1));
        assertEquals(staff2, userProfileRepository.registerStaff(staff2));

        assertEquals(staff1, userProfileRepository.findByStaffId(staffId1));
        assertEquals(staff2, userProfileRepository.findByStaffId(staffId2));
        assertThrows(IllegalArgumentException.class, () -> userProfileRepository.findByStaffId(missingStaffId));

        assertTrue(userProfileRepository.findByStaffIds(List.of()).isEmpty());
        Map<StaffId, StaffDto> staffMap = userProfileRepository.findByStaffIds(
                List.of(staffId1, staffId2, missingStaffId)
        );
        assertEquals(2, staffMap.size());
        assertEquals(staff1, staffMap.get(staffId1));
        assertEquals(staff2, staffMap.get(staffId2));
        assertFalse(staffMap.containsKey(missingStaffId));

        assertThrows(IllegalArgumentException.class, () -> userProfileRepository.registerStaff(staff1));
        assertThrows(IllegalArgumentException.class, () -> userProfileRepository.registerStaff(
                new StaffDto(
                        new StaffId(UUID.randomUUID()),
                        new Name("Marek"),
                        new Name("Lewandowski"),
                        nonExistentDormitory
                )
        ));

        StaffDto updatedStaff1 = new StaffDto(staffId1, new Name("Anna"), new Name("Kowalska"), existingDormitory);
        assertEquals(updatedStaff1, userProfileRepository.updateStaff(updatedStaff1));
        assertEquals(updatedStaff1, userProfileRepository.findByStaffId(staffId1));

        assertThrows(IllegalArgumentException.class, () -> userProfileRepository.updateStaff(
                new StaffDto(missingStaffId, new Name("Ghost"), new Name("Staff"), existingDormitory)
        ));
    }
}
