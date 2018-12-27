package de.iteratec.iteraOfficeMap;

import de.iteratec.iteraOfficeMap.exceptions.AlreadyExistsException;
import de.iteratec.iteraOfficeMap.exceptions.DoesNotExistException;
import de.iteratec.iteraOfficeMap.utility.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class WorkplaceService {
    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public List<WorkplaceDTO> getAllWorkplaces() {
        List<Workplace> allWorkplaces = workplaceRepository.findAll();
        List<WorkplaceDTO> allWorkplacesDTO = new ArrayList<WorkplaceDTO>();
        for (Iterator<Workplace> iterator = allWorkplaces.iterator(); iterator
                .hasNext(); ) {
            Workplace workplace = iterator.next();
            WorkplaceDTO workplaceDTO = new WorkplaceDTO(workplace);
            allWorkplacesDTO.add(workplaceDTO);
        }
        return allWorkplacesDTO;
    }

    /**
     * checks the status of the reservation
     *
     * @param workplaceID
     * @return 0 if free, 1 if adhoc Reserved and 2 if reserved normally
     */
    public Status getStatus(Long workplaceID) {
        //macAddress = MacAddressValidator.convertMac(macAddress);
        Workplace workplace = workplaceRepository.findById(workplaceID).get();

        Long date = DateUtility.startOfDay(new Date());
        Reservation reservation = reservationRepository
                .findByStartDateEqualsAndWorkplace(date, workplace);
        if (reservation == null) {
            return Status.FREE;
        }
         else {
            return Status.NORMALRESERVATION;
        }
    }


    public void addNewWorkplace(AddWorkplaceDTO workplace) {
        Workplace existingWorkplace =
                workplaceRepository.findByXEqualsAndYEqualsOrNameEquals(workplace.getX(), workplace.getY(), workplace.getName());

        if (existingWorkplace == null) {

            workplaceRepository.save(new Workplace(workplace.name,
                    workplace.x, workplace.y, workplace.mapId));
        } else {
            throw new AlreadyExistsException();
        }

    }

    public void deleteWorkplace(DeleteWorkplaceDTO workplace) {
        try {
            workplaceRepository.deleteById(workplace.id);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new DoesNotExistException();
        }

    }
}
