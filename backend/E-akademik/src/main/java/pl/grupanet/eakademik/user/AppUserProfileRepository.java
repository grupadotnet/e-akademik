package pl.grupanet.eakademik.user;

import lombok.NonNull;
import org.commons.DormitoryId;
import org.commons.Name;
import org.commons.RoomNumber;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import org.commons.users.UserId;
import org.springframework.transaction.annotation.Transactional;
import pl.grupanet.eakademik.dormitory.repositories.DormitoryRepository;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.user.dto.ResidentDto;
import pl.grupanet.eakademik.user.dto.StaffDto;
import pl.grupanet.eakademik.user.entities.ResidentEntity;
import pl.grupanet.eakademik.user.entities.ResidentJpaRepository;
import pl.grupanet.eakademik.user.entities.StaffEntity;
import pl.grupanet.eakademik.user.entities.StaffJpaRepository;

import java.util.*;

class AppUserProfileRepository implements UserProfileRepository {
    private final StaffJpaRepository staffRepository;
    private final ResidentJpaRepository residentRepository;
    private final DormitoryRepository dormitoryRepository;

    AppUserProfileRepository(StaffJpaRepository staffRepository, ResidentJpaRepository residentRepository, DormitoryRepository dormitoryRepository) {
        this.staffRepository = staffRepository;
        this.residentRepository = residentRepository;
        this.dormitoryRepository = dormitoryRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public ResidentDto findByResidentId(@NonNull ResidentId residentId) {
        ResidentEntity resident = residentRepository.findById(residentId.getIdAsUUID()).orElseThrow(() -> new IllegalArgumentException("Resident not found"));
        return mapEntityToDto(resident);
    }

    @Transactional(readOnly = true)
    @Override
    public Map<ResidentId, ResidentDto> findByResidentIds(@NonNull List<ResidentId> residentIds) {
        if (residentIds.isEmpty()) return Map.of();

        List<UUID> ids = residentIds.stream().map(UserId::getIdAsUUID).toList();
        List<ResidentEntity> residents = residentRepository.findAllById(ids);
        Map<ResidentId, ResidentDto> map = new HashMap<>();
        for (ResidentEntity resident : residents) {
            map.putIfAbsent(new ResidentId(resident.getId()), mapEntityToDto(resident));
        }

        return map;
    }

    @Transactional(readOnly = true)
    @Override
    public StaffDto findByStaffId(@NonNull StaffId staffId) {
        StaffEntity staff = staffRepository.findById(staffId.getIdAsUUID()).orElseThrow(() -> new IllegalArgumentException("Staff not found"));
        return mapEntityToDto(staff);
    }

    @Transactional(readOnly = true)
    @Override
    public Map<StaffId, StaffDto> findByStaffIds(@NonNull List<StaffId> staffIds) {
        if (staffIds.isEmpty()) return Map.of();

        List<UUID> ids = staffIds.stream().map(UserId::getIdAsUUID).toList();
        List<StaffEntity> staff = staffRepository.findAllById(ids);
        Map<StaffId, StaffDto> map = new HashMap<>();
        for (StaffEntity oneStaff : staff) {
            map.putIfAbsent(new StaffId(oneStaff.getId()), mapEntityToDto(oneStaff));
        }

        return map;
    }

    @Transactional
    @Override
    public ResidentDto registerResident(@NonNull ResidentDto residentDto) {
        dormitoryRepository.get(residentDto.dormitoryId());
        if(residentRepository.existsById(residentDto.id().getIdAsUUID())) throw new IllegalArgumentException("Resident already exists");
        ResidentEntity createdResident = residentRepository.save(mapDtoToEntity(residentDto));
        return mapEntityToDto(createdResident);
    }

    @Transactional
    @Override
    public StaffDto registerStaff(@NonNull StaffDto staffDto) {
        dormitoryRepository.get(staffDto.dormitoryId());
        if(staffRepository.existsById(staffDto.id().getIdAsUUID())) throw new IllegalArgumentException("Staff already exists");
        StaffEntity createdStaff = staffRepository.save(mapDtoToEntity(staffDto));
        return mapEntityToDto(createdStaff);
    }

    @Transactional
    @Override
    public ResidentDto updateResident(@NonNull ResidentDto residentDto) {
        dormitoryRepository.get(residentDto.dormitoryId());
        if(!residentRepository.existsById(residentDto.id().getIdAsUUID())) throw new IllegalArgumentException("Resident not found");
        ResidentEntity updatedResident = residentRepository.save(mapDtoToEntity(residentDto));
        return mapEntityToDto(updatedResident);
    }

    @Transactional
    @Override
    public StaffDto updateStaff(@NonNull StaffDto staffDto) {
        dormitoryRepository.get(staffDto.dormitoryId());
        if(!staffRepository.existsById(staffDto.id().getIdAsUUID())) throw new IllegalArgumentException("Staff not found");
        StaffEntity updatedStaff = staffRepository.save(mapDtoToEntity(staffDto));
        return mapEntityToDto(updatedStaff);
    }

    private ResidentDto mapEntityToDto(ResidentEntity residentEntity) {
        UniversityNames universityName = residentEntity.getUniversityName();
        Name dormitoryName = new Name(residentEntity.getDormitoryName());

        return new ResidentDto(
                new ResidentId(residentEntity.getId()),
                new Name(residentEntity.getName()),
                new Name(residentEntity.getSurname()),
                new DormitoryId(universityName, dormitoryName),
                new RoomNumber(residentEntity.getRoomNumber())
        );
    }

    private ResidentEntity mapDtoToEntity(ResidentDto residentDto) {
        ResidentEntity resident = new ResidentEntity();
        resident.setId(residentDto.id().getIdAsUUID());
        resident.setName(residentDto.name().toString());
        resident.setSurname(residentDto.surname().toString());
        resident.setUniversityName(residentDto.dormitoryId().getUniversityName());
        resident.setDormitoryName(residentDto.dormitoryId().getDormitoryName().toString());
        resident.setRoomNumber(residentDto.roomNumber().toString());

        return resident;
    }

    private StaffDto mapEntityToDto(StaffEntity staffEntity) {
        UniversityNames universityName = staffEntity.getUniversityName();
        Name dormitoryName = new Name(staffEntity.getDormitoryName());

        return new StaffDto(
                new StaffId(staffEntity.getId()),
                new Name(staffEntity.getName()),
                new Name(staffEntity.getSurname()),
                new DormitoryId(universityName, dormitoryName)
        );
    }

    private StaffEntity mapDtoToEntity(StaffDto staffDto) {
        StaffEntity staff =  new StaffEntity();
        staff.setId(staffDto.id().getIdAsUUID());
        staff.setName(staffDto.name().toString());
        staff.setSurname(staffDto.surname().toString());
        staff.setUniversityName(staffDto.dormitoryId().getUniversityName());
        staff.setDormitoryName(staffDto.dormitoryId().getDormitoryName().toString());

        return  staff;
    }
}
