package pl.grupanet.eakademik.user.dto;

import java.util.UUID;

public record ResidentIdDto(UUID userId, String name, String surname, String roomNumber) {
}
