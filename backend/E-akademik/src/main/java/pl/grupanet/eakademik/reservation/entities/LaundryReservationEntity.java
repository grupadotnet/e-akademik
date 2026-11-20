package pl.grupanet.eakademik.reservation.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "laundry_reservations")
public class LaundryReservationEntity extends BaseReservationEntity {}
