package pl.grupanet.eakademik.user.dto;

import java.util.UUID;

public record StaffIdDto(UUID userId, String name, String surname) {
}