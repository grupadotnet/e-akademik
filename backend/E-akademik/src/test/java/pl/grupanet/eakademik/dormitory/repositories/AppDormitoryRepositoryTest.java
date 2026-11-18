package pl.grupanet.eakademik.dormitory.repositories;

import org.commons.DormitoryId;
import org.commons.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.DormitorySpecification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AppDormitoryRepositoryTest {

    private AppDormitoryRepository repository;
    private DormitoryId dormitoryId;
    private DormitorySpecification specification;

    @BeforeEach
    void setUp() {
        repository = new AppDormitoryRepository();
        dormitoryId = new DormitoryId(new Name("MIT"), new Name("Simmons"));

        specification = mock(DormitorySpecification.class);
        when(specification.dormitoryId()).thenReturn(dormitoryId);
    }

    @Test
    void shouldAddAndRetrieveDormitorySpecification() {
        repository.add(specification);

        DormitorySpecification result = repository.get(dormitoryId);

        assertEquals(specification, result);
        assertEquals(List.of(dormitoryId), repository.getAllId());
    }

    @Test
    void shouldThrowExceptionWhenAddingExistingDormitorySpecification() {
        repository.add(specification);

        assertThrows(IllegalArgumentException.class, () -> repository.add(specification));
    }

    @Test
    void shouldThrowExceptionWhenGettingNonExistentDormitorySpecification() {
        DormitoryId nonExistentId = new DormitoryId(new Name("Harvard"), new Name("Baker"));

        assertThrows(IllegalArgumentException.class, () -> repository.get(nonExistentId));
    }
}