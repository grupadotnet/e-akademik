package pl.grupanet.eakademik.dormitory.repositories;

import org.commons.DormitoryId;
import pl.grupanet.eakademik.dormitory.DormitorySpecification;

import java.util.List;

public interface DormitoryRepository {
    DormitorySpecification get(DormitoryId dormitoryId);
    List<DormitoryId> getAllId();
}
