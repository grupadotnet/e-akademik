package pl.grupanet.eakademik.reservation;

import org.commons.DormitoryId;
import org.commons.RoomNumber;
import org.commons.SlotNumber;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;
import pl.grupanet.eakademik.reservation.entities.BaseReservationEntity;
import pl.grupanet.eakademik.reservation.entities.BaseReservationJpaRepository;
import pl.grupanet.eakademik.reservation.entities.LaundryReservationJpaRepository;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import(ReservationRepositoryConfiguration.class)
class AppReservationConcurrencyTest {

    @Autowired
    private LaundryReservationRepository reservationRepository;

    @MockitoSpyBean
    private LaundryReservationJpaRepository jpaRepository;

    private ReservationId reservationId;
    private ResidentId resident1;
    private ResidentId resident2;
    private StaffId staff1;
    private StaffId staff2;
    private LocalDate reservationDate;

    @BeforeEach
    void setUp() {
        DormitoryId dormitoryId = new DormitoryId.Builder(UniversityNames.values()[0], "Akademik 1").build();
        reservationId = new ReservationId(dormitoryId, new RoomNumber("101"), new SlotNumber(1));
        resident1 = new ResidentId(UUID.randomUUID());
        resident2 = new ResidentId(UUID.randomUUID());
        staff1 = new StaffId(UUID.randomUUID());
        staff2 = new StaffId(UUID.randomUUID());
        reservationDate = LocalDate.of(2026, 11, 20);
    }

    @AfterEach
    void tearDown() {
        jpaRepository.deleteAll();
    }

    @Test
    void shouldAllowOnlyOneAcceptanceWhenTwoStaffMembersAcceptConcurrently() throws InterruptedException {
        reservationRepository.reserve(reservationId, resident1, reservationDate);
        reservationRepository.reserve(reservationId, resident2, reservationDate);

        int threads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        executor.submit(() -> {
            readyLatch.countDown();
            try {
                startLatch.await();
                reservationRepository.accept(reservationId, resident1, staff1, reservationDate);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failureCount.incrementAndGet();
            }
        });

        executor.submit(() -> {
            readyLatch.countDown();
            try {
                startLatch.await();
                reservationRepository.accept(reservationId, resident2, staff2, reservationDate);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failureCount.incrementAndGet();
            }
        });

        readyLatch.await();
        startLatch.countDown();

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        assertEquals(1, successCount.get());
        assertEquals(1, failureCount.get());
    }

    @Test
    void shouldHandleConcurrentAcceptAndReserveOnSameSlotSafely() throws InterruptedException {
        reservationRepository.reserve(reservationId, resident1, reservationDate);

        int threads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);

        AtomicBoolean acceptSucceeded = new AtomicBoolean(false);
        AtomicBoolean reserveSucceeded = new AtomicBoolean(false);

        executor.submit(() -> {
            readyLatch.countDown();
            try {
                startLatch.await();
                reservationRepository.accept(reservationId, resident1, staff1, reservationDate);
                acceptSucceeded.set(true);
            } catch (Exception ignored) {
            }
        });

        executor.submit(() -> {
            readyLatch.countDown();
            try {
                startLatch.await();
                reservationRepository.reserve(reservationId, resident2, reservationDate);
                reserveSucceeded.set(true);
            } catch (Exception ignored) {
            }
        });

        readyLatch.await();
        startLatch.countDown();

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        assertTrue(acceptSucceeded.get());
        if (reserveSucceeded.get()) {
            assertEquals(2, jpaRepository.count());
        } else {
            assertEquals(1, jpaRepository.count());
        }
    }

    @Test
    void shouldNeverHaveBothAcceptedAndNewReservedAfterConcurrentAcceptAndReserve() throws InterruptedException {
        for (int i = 0; i < 50; i++) {
            jpaRepository.deleteAll();
            reservationRepository.reserve(reservationId, resident1, reservationDate);

            ExecutorService executor = Executors.newFixedThreadPool(2);
            CountDownLatch readyLatch = new CountDownLatch(2);
            CountDownLatch startLatch = new CountDownLatch(1);

            executor.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    reservationRepository.accept(reservationId, resident1, staff1, reservationDate);
                } catch (Exception ignored) {
                }
            });

            executor.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    reservationRepository.reserve(reservationId, resident2, reservationDate);
                } catch (Exception ignored) {
                }
            });

            readyLatch.await();
            startLatch.countDown();
            executor.shutdown();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

            var all = jpaRepository.findAll();
            long acceptedCount = all.stream().filter(e -> e.getStatus() == ReservationStatus.ACCEPTED).count();

            assertEquals(1, acceptedCount);
            assertEquals(1, all.size());
        }
    }
}