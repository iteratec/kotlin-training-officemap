package de.iteratec.iteraOfficeMap.workplace;

import de.iteratec.iteraOfficeMap.exceptions.AlreadyExistsException;
import de.iteratec.iteraOfficeMap.exceptions.DoesNotExistException;
import de.iteratec.iteraOfficeMap.reservation.Reservation;
import de.iteratec.iteraOfficeMap.reservation.ReservationRepository;
import de.iteratec.iteraOfficeMap.reservation.ReservationStatus;
import de.iteratec.iteraOfficeMap.utility.DateUtility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("dev")
@ExtendWith(SpringExtension.class)
class WorkplaceServiceTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Autowired
    private WorkplaceService workplaceService;

    private Workplace workplace1;
    private Workplace workplace2;
    private Workplace workplace3;
    private Workplace workplace4;
    private Long currentDay = DateUtility.startOfDay(new Date());

    private void createAndSaveWorkplaces() {
        workplace1 = new Workplace("First", 1, 1, "mapId", "Zwei Bildschirme");
        workplace2 = new Workplace("Second", 2, 2, "mapId", "Zwei Bildschirme");
        workplace3 = new Workplace("Third", 3, 3, "mapId", "Zwei Bildschirme");
        workplace4 = new Workplace("Fourth", 4, 4, "mapId", "Zwei Bildschirme");
        workplaceRepository.saveAll(asList(workplace1, workplace2, workplace3));
    }

    void assertWorkplaceEqualsWorkplace(Workplace workplace, Workplace workplace2) {
        assertEquals(workplace.getName(), workplace2.getName());
        assertEquals(workplace.getX(), workplace2.getX());
        assertEquals(workplace.getY(), workplace2.getY());
        assertEquals(workplace.getEquipment(), workplace2.getEquipment());
    }

    @BeforeEach
    void init() {
        reservationRepository.deleteAll(reservationRepository.findAll());
        workplaceRepository.deleteAll(workplaceRepository.findAll());
    }


    @Test
    void getAllWorkplacesTest() {
        createAndSaveWorkplaces();
        List<Workplace> workplaceList = workplaceService.getAllWorkplaces();

        assertEquals(3, workplaceList.size());
        assertWorkplaceEqualsWorkplace(workplace1, workplaceList.get(0));
        assertWorkplaceEqualsWorkplace(workplace2, workplaceList.get(1));
        assertWorkplaceEqualsWorkplace(workplace3, workplaceList.get(2));
    }

    @Test
    void getAllWorkplacesEmptyListTest() {
        assertEquals(0, workplaceService.getAllWorkplaces().size());
    }

    @Test
    void addWorkPlaceTest() {
        workplace4 = new Workplace("Fourth", 4, 4, "mapId", "Zwei Bildschirme");
        workplaceService.addNewWorkplace(workplace4);
        List<Workplace> workplaceList = workplaceService.getAllWorkplaces();

        assertEquals(1, workplaceList.size());
        assertWorkplaceEqualsWorkplace(workplace4, workplaceList.get(0));
    }

    @Test
    void getStatusOfFreeWorkplaceTest() {
        workplace4 = new Workplace("fourth", 4, 4, "mapId", "Zwei Bildschirme");
        workplaceRepository.save(workplace4);

        assertEquals(ReservationStatus.FREE, workplaceService.getStatus(workplace4.getId()));
    }

    @Test
    void getStatusOfNonAdhocReservedTest() {
        workplace4 = new Workplace("fourth", 4, 4, "mapId", "Zwei Bildschirme");
        workplaceRepository.save(workplace4);
        Reservation reserveToday = new Reservation(currentDay, currentDay, "Sascha", workplace4);
        reservationRepository.save(reserveToday);

        assertEquals(ReservationStatus.NORMALRESERVATION, workplaceService.getStatus(workplace4.getId()));
    }

    @Test
    void addAlreadyExistingWorkPlaceWithSameCoordinates() {
        Assertions.assertThrows(AlreadyExistsException.class, () -> {
            workplace4 = new Workplace("fourth", 4, 4, "mapId", "Zwei Bildschirme");
            workplace3 = new Workplace("third", 4, 4, "mapId", "Zwei Bildschirme");
            workplaceService.addNewWorkplace(workplace3);
            workplaceService.addNewWorkplace(workplace4);
        });
    }

    @Test
    void addAlreadyExistingWorkPlaceWithSameName() {
        Assertions.assertThrows(AlreadyExistsException.class, () -> {
            workplace4 = new Workplace("fourth", 4, 4, "mapId", "Zwei Bildschirme");
            workplace3 = new Workplace("fourth", 3, 3, "mapId", "Zwei Bildschirme");
            workplaceService.addNewWorkplace(workplace4);
            workplaceService.addNewWorkplace(workplace3);
        });
    }

    @Test
    void deleteWorkPlaceTest() {
        createAndSaveWorkplaces();
        workplaceService.deleteWorkplace(workplace1.getId());
        List<Workplace> workplaceList = workplaceRepository.findAll();
        assertEquals(2, workplaceList.size());
        assertEquals(workplace2, workplaceList.get(0));
        assertEquals(workplace3, workplaceList.get(1));
    }

    @Test
    void DeleteNonExistingWorkplaceTest() {
        Assertions.assertThrows(DoesNotExistException.class, () -> {
            createAndSaveWorkplaces();
            workplaceRepository.deleteById(workplace1.getId());
            workplaceService.deleteWorkplace(workplace1.getId());

        });
    }
}
