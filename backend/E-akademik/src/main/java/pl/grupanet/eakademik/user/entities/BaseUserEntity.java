package pl.grupanet.eakademik.user.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;

import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseUserEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(name="university_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private UniversityNames universityName;

    @Column(name = "dormitory_name", nullable = false)
    private String dormitoryName;
}
