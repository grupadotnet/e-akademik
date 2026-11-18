package pl.grupanet.eakademik.dormitory.repositories;

import org.reflections.Reflections;
import pl.grupanet.eakademik.dormitory.DormitorySpecificationFactory;

import java.lang.reflect.Modifier;
import java.util.Set;

class SpecificationScanner {

    private final AppDormitoryRepository repository;

    public SpecificationScanner(AppDormitoryRepository repository) {
        this.repository = repository;
    }

    public void scanAndRegisterAll() {
        Reflections reflections = new Reflections("pl.grupanet.eakademik.dormitory.spec");

        Set<Class<? extends DormitorySpecificationFactory>> subTypes =
                reflections.getSubTypesOf(DormitorySpecificationFactory.class);

        for (Class<? extends DormitorySpecificationFactory> clazz : subTypes) {

            if (Modifier.isAbstract(clazz.getModifiers()) || clazz.isInterface()) {
                continue;
            }

            try {
                DormitorySpecificationFactory factory = clazz.getDeclaredConstructor().newInstance();
                repository.add(factory.create());
            } catch (Exception e) {
                throw new RuntimeException("Failed to instantiate factory: " + clazz.getName(), e);
            }
        }
    }
}