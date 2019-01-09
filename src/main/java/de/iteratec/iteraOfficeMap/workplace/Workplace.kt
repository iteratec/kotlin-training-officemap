package de.iteratec.iteraOfficeMap.workplace

import de.iteratec.iteraOfficeMap.persistence.AbstractJpaPersistable
import javax.persistence.Entity

@Entity
class Workplace(
        val name: String,
        val x: Int,
        val y: Int,
        val mapId: String,
        val equipment: String
) : AbstractJpaPersistable<Long>()
