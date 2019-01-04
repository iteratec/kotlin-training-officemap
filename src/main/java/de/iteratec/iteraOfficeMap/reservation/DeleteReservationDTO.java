package de.iteratec.iteraOfficeMap.reservation;

public class DeleteReservationDTO {

    public DeleteReservationDTO(Long workplaceId, Long startDate, Long endDate) {
        this.workplaceId = workplaceId;
        this.startDate = startDate;
        this.endDate = endDate;
    }


    public DeleteReservationDTO(Reservation reservation) {
        this.workplaceId = reservation.getWorkplace().getId();
        this.startDate = reservation.getStartDate();
        this.endDate = reservation.getEndDate();
    }

    public DeleteReservationDTO() {
    }

    Long workplaceId;
    Long startDate;
    Long endDate;

    public Long getworkplaceId() {
        return workplaceId;
    }

    public void setWorkplaceId(Long workplaceId) {
        this.workplaceId = workplaceId;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long date) {
        this.startDate = date;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

}
