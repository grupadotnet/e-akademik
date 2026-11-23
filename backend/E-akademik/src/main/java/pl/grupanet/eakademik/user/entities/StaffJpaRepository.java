package pl.grupanet.eakademik.user.entities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StaffJpaRepository extends JpaRepository<StaffEntity, UUID> {

}
