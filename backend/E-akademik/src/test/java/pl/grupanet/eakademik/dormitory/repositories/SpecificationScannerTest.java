package pl.grupanet.eakademik.dormitory.repositories;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.reflections.Reflections;
import pl.grupanet.eakademik.dormitory.DormitorySpecificationFactory;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SpecificationScannerTest {
    public static class ValidFactory extends DormitorySpecificationFactory {
        public ValidFactory() {
            super("Pk", "dor");
        }
    }

    public static class NoDefaultConstructorFactory extends DormitorySpecificationFactory {
        public NoDefaultConstructorFactory(String dummyParam) {
            super(dummyParam, dummyParam);
        }
    }

    public static class ThrowingConstructorFactory extends DormitorySpecificationFactory {
        public ThrowingConstructorFactory() {
            super("PK", "dor");
            throw new IllegalStateException("Simulated exception");
        }
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void shouldScanAndRegisterAllSpecifications() {
        AppDormitoryRepository repository = mock(AppDormitoryRepository.class);

        try (MockedConstruction<Reflections> mocked = mockConstruction(Reflections.class, (mock, context) -> {
            Set classes = new HashSet<>();
            classes.add(ValidFactory.class);
            when(mock.getSubTypesOf(DormitorySpecificationFactory.class)).thenReturn(classes);
        })) {
            SpecificationScanner scanner = new SpecificationScanner(repository);

            scanner.scanAndRegisterAll();

            verify(repository, times(1)).add(any());
        }
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void shouldNotRegisterWhenNoFactoriesFound() {
        AppDormitoryRepository repository = mock(AppDormitoryRepository.class);

        try (MockedConstruction<Reflections> mocked = mockConstruction(Reflections.class, (mock, context) -> {
            Set classes = new HashSet<>();
            when(mock.getSubTypesOf(DormitorySpecificationFactory.class)).thenReturn(classes);
        })) {
            SpecificationScanner scanner = new SpecificationScanner(repository);

            scanner.scanAndRegisterAll();

            verify(repository, never()).add(any());
        }
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void shouldThrowExceptionWhenFactoryLacksDefaultConstructor() {
        AppDormitoryRepository repository = mock(AppDormitoryRepository.class);

        try (MockedConstruction<Reflections> mocked = mockConstruction(Reflections.class, (mock, context) -> {
            Set classes = new HashSet<>();
            classes.add(NoDefaultConstructorFactory.class);
            when(mock.getSubTypesOf(DormitorySpecificationFactory.class)).thenReturn(classes);
        })) {
            SpecificationScanner scanner = new SpecificationScanner(repository);

            RuntimeException exception = assertThrows(RuntimeException.class, scanner::scanAndRegisterAll);

            assertTrue(exception.getMessage().contains("Failed to instantiate factory"));
            assertEquals(NoSuchMethodException.class, exception.getCause().getClass());

            verify(repository, never()).add(any());
        }
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void shouldThrowExceptionWhenFactoryConstructorThrowsException() {
        AppDormitoryRepository repository = mock(AppDormitoryRepository.class);

        try (MockedConstruction<Reflections> mocked = mockConstruction(Reflections.class, (mock, context) -> {
            Set classes = new HashSet<>();
            classes.add(ThrowingConstructorFactory.class);
            when(mock.getSubTypesOf(DormitorySpecificationFactory.class)).thenReturn(classes);
        })) {
            SpecificationScanner scanner = new SpecificationScanner(repository);

            RuntimeException exception = assertThrows(RuntimeException.class, scanner::scanAndRegisterAll);

            assertTrue(exception.getMessage().contains("Failed to instantiate factory"));
            assertEquals(java.lang.reflect.InvocationTargetException.class, exception.getCause().getClass());

            verify(repository, never()).add(any());
        }
    }
}