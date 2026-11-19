package pl.grupanet.eakademik.reservation;


abstract class Reservation {
    protected final ReservationId reservationId;

    public Reservation(final ReservationId reservationId) {
        this.reservationId = reservationId;
    }
}