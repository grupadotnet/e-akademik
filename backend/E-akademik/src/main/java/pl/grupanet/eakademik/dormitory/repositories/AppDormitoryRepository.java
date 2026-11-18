package pl.grupanet.eakademik.dormitory.repositories;

import org.commons.DormitoryId;
import pl.grupanet.eakademik.dormitory.DormitorySpecification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class AppDormitoryRepository implements DormitoryRepository {
    private final List<DormitoryId> dormitoryIds = new ArrayList<>();
    private final HashMap<DormitoryId, DormitorySpecification> dormitorySpecifications = new HashMap<>();

    void add(DormitorySpecification dormitorySpecification) {
        DormitoryId dormitoryId = dormitorySpecification.dormitoryId();
        if (dormitorySpecifications.containsKey(dormitoryId))
            throw new IllegalArgumentException(dormitoryId + " is exists");

        dormitorySpecifications.put(dormitoryId, dormitorySpecification);
        if(!dormitoryIds.contains(dormitoryId)) dormitoryIds.add(dormitoryId);
    }

    public DormitorySpecification get(DormitoryId dormitoryId) {
        DormitorySpecification specification = dormitorySpecifications.get(dormitoryId);
        if(specification == null) throw new IllegalArgumentException(dormitoryId + " is not exists");
        return specification;
    }

    @Override
    public List<DormitoryId> getAllId() {
        return dormitoryIds;
    }
}
