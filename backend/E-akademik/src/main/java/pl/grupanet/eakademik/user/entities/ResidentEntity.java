package pl.grupanet.eakademik.user.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "residents")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ResidentEntity extends BaseUserEntity {

    @Column(name = "room_number", nullable = false)
    private String roomNumber;
}
