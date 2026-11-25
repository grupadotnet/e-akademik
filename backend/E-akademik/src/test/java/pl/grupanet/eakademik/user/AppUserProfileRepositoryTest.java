package pl.grupanet.eakademik.user;

import org.commons.DormitoryId;
import org.commons.Name;
import org.commons.RoomNumber;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import pl.grupanet.eakademik.dormitory.DormitorySpecification;
import pl.grupanet.eakademik.dormitory.repositories.DormitoryRepository;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.user.dto.ResidentDto;
import pl.grupanet.eakademik.user.dto.StaffDto;
import pl.grupanet.eakademik.user.entities.ResidentEntity;
import pl.grupanet.eakademik.user.entities.ResidentJpaRepository;
import pl.grupanet.eakademik.user.entities.StaffEntity;
import pl.grupanet.eakademik.user.entities.StaffJpaRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AppUserProfileRepositoryTest {

    private StaffJpaRepository staffRepository;
    private ResidentJpaRepository residentRepository;
    private DormitoryRepository dormitoryRepository;
    private AppUserProfileRepository userProfileRepository;

    private DormitoryId dormitoryId;

    @BeforeEach
    void setUp() {
        staffRepository = mock(StaffJpaRepository.class);
        residentRepository = mock(ResidentJpaRepository.class);
        dormitoryRepository = mock(DormitoryRepository.class);
        userProfileRepository = new AppUserProfileRepository(staffRepository, residentRepository, dormitoryRepository);
        dormitoryId = new DormitoryId.Builder(UniversityNames.values()[0], "Ds-3 Bartek").build();
    }

    @Test
    void shouldFindResidentById() {
        UUID uuid = UUID.randomUUID();
        ResidentId residentId = new ResidentId(uuid);
        ResidentEntity entity = createResidentEntity(uuid, "Jan", "Kowalski", "204B");

        when(residentRepository.findById(uuid)).thenReturn(Optional.of(entity));

        ResidentDto result = userProfileRepository.findByResidentId(residentId);

        assertEquals(residentId, result.id());
        assertEquals(new Name("Jan"), result.name());
        assertEquals(new Name("Kowalski"), result.surname());
        assertEquals(dormitoryId, result.dormitoryId());
        assertEquals(new RoomNumber("204B"), result.roomNumber());
    }

    @Test
    void shouldThrowExceptionWhenResidentNotFoundById() {
        UUID uuid = UUID.randomUUID();
        ResidentId residentId = new ResidentId(uuid);
        when(residentRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userProfileRepository.findByResidentId(residentId));
    }

    @Test
    void shouldReturnEmptyMapWhenResidentIdsListIsEmpty() {
        Map<ResidentId, ResidentDto> result = userProfileRepository.findByResidentIds(List.of());

        assertTrue(result.isEmpty());
        verifyNoInteractions(residentRepository);
    }

    @Test
    void shouldFindResidentsByIdsAndReturnMap() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        ResidentId id1 = new ResidentId(uuid1);
        ResidentId id2 = new ResidentId(uuid2);
        ResidentEntity entity1 = createResidentEntity(uuid1, "Jan", "Kowalski", "101A");
        ResidentEntity entity2 = createResidentEntity(uuid2, "Adam", "Nowak", "102B");

        when(residentRepository.findAllById(List.of(uuid1, uuid2))).thenReturn(List.of(entity1, entity2));

        Map<ResidentId, ResidentDto> result = userProfileRepository.findByResidentIds(List.of(id1, id2));

        assertEquals(2, result.size());
        assertEquals(new RoomNumber("101A"), result.get(id1).roomNumber());
        assertEquals(new RoomNumber("102B"), result.get(id2).roomNumber());
    }

    @Test
    void shouldFindStaffById() {
        UUID uuid = UUID.randomUUID();
        StaffId staffId = new StaffId(uuid);
        StaffEntity entity = createStaffEntity(uuid, "Anna", "Nowak");

        when(staffRepository.findById(uuid)).thenReturn(Optional.of(entity));

        StaffDto result = userProfileRepository.findByStaffId(staffId);

        assertEquals(staffId, result.id());
        assertEquals(new Name("Anna"), result.name());
        assertEquals(new Name("Nowak"), result.surname());
        assertEquals(dormitoryId, result.dormitoryId());
    }

    @Test
    void shouldThrowExceptionWhenStaffNotFoundById() {
        UUID uuid = UUID.randomUUID();
        StaffId staffId = new StaffId(uuid);
        when(staffRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userProfileRepository.findByStaffId(staffId));
    }

    @Test
    void shouldReturnEmptyMapWhenStaffIdsListIsEmpty() {
        Map<StaffId, StaffDto> result = userProfileRepository.findByStaffIds(List.of());

        assertTrue(result.isEmpty());
        verifyNoInteractions(staffRepository);
    }

    @Test
    void shouldFindStaffByIdsAndReturnMap() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        StaffId id1 = new StaffId(uuid1);
        StaffId id2 = new StaffId(uuid2);
        StaffEntity entity1 = createStaffEntity(uuid1, "Anna", "Nowak");
        StaffEntity entity2 = createStaffEntity(uuid2, "Piotr", "Wisniewski");

        when(staffRepository.findAllById(List.of(uuid1, uuid2))).thenReturn(List.of(entity1, entity2));

        Map<StaffId, StaffDto> result = userProfileRepository.findByStaffIds(List.of(id1, id2));

        assertEquals(2, result.size());
        assertEquals(new Name("Anna"), result.get(id1).name());
        assertEquals(new Name("Piotr"), result.get(id2).name());
    }

    @Test
    void shouldRegisterResidentWhenDormitoryExistsAndResidentDoesNotExist() {
        UUID uuid = UUID.randomUUID();
        ResidentDto dto = new ResidentDto(
                new ResidentId(uuid),
                new Name("Jan"),
                new Name("Kowalski"),
                dormitoryId,
                new RoomNumber("204b")
        );
        ResidentEntity savedEntity = createResidentEntity(uuid, "Jan", "Kowalski", "204B");

        when(dormitoryRepository.get(dormitoryId)).thenReturn(mock(DormitorySpecification.class));
        when(residentRepository.existsById(uuid)).thenReturn(false);
        when(residentRepository.save(any(ResidentEntity.class))).thenReturn(savedEntity);

        ResidentDto result = userProfileRepository.registerResident(dto);

        ArgumentCaptor<ResidentEntity> captor = ArgumentCaptor.forClass(ResidentEntity.class);
        verify(residentRepository).save(captor.capture());

        ResidentEntity persisted = captor.getValue();
        assertEquals(uuid, persisted.getId());
        assertEquals("Jan", persisted.getName());
        assertEquals("Kowalski", persisted.getSurname());
        assertEquals(dormitoryId.getUniversityName(), persisted.getUniversityName());
        assertEquals("Ds-3 Bartek", persisted.getDormitoryName());
        assertEquals("204B", persisted.getRoomNumber());
        assertEquals(dto, result);
    }

    @Test
    void shouldThrowExceptionWhenRegisteringResidentWithNonExistentDormitory() {
        ResidentDto dto = new ResidentDto(
                new ResidentId(UUID.randomUUID()),
                new Name("Jan"),
                new Name("Kowalski"),
                dormitoryId,
                new RoomNumber("204B")
        );
        when(dormitoryRepository.get(dormitoryId)).thenThrow(new IllegalArgumentException("Dormitory does not exist"));

        assertThrows(IllegalArgumentException.class, () -> userProfileRepository.registerResident(dto));
        verify(residentRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenRegisteringExistingResident() {
        UUID uuid = UUID.randomUUID();
        ResidentDto dto = new ResidentDto(
                new ResidentId(uuid),
                new Name("Jan"),
                new Name("Kowalski"),
                dormitoryId,
                new RoomNumber("204B")
        );
        when(dormitoryRepository.get(dormitoryId)).thenReturn(mock(DormitorySpecification.class));
        when(residentRepository.existsById(uuid)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userProfileRepository.registerResident(dto));
        verify(residentRepository, never()).save(any());
    }

    @Test
    void shouldRegisterStaffWhenDormitoryExistsAndStaffDoesNotExist() {
        UUID uuid = UUID.randomUUID();
        StaffDto dto = new StaffDto(
                new StaffId(uuid),
                new Name("Anna"),
                new Name("Nowak"),
                dormitoryId
        );
        StaffEntity savedEntity = createStaffEntity(uuid, "Anna", "Nowak");

        when(dormitoryRepository.get(dormitoryId)).thenReturn(mock(DormitorySpecification.class));
        when(staffRepository.existsById(uuid)).thenReturn(false);
        when(staffRepository.save(any(StaffEntity.class))).thenReturn(savedEntity);

        StaffDto result = userProfileRepository.registerStaff(dto);

        ArgumentCaptor<StaffEntity> captor = ArgumentCaptor.forClass(StaffEntity.class);
        verify(staffRepository).save(captor.capture());

        StaffEntity persisted = captor.getValue();
        assertEquals(uuid, persisted.getId());
        assertEquals("Anna", persisted.getName());
        assertEquals("Nowak", persisted.getSurname());
        assertEquals(dormitoryId.getUniversityName(), persisted.getUniversityName());
        assertEquals("Ds-3 Bartek", persisted.getDormitoryName());
        assertEquals(dto, result);
    }

    @Test
    void shouldThrowExceptionWhenRegisteringStaffWithNonExistentDormitory() {
        StaffDto dto = new StaffDto(
                new StaffId(UUID.randomUUID()),
                new Name("Anna"),
                new Name("Nowak"),
                dormitoryId
        );
        when(dormitoryRepository.get(dormitoryId)).thenThrow(new IllegalArgumentException("Dormitory does not exist"));

        assertThrows(IllegalArgumentException.class, () -> userProfileRepository.registerStaff(dto));
        verify(staffRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenRegisteringExistingStaff() {
        UUID uuid = UUID.randomUUID();
        StaffDto dto = new StaffDto(
                new StaffId(uuid),
                new Name("Anna"),
                new Name("Nowak"),
                dormitoryId
        );
        when(dormitoryRepository.get(dormitoryId)).thenReturn(mock(DormitorySpecification.class));
        when(staffRepository.existsById(uuid)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userProfileRepository.registerStaff(dto));
        verify(staffRepository, never()).save(any());
    }

    @Test
    void shouldUpdateExistingResidentWhenDormitoryExists() {
        UUID uuid = UUID.randomUUID();
        ResidentDto dto = new ResidentDto(
                new ResidentId(uuid),
                new Name("Jan"),
                new Name("Nowak"),
                dormitoryId,
                new RoomNumber("308B")
        );
        ResidentEntity updatedEntity = createResidentEntity(uuid, "Jan", "Nowak", "308B");

        when(dormitoryRepository.get(dormitoryId)).thenReturn(mock(DormitorySpecification.class));
        when(residentRepository.existsById(uuid)).thenReturn(true);
        when(residentRepository.save(any(ResidentEntity.class))).thenReturn(updatedEntity);

        ResidentDto result = userProfileRepository.updateResident(dto);

        verify(residentRepository).save(any(ResidentEntity.class));
        assertEquals(dto, result);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingResidentWithNonExistentDormitory() {
        ResidentDto dto = new ResidentDto(
                new ResidentId(UUID.randomUUID()),
                new Name("Jan"),
                new Name("Nowak"),
                dormitoryId,
                new RoomNumber("308B")
        );
        when(dormitoryRepository.get(dormitoryId)).thenThrow(new IllegalArgumentException("Dormitory does not exist"));

        assertThrows(IllegalArgumentException.class, () -> userProfileRepository.updateResident(dto));
        verify(residentRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentResident() {
        UUID uuid = UUID.randomUUID();
        ResidentDto dto = new ResidentDto(
                new ResidentId(uuid),
                new Name("Jan"),
                new Name("Nowak"),
                dormitoryId,
                new RoomNumber("308B")
        );
        when(dormitoryRepository.get(dormitoryId)).thenReturn(mock(DormitorySpecification.class));
        when(residentRepository.existsById(uuid)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userProfileRepository.updateResident(dto));
        verify(residentRepository, never()).save(any());
    }

    @Test
    void shouldUpdateExistingStaffWhenDormitoryExists() {
        UUID uuid = UUID.randomUUID();
        StaffDto dto = new StaffDto(
                new StaffId(uuid),
                new Name("Anna"),
                new Name("Kowalska"),
                dormitoryId
        );
        StaffEntity updatedEntity = createStaffEntity(uuid, "Anna", "Kowalska");

        when(dormitoryRepository.get(dormitoryId)).thenReturn(mock(DormitorySpecification.class));
        when(staffRepository.existsById(uuid)).thenReturn(true);
        when(staffRepository.save(any(StaffEntity.class))).thenReturn(updatedEntity);

        StaffDto result = userProfileRepository.updateStaff(dto);

        verify(staffRepository).save(any(StaffEntity.class));
        assertEquals(dto, result);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingStaffWithNonExistentDormitory() {
        StaffDto dto = new StaffDto(
                new StaffId(UUID.randomUUID()),
                new Name("Anna"),
                new Name("Kowalska"),
                dormitoryId
        );
        when(dormitoryRepository.get(dormitoryId)).thenThrow(new IllegalArgumentException("Dormitory does not exist"));

        assertThrows(IllegalArgumentException.class, () -> userProfileRepository.updateStaff(dto));
        verify(staffRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentStaff() {
        UUID uuid = UUID.randomUUID();
        StaffDto dto = new StaffDto(
                new StaffId(uuid),
                new Name("Anna"),
                new Name("Kowalska"),
                dormitoryId
        );
        when(dormitoryRepository.get(dormitoryId)).thenReturn(mock(DormitorySpecification.class));
        when(staffRepository.existsById(uuid)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userProfileRepository.updateStaff(dto));
        verify(staffRepository, never()).save(any());
    }

    private ResidentEntity createResidentEntity(UUID id, String name, String surname, String roomNumber) {
        ResidentEntity entity = new ResidentEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setSurname(surname);
        entity.setUniversityName(dormitoryId.getUniversityName());
        entity.setDormitoryName(dormitoryId.getDormitoryName().toString());
        entity.setRoomNumber(roomNumber);
        return entity;
    }

    private StaffEntity createStaffEntity(UUID id, String name, String surname) {
        StaffEntity entity = new StaffEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setSurname(surname);
        entity.setUniversityName(dormitoryId.getUniversityName());
        entity.setDormitoryName(dormitoryId.getDormitoryName().toString());
        return entity;
    }
}
