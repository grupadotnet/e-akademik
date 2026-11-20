package pl.grupanet.eakademik.dormitory.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SpecificationConfigurationTest {
    @Autowired
    private DormitoryRepository dormitoryRepository;

    @Test
    void shouldSuccessfullyInjectRealRepository() {
        assertNotNull(dormitoryRepository);
        assertFalse(dormitoryRepository.getAllId().isEmpty());
    }
}