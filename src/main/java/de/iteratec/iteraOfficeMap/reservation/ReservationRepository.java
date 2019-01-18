package de.iteratec.iteraOfficeMap.reservation;

import de.iteratec.iteraOfficeMap.workplace.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate endDate, LocalDate startDate);

    List<Reservation> findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndWorkplace(
            LocalDate endDate, LocalDate startDate, Workplace workplace);

    Reservation findByStartDateEqualsAndWorkplace(LocalDate startDate, Workplace workplace);

    List<Reservation> findByUsernameEqualsOrderByStartDateAsc(String username);

    Reservation findByWorkplaceAndStartDateEqualsAndEndDateEquals(Workplace workplace, LocalDate startDate, LocalDate endDate);

    @Transactional
    List<Reservation> deleteByWorkplaceAndStartDateAndEndDate(
            Workplace workplace, LocalDate startDate, LocalDate endDate);

}
