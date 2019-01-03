package de.iteratec.iteraOfficeMap;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long startDate;
    private Long endDate;
    private String username;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "workplace_id")
    private Workplace workplace;

    public Reservation() {
    }

    public Reservation(Long startDate, Workplace workplace, String username) {
        super();
        this.startDate = startDate;
        this.endDate = startDate;
        this.workplace = workplace;
        this.username = username;
    }

    public Reservation(Long startDate, Long endDate, Workplace workplace,
                       String username) {
        super();
        this.startDate = startDate;
        this.endDate = endDate;
        this.workplace = workplace;
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return
                Objects.equals(startDate, that.startDate) &&
                        Objects.equals(endDate, that.endDate) &&
                        Objects.equals(username, that.username) &&
                        Objects.equals(workplace, that.workplace);
    }

    @Override
    public int hashCode() {

        return Objects.hash(startDate, endDate, username, workplace);
    }

    public Long getReservationId() {
        return id;
    }

    public void setReservationId(Long reservationId) {
        this.id = id;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public String getUser() {
        return username;
    }

    public void setUser(String username) {
        this.username = username;
    }

    public Workplace getWorkplace() {
        return workplace;
    }

    public void setWorkplace(Workplace workplace) {
        this.workplace = workplace;
    }
}
