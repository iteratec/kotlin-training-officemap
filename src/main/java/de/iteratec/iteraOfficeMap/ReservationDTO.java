package de.iteratec.iteraOfficeMap;

public class ReservationDTO {

    public ReservationDTO(Reservation reservation) {
        this.id = reservation.getReservationId();
        this.date = reservation.getStartDate();
        this.endDate = reservation.getEndDate();
        this.user = reservation.getUser();
        this.workplace = reservation.getWorkplace();
    }

    Long id;
    Long date;
    Long endDate;
    String user;
    Workplace workplace;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Workplace getWorkplace() {
        return workplace;
    }

    public void setWorkplace(Workplace workplace) {
        this.workplace = workplace;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

}
