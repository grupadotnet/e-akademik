package pl.grupanet.eakademik.dormitory.repositories;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpecificationConfiguration {
    @Bean
    public DormitoryRepository dormitoryRepository() {
        AppDormitoryRepository dormitoryRepository = new AppDormitoryRepository();
        SpecificationScanner specificationScanner = new SpecificationScanner(dormitoryRepository);
        specificationScanner.scanAndRegisterAll();

        return dormitoryRepository;
    }
}
