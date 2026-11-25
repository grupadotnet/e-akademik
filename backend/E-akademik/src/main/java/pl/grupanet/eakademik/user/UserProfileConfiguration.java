package pl.grupanet.eakademik.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.grupanet.eakademik.dormitory.repositories.DormitoryRepository;
import pl.grupanet.eakademik.user.entities.ResidentJpaRepository;
import pl.grupanet.eakademik.user.entities.StaffJpaRepository;

@Configuration
public class UserProfileConfiguration {

    private final ResidentJpaRepository residentJpaRepository;
    private final StaffJpaRepository staffJpaRepository;
    private final DormitoryRepository dormitoryRepository;

    public UserProfileConfiguration(ResidentJpaRepository residentJpaRepository, StaffJpaRepository staffJpaRepository, DormitoryRepository dormitoryRepository) {
        this.residentJpaRepository = residentJpaRepository;
        this.staffJpaRepository = staffJpaRepository;
        this.dormitoryRepository = dormitoryRepository;
    }


    @Bean
    public UserProfileRepository userProfileRepository() {
        return new AppUserProfileRepository(staffJpaRepository, residentJpaRepository, dormitoryRepository);
    }
}
