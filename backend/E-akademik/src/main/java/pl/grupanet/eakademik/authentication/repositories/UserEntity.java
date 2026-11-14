package pl.grupanet.eakademik.authentication.repositories;

import jakarta.persistence.*;
import lombok.*;
import org.commons.Role;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name="users")
class UserEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String universityName;

    @Column(nullable = true)
    private String roomNumber;

    @Column(nullable = false)
    private String dormitoryName;
}
