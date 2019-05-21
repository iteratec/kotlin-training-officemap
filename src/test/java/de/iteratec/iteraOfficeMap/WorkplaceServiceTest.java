package de.iteratec.iteraOfficeMap;

import de.iteratec.iteraOfficeMap.exceptions.AlreadyExistsException;
import de.iteratec.iteraOfficeMap.exceptions.DoesNotExistException;
import de.iteratec.iteraOfficeMap.utility.DateUtility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class WorkplaceServiceTest {

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
        workplace1 = new Workplace("First", 1, 1, "mapId");
        workplace2 = new Workplace("Second", 2, 2, "mapId");
        workplace3 = new Workplace("Third", 3, 3, "mapId");
        workplace4 = new Workplace("Fourth", 4, 4, "mapId");
        workplaceRepository.saveAll(asList(workplace1, workplace2, workplace3));
    }

    void assertWorkplaceEqualsWorkplaceDTO(Workplace workplace, WorkplaceDTO workplaceDTO) {

        // assertEquals(workplace.getId(),workplaceDTO.getId());
        assertEquals(workplace.getName(), workplaceDTO.getName());
        assertEquals(workplace.getX(), workplaceDTO.getX());
        assertEquals(workplace.getY(), workplaceDTO.getY());
    }

    @BeforeEach
    void init() {
        reservationRepository.deleteAll(reservationRepository.findAll());
        workplaceRepository.deleteAll(workplaceRepository.findAll());
    }


    @Test
    void getAllWorkplacesTest() {
        createAndSaveWorkplaces();
        List<WorkplaceDTO> workplaceDTOList = workplaceService.getAllWorkplaces();

        assertEquals(3, workplaceDTOList.size());
        assertWorkplaceEqualsWorkplaceDTO(workplace1, workplaceDTOList.get(0));
        assertWorkplaceEqualsWorkplaceDTO(workplace2, workplaceDTOList.get(1));
        assertWorkplaceEqualsWorkplaceDTO(workplace3, workplaceDTOList.get(2));
    }

    @Test
    void getAllWorkplacesEmptyListTest() {
        assertEquals(0, workplaceService.getAllWorkplaces().size());
    }

    @Test
    void addWorkPlaceTest() {
        workplace4 = new Workplace("Fourth", 4, 4, "mapId");
        AddWorkplaceDTO addWorkplaceDTO = new AddWorkplaceDTO(workplace4);
        workplaceService.addNewWorkplace(addWorkplaceDTO);
        List<WorkplaceDTO> workplaceDTOList = workplaceService.getAllWorkplaces();

        assertEquals(1, workplaceDTOList.size());
        assertWorkplaceEqualsWorkplaceDTO(workplace4, workplaceDTOList.get(0));
    }

    @Test
    void getStatusOfFreeWorkplaceTest() {
        workplace4 = new Workplace("fourth", 4, 4, "mapId");
        workplaceRepository.save(workplace4);

        assertEquals(Status.FREE, workplaceService.getStatus(workplace4.getId()));
    }

    @Test
    void getStatusOfReservedTest() {
        workplace4 = new Workplace("fourth", 4, 4, "mapId");
        workplaceRepository.save(workplace4);
        Reservation reserveToday = new Reservation(currentDay, workplace4, "Sascha");
        reservationRepository.save(reserveToday);

        assertEquals(Status.NORMALRESERVATION, workplaceService.getStatus(workplace4.getId()));
    }

    @Test
    void addAlreadyExistingWorkPlaceWithSameCoordinates() {
        Assertions.assertThrows(AlreadyExistsException.class, () -> {
            workplace4 = new Workplace("fourth", 4, 4, "mapId");
            workplace3 = new Workplace("third", 4, 4, "mapId");
            AddWorkplaceDTO addWorkplaceDTO = new AddWorkplaceDTO(workplace4);
            AddWorkplaceDTO addWorkplaceDTONew = new AddWorkplaceDTO(workplace3);
            workplaceService.addNewWorkplace(addWorkplaceDTO);
            workplaceService.addNewWorkplace(addWorkplaceDTONew);
        });
    }

    @Test
    void addAlreadyExistingWorkPlaceWithSameName() {
        Assertions.assertThrows(AlreadyExistsException.class, () -> {
            workplace4 = new Workplace("fourth", 4, 4, "mapId");
            workplace3 = new Workplace("fourth", 3, 3, "mapId");
            AddWorkplaceDTO addWorkplaceDTO = new AddWorkplaceDTO(workplace4);
            AddWorkplaceDTO addWorkplaceDTONew = new AddWorkplaceDTO(workplace3);
            workplaceService.addNewWorkplace(addWorkplaceDTO);
            workplaceService.addNewWorkplace(addWorkplaceDTONew);
        });
    }

    @Test
    void deleteWorkPlaceTest() {
        createAndSaveWorkplaces();
        DeleteWorkplaceDTO deleteWorkplaceDTO = new DeleteWorkplaceDTO(workplace1.getId());
        workplaceService.deleteWorkplace(deleteWorkplaceDTO);
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
            DeleteWorkplaceDTO deleteWorkplaceDTO = new DeleteWorkplaceDTO(workplace1.getId());
            workplaceService.deleteWorkplace(deleteWorkplaceDTO);
        });
    }
}
