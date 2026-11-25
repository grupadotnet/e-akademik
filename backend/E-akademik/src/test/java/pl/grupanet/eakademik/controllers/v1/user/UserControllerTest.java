package pl.grupanet.eakademik.controllers.v1.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.commons.DormitoryId;
import org.commons.Name;
import org.commons.RoomNumber;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.grupanet.eakademik.controllers.ApiVersions;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.user.UserProfileRepository;
import pl.grupanet.eakademik.user.dto.ResidentDto;
import pl.grupanet.eakademik.user.dto.StaffDto;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {
    private static final String API_URL = ApiVersions.V1.getPath() + "user";
    private static final DormitoryId EXISTING_DORMITORY =
            new DormitoryId(UniversityNames.POLITECHNIKA_KRAKOWSKA, new Name("Ds-3 Bartek"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Test
    @WithMockUser(roles = "STAFF")
    void shouldUpdateResidentWhenStaffIsAuthenticated() throws Exception {
        UUID uuid = UUID.randomUUID();
        ResidentId residentId = new ResidentId(uuid);
        userProfileRepository.registerResident(new ResidentDto(
                residentId,
                new Name("Jan"),
                new Name("Kowalski"),
                EXISTING_DORMITORY,
                new RoomNumber("101A")
        ));

        UpdateResidentRequest request = new UpdateResidentRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA,
                "Ds-3 Bartek",
                "204B",
                "Janusz",
                "Nowak"
        );

        mockMvc.perform(put(API_URL + "/resident/{id}", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(uuid.toString()))
                .andExpect(jsonPath("$.name").value("Janusz"))
                .andExpect(jsonPath("$.surname").value("Nowak"))
                .andExpect(jsonPath("$.universityName").value("POLITECHNIKA_KRAKOWSKA"))
                .andExpect(jsonPath("$.dormitoryName").value("Ds-3 Bartek"))
                .andExpect(jsonPath("$.roomNumber").value("204B"));

        ResidentDto persisted = userProfileRepository.findByResidentId(residentId);
        assertEquals(new Name("Janusz"), persisted.name());
        assertEquals(new Name("Nowak"), persisted.surname());
        assertEquals(new RoomNumber("204B"), persisted.roomNumber());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateResidentWhenAdminIsAuthenticated() throws Exception {
        UUID uuid = UUID.randomUUID();
        userProfileRepository.registerResident(new ResidentDto(
                new ResidentId(uuid),
                new Name("Jan"),
                new Name("Kowalski"),
                EXISTING_DORMITORY,
                new RoomNumber("101A")
        ));

        UpdateResidentRequest request = new UpdateResidentRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA,
                "Ds-3 Bartek",
                "308B",
                "Jan",
                "Kowalski"
        );

        mockMvc.perform(put(API_URL + "/resident/{id}", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomNumber").value("308B"));
    }

    @Test
    @WithMockUser(roles = "RESIDENT")
    void shouldForbidResidentUpdateForResidentRole() throws Exception {
        UpdateResidentRequest request = new UpdateResidentRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA,
                "Ds-3 Bartek",
                "204B",
                "Jan",
                "Kowalski"
        );

        mockMvc.perform(put(API_URL + "/resident/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldForbidResidentUpdateForAnonymousUser() throws Exception {
        UpdateResidentRequest request = new UpdateResidentRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA,
                "Ds-3 Bartek",
                "204B",
                "Jan",
                "Kowalski"
        );

        mockMvc.perform(put(API_URL + "/resident/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void shouldReturnBadRequestWhenUpdatingNonExistentResident() throws Exception {
        UpdateResidentRequest request = new UpdateResidentRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA,
                "Ds-3 Bartek",
                "204B",
                "Jan",
                "Kowalski"
        );

        mockMvc.perform(put(API_URL + "/resident/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void shouldReturnBadRequestWhenUpdatingResidentWithNonExistentDormitory() throws Exception {
        UUID uuid = UUID.randomUUID();
        userProfileRepository.registerResident(new ResidentDto(
                new ResidentId(uuid),
                new Name("Jan"),
                new Name("Kowalski"),
                EXISTING_DORMITORY,
                new RoomNumber("101A")
        ));

        UpdateResidentRequest request = new UpdateResidentRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA,
                "Nieistniejacy Akademik",
                "204B",
                "Jan",
                "Kowalski"
        );

        mockMvc.perform(put(API_URL + "/resident/{id}", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateStaffWhenAdminIsAuthenticated() throws Exception {
        UUID uuid = UUID.randomUUID();
        StaffId staffId = new StaffId(uuid);
        userProfileRepository.registerStaff(new StaffDto(
                staffId,
                new Name("Anna"),
                new Name("Nowak"),
                EXISTING_DORMITORY
        ));

        UpdateStaffRequest request = new UpdateStaffRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA,
                "Ds-3 Bartek",
                "Anna",
                "Kowalska"
        );

        mockMvc.perform(put(API_URL + "/staff/{id}", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(uuid.toString()))
                .andExpect(jsonPath("$.name").value("Anna"))
                .andExpect(jsonPath("$.surname").value("Kowalska"))
                .andExpect(jsonPath("$.universityName").value("POLITECHNIKA_KRAKOWSKA"))
                .andExpect(jsonPath("$.dormitoryName").value("Ds-3 Bartek"));

        StaffDto persisted = userProfileRepository.findByStaffId(staffId);
        assertEquals(new Name("Kowalska"), persisted.surname());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void shouldForbidStaffUpdateForStaffRole() throws Exception {
        UpdateStaffRequest request = new UpdateStaffRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA,
                "Ds-3 Bartek",
                "Anna",
                "Nowak"
        );

        mockMvc.perform(put(API_URL + "/staff/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "RESIDENT")
    void shouldForbidStaffUpdateForResidentRole() throws Exception {
        UpdateStaffRequest request = new UpdateStaffRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA,
                "Ds-3 Bartek",
                "Anna",
                "Nowak"
        );

        mockMvc.perform(put(API_URL + "/staff/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenUpdatingNonExistentStaff() throws Exception {
        UpdateStaffRequest request = new UpdateStaffRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA,
                "Ds-3 Bartek",
                "Anna",
                "Nowak"
        );

        mockMvc.perform(put(API_URL + "/staff/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "RESIDENT")
    void shouldGetResidentByIdWhenAuthenticated() throws Exception {
        UUID uuid = UUID.randomUUID();
        userProfileRepository.registerResident(new ResidentDto(
                new ResidentId(uuid),
                new Name("Jan"),
                new Name("Kowalski"),
                EXISTING_DORMITORY,
                new RoomNumber("204B")
        ));

        mockMvc.perform(get(API_URL + "/resident/{id}", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(uuid.toString()))
                .andExpect(jsonPath("$.name").value("Jan"))
                .andExpect(jsonPath("$.surname").value("Kowalski"))
                .andExpect(jsonPath("$.universityName").value("POLITECHNIKA_KRAKOWSKA"))
                .andExpect(jsonPath("$.dormitoryName").value("Ds-3 Bartek"))
                .andExpect(jsonPath("$.roomNumber").value("204B"));
    }

    @Test
    void shouldForbidGettingResidentWhenUnauthenticated() throws Exception {
        mockMvc.perform(get(API_URL + "/resident/{id}", UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "RESIDENT")
    void shouldReturnBadRequestWhenResidentNotFound() throws Exception {
        mockMvc.perform(get(API_URL + "/resident/{id}", UUID.randomUUID()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void shouldGetStaffByIdWhenAuthenticated() throws Exception {
        UUID uuid = UUID.randomUUID();
        userProfileRepository.registerStaff(new StaffDto(
                new StaffId(uuid),
                new Name("Anna"),
                new Name("Nowak"),
                EXISTING_DORMITORY
        ));

        mockMvc.perform(get(API_URL + "/staff/{id}", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(uuid.toString()))
                .andExpect(jsonPath("$.name").value("Anna"))
                .andExpect(jsonPath("$.surname").value("Nowak"))
                .andExpect(jsonPath("$.universityName").value("POLITECHNIKA_KRAKOWSKA"))
                .andExpect(jsonPath("$.dormitoryName").value("Ds-3 Bartek"));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void shouldReturnBadRequestWhenStaffNotFound() throws Exception {
        mockMvc.perform(get(API_URL + "/staff/{id}", UUID.randomUUID()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "RESIDENT")
    void shouldGetMultipleResidentsByIdsAndOmitMissingOnes() throws Exception {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID missingUuid = UUID.randomUUID();

        userProfileRepository.registerResident(new ResidentDto(
                new ResidentId(uuid1), new Name("Jan"), new Name("Kowalski"), EXISTING_DORMITORY, new RoomNumber("101A")
        ));
        userProfileRepository.registerResident(new ResidentDto(
                new ResidentId(uuid2), new Name("Adam"), new Name("Nowak"), EXISTING_DORMITORY, new RoomNumber("102B")
        ));

        mockMvc.perform(get(API_URL + "/residents")
                        .param("ids", uuid1.toString(), uuid2.toString(), missingUuid.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['" + uuid1 + "'].name").value("Jan"))
                .andExpect(jsonPath("$['" + uuid1 + "'].roomNumber").value("101A"))
                .andExpect(jsonPath("$['" + uuid2 + "'].name").value("Adam"))
                .andExpect(jsonPath("$['" + uuid2 + "'].roomNumber").value("102B"))
                .andExpect(jsonPath("$['" + missingUuid + "']").doesNotExist());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void shouldGetMultipleStaffByIdsAndOmitMissingOnes() throws Exception {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID missingUuid = UUID.randomUUID();

        userProfileRepository.registerStaff(new StaffDto(
                new StaffId(uuid1), new Name("Anna"), new Name("Nowak"), EXISTING_DORMITORY
        ));
        userProfileRepository.registerStaff(new StaffDto(
                new StaffId(uuid2), new Name("Piotr"), new Name("Wisniewski"), EXISTING_DORMITORY
        ));

        mockMvc.perform(get(API_URL + "/staff")
                        .param("ids", uuid1.toString(), uuid2.toString(), missingUuid.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['" + uuid1 + "'].name").value("Anna"))
                .andExpect(jsonPath("$['" + uuid2 + "'].name").value("Piotr"))
                .andExpect(jsonPath("$['" + missingUuid + "']").doesNotExist());
    }
}
