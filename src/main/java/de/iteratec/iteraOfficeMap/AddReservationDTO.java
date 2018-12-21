package de.iteratec.iteraOfficeMap;

public class AddReservationDTO {


    public AddReservationDTO() {
    }

    public AddReservationDTO(Reservation reservation) {
        this.startDate = reservation.getStartDate();
        this.endDate = reservation.getEndDate();
        this.user = reservation.getUser();
        this.workplaceId = reservation.getWorkplace().getId();
        this.adhoc = reservation.isAdhoc();
    }

    Long workplaceId;
    Long startDate;
    Long endDate;
    String user;
    boolean adhoc;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

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

    public boolean isAdhoc() {
        return adhoc;
    }

    public void setAdhoc(boolean adhoc) {
        this.adhoc = adhoc;
    }
}
