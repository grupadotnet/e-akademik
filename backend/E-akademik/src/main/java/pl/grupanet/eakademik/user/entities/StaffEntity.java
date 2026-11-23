package pl.grupanet.eakademik.user.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "staff")
@NoArgsConstructor
public class StaffEntity extends BaseUserEntity{
}
