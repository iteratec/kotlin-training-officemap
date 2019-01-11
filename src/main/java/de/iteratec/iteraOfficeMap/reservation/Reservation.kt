package de.iteratec.iteraOfficeMap.reservation

import de.iteratec.iteraOfficeMap.persistence.AbstractJpaPersistable
import de.iteratec.iteraOfficeMap.workplace.Workplace
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class Reservation(
        val startDate: Long,
        val endDate: Long,
        val username: String,

        @ManyToOne
        @OnDelete(action = OnDeleteAction.CASCADE)
        @JoinColumn(name = "workplace_id")
        val workplace: Workplace
) : AbstractJpaPersistable<Long>()
