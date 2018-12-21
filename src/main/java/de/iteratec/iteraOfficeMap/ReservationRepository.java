package de.iteratec.iteraOfficeMap;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long endDate, Long startDate);

    List<Reservation> findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndWorkplace(
            Long endDate, Long startDate, Workplace workplace);

    Reservation findByStartDateEqualsAndWorkplace(Long startDate, Workplace workplace);

    List<Reservation> findByUsernameEqualsOrderByStartDateAsc(String username);


    Reservation findByWorkplaceAndStartDateEqualsAndEndDateEquals(Workplace workplace, Long startDate, Long endDate);

    @Transactional
    List<Reservation> deleteByWorkplaceAndStartDateAndEndDate(
            Workplace workplace, Long StartDate, Long EndDate);


}
