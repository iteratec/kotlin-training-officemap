package de.iteratec.iteraOfficeMap;

import de.iteratec.iteraOfficeMap.exceptions.*;
import de.iteratec.iteraOfficeMap.utility.DateUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.Principal;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ReservationServiceTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Autowired
    private ReservationService reservationService;

    private Workplace workplace;
    private Reservation reservationLimit1;
    private Reservation reservationMid;
    private Reservation reservationLimit2;
    private Reservation reservationBefore;
    private Reservation reservationAfter;
    private Long currentDay = DateUtility.startOfDay(new Date());
    private final Clock clock = Clock.systemUTC();
    private final long now = Instant.now(clock).toEpochMilli();

    private void createAndSaveReservations() {
        reservationBefore = new Reservation(50L, workplace, "john");
        reservationLimit1 = new Reservation(100L, 149L, workplace, "sam");
        reservationMid = new Reservation(150L, 199L, workplace, "john");
        reservationLimit2 = new Reservation(200L, workplace, "john");
        reservationAfter = new Reservation(250L, 299L, workplace, "john");
        reservationRepository.saveAll(asList(reservationLimit1, reservationMid, reservationLimit2));
    }

    @BeforeEach
    void init() {
        reservationRepository.deleteAll(reservationRepository.findAll());
        workplaceRepository.deleteAll(workplaceRepository.findAll());
        workplace = workplaceRepository.save(new Workplace("myPlace", 0, 0, "mapId"));
    }

    void assertReservationEqualsReservationDTO(Reservation reservation, ReservationDTO reservationDTO) {

        assertEquals(reservation.getStartDate(), reservationDTO.getDate());
        assertEquals(reservation.getEndDate(), reservationDTO.getEndDate());
        assertEquals(reservation.getUser(), reservationDTO.getUser());
        assertEquals(reservation.getWorkplace(), reservationDTO.getWorkplace());
    }

    void assertReservationEqualsAddReservationDTO(Reservation reservation, AddReservationDTO addReservationDTO) {

        assertEquals(reservation.getStartDate(), addReservationDTO.getStartDate());
        assertEquals(reservation.getEndDate(), addReservationDTO.getEndDate());
        assertEquals(reservation.getUser(), addReservationDTO.getUser());
        assertEquals(reservation.getWorkplace().getId(), addReservationDTO.getworkplaceId());
    }


    /*
     *Getting Reservatons Methods Testing
     */
    @Test
    void getAllReservationTest() {
        createAndSaveReservations();
        List<ReservationDTO> reservationDTOList = reservationService.getAllReservations();

        assertEquals(3, reservationDTOList.size());
        assertReservationEqualsReservationDTO(reservationLimit1, reservationDTOList.get(0));
        assertReservationEqualsReservationDTO(reservationMid, reservationDTOList.get(1));
        assertReservationEqualsReservationDTO(reservationLimit2, reservationDTOList.get(2));

    }

    @Test
    void getAllReservationsEmptyListTest() {
        assertEquals(0, reservationService.getAllReservations().size());
    }

    @Test
    void getDailyReservationsTest() {
        Workplace workplace2 = new Workplace("secondPlace", 1, 2, "mapId");
        workplaceRepository.save(workplace2);
        Reservation reservationBefore = new Reservation(currentDay, currentDay, workplace, "Max");
        Reservation reservationAfter = new Reservation(currentDay, currentDay, workplace2, "Moritz");
        reservationRepository.saveAll(asList(reservationBefore, reservationAfter));
        List<ReservationDTO> reservationDTOList = reservationService.getDailyReservations(DateUtility.startOfDay(new Date()));

        assertEquals(2, reservationDTOList.size());
        assertReservationEqualsReservationDTO(reservationBefore, reservationDTOList.get(0));
        assertReservationEqualsReservationDTO(reservationAfter, reservationDTOList.get(1));
    }


    @Test
    void getDailyReservationsEmptyListTest() {
        List<ReservationDTO> reservationDTOList = reservationService.getDailyReservations(DateUtility.startOfDay(new Date()));
        assertEquals(0, reservationDTOList.size());
    }

    @Test
    void getPeriodReservationsTest() {
        createAndSaveReservations();
        List<ReservationDTO> reservationDTOList = reservationService.getPeriodReservations(100L, 200L);

        assertEquals(3, reservationDTOList.size());
        assertReservationEqualsReservationDTO(reservationLimit1, reservationDTOList.get(0));
        assertReservationEqualsReservationDTO(reservationMid, reservationDTOList.get(1));
        assertReservationEqualsReservationDTO(reservationLimit2, reservationDTOList.get(2));
    }

    @Test
    void getPeriodReservationInvalidDatesTest() {
        assertThrows(InvalidDatesException.class, () -> {
            createAndSaveReservations();
            List<ReservationDTO> reservationDTOList = reservationService.getPeriodReservations(200L, 100L);
        });

    }

    @Test
    void getPeriodReservationsEmptyListTest() {
        List<ReservationDTO> reservationDTOList = reservationService.getPeriodReservations(100L, 180L);
        assertEquals(0, reservationDTOList.size());
    }

    @Test
    void getPeriodReservationsEndDateinBetweenTest() {
        createAndSaveReservations();
        List<ReservationDTO> reservationDTOList = reservationService.getPeriodReservations(100L, 180L);

        assertEquals(2, reservationDTOList.size());
        assertReservationEqualsReservationDTO(reservationLimit1, reservationDTOList.get(0));
        assertReservationEqualsReservationDTO(reservationMid, reservationDTOList.get(1));
    }

    @Test
    void getPeriodReservationsStartDateinBetweenTest() {
        createAndSaveReservations();
        List<ReservationDTO> reservationDTOList = reservationService.getPeriodReservations(120L, 199L);

        assertEquals(2, reservationDTOList.size());
        assertReservationEqualsReservationDTO(reservationLimit1, reservationDTOList.get(0));
        assertReservationEqualsReservationDTO(reservationMid, reservationDTOList.get(1));
    }


    @Test
    void getPeriodReservationsStartDateAtLimitTest() {
        createAndSaveReservations();
        List<ReservationDTO> reservationDTOList = reservationService.getPeriodReservations(200L, 210L);

        assertEquals(1, reservationDTOList.size());
        assertReservationEqualsReservationDTO(reservationLimit2, reservationDTOList.get(0));
    }

    @Test
    void getPeriodReservationsEndDateAtLimitTest() {
        createAndSaveReservations();
        List<ReservationDTO> reservationDTOList = reservationService.getPeriodReservations(50L, 100L);

        assertEquals(1, reservationDTOList.size());
        assertReservationEqualsReservationDTO(reservationLimit1, reservationDTOList.get(0));
    }

    @Test
    void getPeriodReservationsNonExistingBeforeTest() {
        createAndSaveReservations();
        List<ReservationDTO> reservationDTOList = reservationService.getPeriodReservations(20L, 99L);
        assertEquals(0, reservationDTOList.size());
    }


    @Test
    void getPeriodReservationsNonExistingAfterTest() {
        createAndSaveReservations();
        List<ReservationDTO> reservationDTOList = reservationService.getPeriodReservations(201L, 205L);
        assertEquals(0, reservationDTOList.size());
    }

    @Test
    void getUserReservationsTest() {
        createAndSaveReservations();
        List<ReservationDTO> reservationDTOList = reservationService.getUserReservations("john");

        assertEquals(2, reservationDTOList.size());
        assertEquals("john", reservationDTOList.get(0).getUser());
        assertEquals("john", reservationDTOList.get(1).getUser());
    }

    @Test
    void getUserReservationsEmptyList() {
        createAndSaveReservations();
        List<ReservationDTO> reservationDTOList = reservationService.getUserReservations("max");

        assertEquals(0, reservationDTOList.size());
    }
    /*
     *GetConflictingReservatons Methods Testing
     */

    @Test
    void getConflictingReservationSameDatesTest() {
        createAndSaveReservations();
        List<ReservationDTO> conflictingReservationDTOList = reservationService.getConflictingReservations(150L, 199L, workplace.getId());

        assertEquals(1, conflictingReservationDTOList.size());
        assertReservationEqualsReservationDTO(reservationMid, conflictingReservationDTOList.get(0));
    }


    @Test
    void getConflictingReservationEndDateBetweenTest() {
        createAndSaveReservations();
        List<ReservationDTO> conflictingReservationDTOList = reservationService.getConflictingReservations(50L, 140L, workplace.getId());

        assertEquals(1, conflictingReservationDTOList.size());
        assertReservationEqualsReservationDTO(reservationLimit1, conflictingReservationDTOList.get(0));
    }

    @Test
    void getConflictingReservationStartdateAtLimitEndDateBetweenTest() {
        createAndSaveReservations();
        List<ReservationDTO> conflictingReservationDTOList = reservationService.getConflictingReservations(100L, 140L, workplace.getId());

        assertEquals(1, conflictingReservationDTOList.size());
        assertReservationEqualsReservationDTO(reservationLimit1, conflictingReservationDTOList.get(0));
    }

    @Test
    void getConflictingReservationStartDateBetweenTest() {
        createAndSaveReservations();
        List<ReservationDTO> conflictingReservationDTOList = reservationService.getConflictingReservations(160L, 250L, workplace.getId());

        assertEquals(2, conflictingReservationDTOList.size());
        assertReservationEqualsReservationDTO(reservationMid, conflictingReservationDTOList.get(0));
        assertReservationEqualsReservationDTO(reservationLimit2, conflictingReservationDTOList.get(1));
    }

    @Test
    void getConflictingReservationStartDateBetweenEndDateAtLimitTest() { // to be implemented
        createAndSaveReservations();
        List<ReservationDTO> conflictingReservationDTOList = reservationService.getConflictingReservations(160L, 200L, workplace.getId());

        assertEquals(2, conflictingReservationDTOList.size());
        assertReservationEqualsReservationDTO(reservationMid, conflictingReservationDTOList.get(0));
        assertReservationEqualsReservationDTO(reservationLimit2, conflictingReservationDTOList.get(1));
    }

    @Test
    void getConflictingReservationNonExistingBeforeTest() { // to be implemented
        createAndSaveReservations();
        List<ReservationDTO> conflictingReservationDTOList = reservationService.getConflictingReservations(10L, 20L, workplace.getId());

        assertEquals(0, conflictingReservationDTOList.size());
    }

    @Test
    void getConflictingReservationNonExistingAfterTest() { // to be implemented
        createAndSaveReservations();
        List<ReservationDTO> conflictingReservationDTOList = reservationService.getConflictingReservations(210L, 220L, workplace.getId());

        assertEquals(0, conflictingReservationDTOList.size());
    }

    @Test
    void getConflictingReservationNonExistingInBetweenTest() {
        createAndSaveReservations();
        List<ReservationDTO> conflictingReservationDTOList = reservationService.getConflictingReservations(210L, 240L, workplace.getId());

        assertEquals(0, conflictingReservationDTOList.size());
    }

    @Test
    void getConflictingReservationStartAndEndDateOutTest() {
        createAndSaveReservations();
        List<ReservationDTO> conflictingReservationDTOList = reservationService.getConflictingReservations(50L, 250L, workplace.getId());

        assertEquals(3, conflictingReservationDTOList.size());
        assertReservationEqualsReservationDTO(reservationLimit1, conflictingReservationDTOList.get(0));
        assertReservationEqualsReservationDTO(reservationMid, conflictingReservationDTOList.get(1));
        assertReservationEqualsReservationDTO(reservationLimit2, conflictingReservationDTOList.get(2));
    }

    /*
     *getWorkplaceReservation Tests
     */

    @Test
    void getWorkPlaceReservationsTest() {
        createAndSaveReservations();
        List<ReservationDTO> reservationDTOList = reservationService.getWorkplaceReservations(50L, 200L, workplace.getId());
        assertEquals(workplace, reservationDTOList.get(0).getWorkplace());
        assertEquals(workplace, reservationDTOList.get(1).getWorkplace());
        assertEquals(workplace, reservationDTOList.get(2).getWorkplace());
    }

    @Test
    void getWorkplaceReservationsOfNullWorkplaceTest() {
        assertThrows(DoesNotExistException.class, () -> {
            createAndSaveReservations();
            reservationService.getWorkplaceReservations(50L, 200L, 54L);
        });
    }

    @Test
    void getWorkplaceReservationsEmptyListTest() {
        workplaceRepository.save(workplace);
        List<ReservationDTO> reservationDTOList = reservationService.getWorkplaceReservations(50L, 200L, workplace.getId());

        assertEquals(0, reservationDTOList.size());
    }



    /*
     *addReservation Tests
     */

    @Test
    void addReservationTest() {
        Reservation reservation = new Reservation(100L, 200L, workplace, "sam");
        AddReservationDTO addReservationDTO = new AddReservationDTO(reservation);
        Principal principal = () -> "sam";
        reservationService.addReservation(addReservationDTO, principal);
        List<Reservation> reservationList = reservationRepository.findAll();

        assertEquals(1, reservationList.size());
        assertReservationEqualsAddReservationDTO(reservationList.get(0), addReservationDTO);
    }


    @Test
    void addReservationEndDateBeforeStartDateTest() {
        assertThrows(InvalidReservationException.class, () -> {
            Reservation reservation = new Reservation(250L, 200L, workplace, "sam");
            AddReservationDTO addReservationDTO = new AddReservationDTO(reservation);
            Principal principal = () -> "sam";
            reservationService.addReservation(addReservationDTO, principal);
        });
    }


    @Test
    void addReservationInvalidWorkplaceTest() {
        assertThrows(InvalidWorkplaceException.class, () -> {
            workplaceRepository.deleteAll(workplaceRepository.findAll());
            Reservation reservation = new Reservation(150L, 200L, workplace, "sam");
            AddReservationDTO addReservationDTO = new AddReservationDTO(reservation);
            Principal principal = () -> "sam";
            reservationService.addReservation(addReservationDTO, principal);
        });
    }

    @Test
    void addConflictingReservationTest() {
        assertThrows(AlreadyExistsException.class, () -> {
            createAndSaveReservations();
            Reservation reservation = new Reservation(100L, 200L, workplace, "sam");
            AddReservationDTO addReservationDTO = new AddReservationDTO(reservation);
            Principal principal = () -> "sam";
            reservationService.addReservation(addReservationDTO, principal);
        });
    }

    @Test
    void addReservationsConflictingTest() {
        assertThrows(AlreadyExistsException.class, () -> {
            createAndSaveReservations();
            reservationRepository.deleteAll();
            reservationRepository.save(reservationMid);
            Principal principal = () -> "sam";
            List<AddReservationDTO> addReservationDTOList = asList(
                    new AddReservationDTO(reservationLimit1), new AddReservationDTO(reservationMid), new AddReservationDTO(reservationLimit2));
            try {
                reservationService.addReservations(addReservationDTOList, principal);
            } catch (AlreadyExistsException e) {
                assertEquals(1, reservationRepository.findAll().size());
                assertTrue(reservationRepository.findAll().contains(reservationMid));

                throw e;
            }
            fail();
        });

    }




    /*
     *delete Reservation methods Testing
     */

    @Test
    void deleteReservationTest() {
        createAndSaveReservations();
        DeleteReservationDTO deleteReservationDTOMid = new DeleteReservationDTO(reservationMid);
        DeleteReservationDTO deleteReservationDTOLimit2 = new DeleteReservationDTO(reservationLimit2);
        reservationService.deleteReservation(deleteReservationDTOLimit2);
        reservationService.deleteReservation(deleteReservationDTOMid);
        List<ReservationDTO> reservationDTOList = reservationService.getAllReservations();

        assertEquals(1, reservationDTOList.size());
        assertReservationEqualsReservationDTO(reservationLimit1, reservationDTOList.get(0));
    }

    @Test
    void deleteReservationWithInvalidWorkPlace() {
        assertThrows(InvalidReservationException.class, () -> {
            workplaceRepository.deleteAll();
            Reservation reservation = new Reservation(now, workplace, "Sascha");
            DeleteReservationDTO deleteReservationDTOMid = new DeleteReservationDTO(reservation);
            reservationService.deleteReservation(deleteReservationDTOMid);
        });
    }


    @Test
    void deleteNonExistingReservationTest() {
        assertThrows(DoesNotExistException.class, () -> {
            Reservation reservation = new Reservation(now, workplace, "Sascha");
            DeleteReservationDTO deleteReservationDTOMid = new DeleteReservationDTO(reservation);
            reservationService.deleteReservation(deleteReservationDTOMid);
        });
    }

    @Test
    void deleteReservationsTest() {
        createAndSaveReservations();
        reservationService.deleteReservations
                (asList(new DeleteReservationDTO(reservationLimit1), new DeleteReservationDTO(reservationMid)));
        assertEquals(1, reservationRepository.findAll().size());
        assertTrue(reservationRepository.findAll().contains(reservationLimit2));
    }

    @Test
    void deleteReservationsWithOneNonExistingTest() {
        createAndSaveReservations();
        reservationRepository.delete(reservationMid);
        reservationService.deleteReservations(asList(
                new DeleteReservationDTO(reservationMid), new DeleteReservationDTO(reservationLimit2)
        ));
        assertEquals(1, reservationRepository.findAll().size());
        assertTrue(reservationRepository.findAll().contains(reservationLimit1));
    }

}
