package pl.grupanet.eakademik.register;

import org.commons.DormitoryId;
import org.commons.Name;
import org.commons.RoomNumber;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.authentication.AuthService;
import pl.grupanet.eakademik.authentication.Email;
import pl.grupanet.eakademik.authentication.Password;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.user.UserProfileRepository;
import pl.grupanet.eakademik.user.dto.ResidentDto;
import pl.grupanet.eakademik.user.dto.StaffDto;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserRegistrationServiceTest {

    private UserProfileRepository userProfileRepository;
    private AuthService authService;
    private AppUserRegistrationService userRegistrationService;

    @BeforeEach
    void setUp() {
        userProfileRepository = mock(UserProfileRepository.class);
        authService = mock(AuthService.class);
        userRegistrationService = new AppUserRegistrationService(userProfileRepository, authService);
    }

    @Test
    void shouldRegisterResidentInAuthAndUserProfileWithSameId() {
        Email email = new Email("jan@example.com");
        Password password = new Password("secret123");
        Name name = new Name("Jan");
        Name surname = new Name("Kowalski");
        DormitoryId dormitoryId = new DormitoryId(UniversityNames.POLITECHNIKA_KRAKOWSKA, new Name("Ds-3 Bartek"));
        RoomNumber roomNumber = new RoomNumber("204B");
        ResidentId generatedId = new ResidentId(UUID.randomUUID());

        when(authService.registerResident(email, password)).thenReturn(generatedId);

        userRegistrationService.registerResident(email, password, name, surname, dormitoryId, roomNumber);

        verify(authService).registerResident(email, password);
        verify(userProfileRepository).registerResident(new ResidentDto(generatedId, name, surname, dormitoryId, roomNumber));
    }

    @Test
    void shouldRegisterStaffInAuthAndUserProfileWithSameId() {
        Email email = new Email("anna@example.com");
        Password password = new Password("secret123");
        Name name = new Name("Anna");
        Name surname = new Name("Nowak");
        DormitoryId dormitoryId = new DormitoryId(UniversityNames.POLITECHNIKA_KRAKOWSKA, new Name("Ds-3 Bartek"));
        StaffId generatedId = new StaffId(UUID.randomUUID());

        when(authService.registerStaff(email, password)).thenReturn(generatedId);

        userRegistrationService.registerStaff(email, password, name, surname, dormitoryId);

        verify(authService).registerStaff(email, password);
        verify(userProfileRepository).registerStaff(new StaffDto(generatedId, name, surname, dormitoryId));
    }

    @Test
    void shouldNotCreateResidentProfileWhenAuthRegistrationFails() {
        Email email = new Email("duplicate@example.com");
        Password password = new Password("secret123");
        Name name = new Name("Jan");
        Name surname = new Name("Kowalski");
        DormitoryId dormitoryId = new DormitoryId(UniversityNames.POLITECHNIKA_KRAKOWSKA, new Name("Ds-3 Bartek"));
        RoomNumber roomNumber = new RoomNumber("204B");

        when(authService.registerResident(email, password)).thenThrow(new IllegalArgumentException("Duplicate email"));

        assertThrows(
                IllegalArgumentException.class,
                () -> userRegistrationService.registerResident(email, password, name, surname, dormitoryId, roomNumber)
        );
        verify(userProfileRepository, never()).registerResident(any());
    }

    @Test
    void shouldNotCreateStaffProfileWhenAuthRegistrationFails() {
        Email email = new Email("duplicate@staff.example.com");
        Password password = new Password("secret123");
        Name name = new Name("Anna");
        Name surname = new Name("Nowak");
        DormitoryId dormitoryId = new DormitoryId(UniversityNames.POLITECHNIKA_KRAKOWSKA, new Name("Ds-3 Bartek"));

        when(authService.registerStaff(email, password)).thenThrow(new IllegalArgumentException("Duplicate email"));

        assertThrows(
                IllegalArgumentException.class,
                () -> userRegistrationService.registerStaff(email, password, name, surname, dormitoryId)
        );
        verify(userProfileRepository, never()).registerStaff(any());
    }

    @Test
    void shouldPropagateExceptionWhenResidentProfileCreationFails() {
        Email email = new Email("jan@example.com");
        Password password = new Password("secret123");
        Name name = new Name("Jan");
        Name surname = new Name("Kowalski");
        DormitoryId dormitoryId = new DormitoryId(UniversityNames.POLITECHNIKA_KRAKOWSKA, new Name("Nieistniejacy Akademik"));
        RoomNumber roomNumber = new RoomNumber("204B");
        ResidentId generatedId = new ResidentId(UUID.randomUUID());

        when(authService.registerResident(email, password)).thenReturn(generatedId);
        when(userProfileRepository.registerResident(new ResidentDto(generatedId, name, surname, dormitoryId, roomNumber)))
                .thenThrow(new IllegalArgumentException("Dormitory does not exist"));

        assertThrows(
                IllegalArgumentException.class,
                () -> userRegistrationService.registerResident(email, password, name, surname, dormitoryId, roomNumber)
        );
    }

    @Test
    void shouldPropagateExceptionWhenStaffProfileCreationFails() {
        Email email = new Email("anna@example.com");
        Password password = new Password("secret123");
        Name name = new Name("Anna");
        Name surname = new Name("Nowak");
        DormitoryId dormitoryId = new DormitoryId(UniversityNames.POLITECHNIKA_KRAKOWSKA, new Name("Nieistniejacy Akademik"));
        StaffId generatedId = new StaffId(UUID.randomUUID());

        when(authService.registerStaff(email, password)).thenReturn(generatedId);
        when(userProfileRepository.registerStaff(new StaffDto(generatedId, name, surname, dormitoryId)))
                .thenThrow(new IllegalArgumentException("Dormitory does not exist"));

        assertThrows(
                IllegalArgumentException.class,
                () -> userRegistrationService.registerStaff(email, password, name, surname, dormitoryId)
        );
    }
}
