package de.iteratec.iteraOfficeMap;

import de.iteratec.iteraOfficeMap.exceptions.*;
import de.iteratec.iteraOfficeMap.utility.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private WorkplaceRepository workplaceRepository;


    public List<ReservationDTO> getAllReservations() {
        List<Reservation> allReservations = reservationRepository.findAll();
        List<ReservationDTO> allReservationsDTO = new ArrayList<ReservationDTO>();
        for (Reservation reservation : allReservations) {
            ReservationDTO reservationDTO = new ReservationDTO(reservation);
            allReservationsDTO.add(reservationDTO);
        }
        return allReservationsDTO;
    }

    // adds a new reservation to the database
    public void addReservation(AddReservationDTO addReservationDTO, Principal principal) {

        Workplace workplace = checkValidityAndGetWorkplace(addReservationDTO);

        addReservationDirect(addReservationDTO, workplace, principal);


    }

    private void addReservationDirect(AddReservationDTO addReservationDTO, Workplace workplace, Principal principal) {
        reservationRepository.save(new Reservation(addReservationDTO.getStartDate(),
                addReservationDTO.getEndDate(), workplace, principal.getName()));
    }

    private Workplace checkValidityAndGetWorkplace(AddReservationDTO addReservationDTO) {
        Workplace workplace = workplaceRepository.findById(addReservationDTO.getWorkplaceId())
                .orElseThrow(InvalidWorkplaceException::new);

        if (addReservationDTO.getStartDate() > addReservationDTO.getEndDate()) {
            throw new InvalidReservationException();
        }

        if (!getConflictingReservations(addReservationDTO.getStartDate(), addReservationDTO.getEndDate(), addReservationDTO.getWorkplaceId())
                .isEmpty()) {
            throw new AlreadyExistsException();
        }
        return workplace;
    }


    /**
     * adds a list of reservations. None of them will be added if any of the reservations is invalid
     *
     * @param reservations
     * @throws AlreadyExistsException      if any reservation already exists
     * @throws InvalidDatesException       if the dates are invalid
     * @throws InvalidReservationException if the reservation is invalid
     */
    public void addReservations(List<AddReservationDTO> reservations, Principal principal) {

        List<Workplace> workplaceList = reservations.stream().map((r) -> checkValidityAndGetWorkplace(r)).collect(Collectors.toList());

        for (int i = 0; i < reservations.size(); i++) {
            addReservationDirect(reservations.get(i), workplaceList.get(i), principal);
        }

    }


    /**
     * @param startDate
     * @param endDate
     * @param workplaceId
     * @return a list of reservations, if there are any in given period
     */

    public List<ReservationDTO> getConflictingReservations(Long startDate, Long endDate, Long workplaceId) {

        List<ReservationDTO> conflictingReservations = new ArrayList<ReservationDTO>();
        Workplace workplace = workplaceRepository.findById(workplaceId).get();


        //starts at start or in btw n ends in btw or at end
        List<Reservation> reservationList = reservationRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndWorkplace(endDate, startDate, workplace);


        for (Iterator<Reservation> iterator = reservationList.iterator(); iterator
                .hasNext(); ) {
            Reservation reservation = (Reservation) iterator.next();
            ReservationDTO reservationDTO = new ReservationDTO(reservation);
            conflictingReservations.add(reservationDTO);
        }
        return conflictingReservations;
    }

    /**
     * @param date
     * @return a list of all reservations for one day
     */
    public List<ReservationDTO> getDailyReservations(Long date) {
        List<ReservationDTO> dailyReservations = new ArrayList<ReservationDTO>();
        List<Reservation> allReservations = reservationRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(date,
                        date);
        for (Iterator<Reservation> iterator = allReservations.iterator(); iterator
                .hasNext(); ) {
            Reservation reservation = (Reservation) iterator.next();
            ReservationDTO reservationDTO = new ReservationDTO(reservation);
            dailyReservations.add(reservationDTO);
        }

        return dailyReservations;
    }

    /**
     * @param startDate
     * @param endDate
     * @return a list of all reservations for a time period
     */
    public List<ReservationDTO> getPeriodReservations(Long startDate,
                                                      Long endDate) {
        if (startDate > endDate) {
            throw new InvalidDatesException();
        }
        List<ReservationDTO> periodReservations = new ArrayList<ReservationDTO>();
        List<Reservation> allReservations = reservationRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        endDate, startDate);
        for (Iterator<Reservation> iterator = allReservations.iterator(); iterator
                .hasNext(); ) {
            Reservation reservation = (Reservation) iterator.next();
            ReservationDTO reservationDTO = new ReservationDTO(reservation);
            periodReservations.add(reservationDTO);
        }
        return periodReservations;
    }

    /**
     * @param startDate
     * @param endDate
     * @param workplaceId
     * @return a list of a workplaces reservations for a time period
     */
    public List<ReservationDTO> getWorkplaceReservations(Long startDate,
                                                         Long endDate, Long workplaceId) {
        Workplace workplace = workplaceRepository.findById(workplaceId).orElseThrow(DoesNotExistException::new);

        List<ReservationDTO> workplaceReservations = new ArrayList<ReservationDTO>();
        List<Reservation> allReservations = reservationRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndWorkplace(
                        endDate, startDate,
                        workplace);
        for (Iterator<Reservation> iterator = allReservations.iterator(); iterator
                .hasNext(); ) {
            Reservation reservation = (Reservation) iterator.next();
            ReservationDTO reservationDTO = new ReservationDTO(reservation);
            workplaceReservations.add(reservationDTO);
        }
        return workplaceReservations;
    }

    /**
     * @param user
     * @return a list of all reservations for one user
     */
    public List<ReservationDTO> getUserReservations(String user) {
        List<ReservationDTO> userReservations = new ArrayList<ReservationDTO>();
        List<Reservation> allReservations = reservationRepository
                .findByUsernameEqualsOrderByStartDateAsc(user);
        for (Iterator<Reservation> iterator = allReservations.iterator(); iterator
                .hasNext(); ) {
            Reservation reservation = (Reservation) iterator.next();
            ReservationDTO reservationDTO = new ReservationDTO(reservation);
            userReservations.add(reservationDTO);
        }
        return userReservations;
    }


    /**
     * deletes one explicit reservation
     *
     * @param deleteReservationDTO
     * @throws InvalidReservationException
     * @throws DoesNotExistException
     */
    public void deleteReservation(DeleteReservationDTO deleteReservationDTO) {
        Workplace workplace = workplaceRepository.findById(deleteReservationDTO.getworkplaceId()).orElseThrow(InvalidReservationException::new);

        Reservation reservation = reservationRepository.findByWorkplaceAndStartDateEqualsAndEndDateEquals(
                workplaceRepository.findById(deleteReservationDTO.getworkplaceId()).get(),
                deleteReservationDTO.getStartDate(),
                deleteReservationDTO.getEndDate());

        if (reservation == null) {
            throw new DoesNotExistException();
        }

        reservationRepository.deleteByWorkplaceAndStartDateAndEndDate(
                workplaceRepository.findById(deleteReservationDTO.getworkplaceId()).get(),
                deleteReservationDTO.getStartDate(),
                deleteReservationDTO.getEndDate());
    }

    /**
     * deletes a list of reservation.
     * If any of the reservations does not exist, it will ignore the exception and delete the other
     *
     * @param reservations
     * @throws InvalidReservationException
     * @throws DoesNotExistException
     */
    public void deleteReservations(List<DeleteReservationDTO> reservations) {

        for (DeleteReservationDTO deleteReservationDTO : reservations) {
            try {
                deleteReservation(deleteReservationDTO);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }



}
