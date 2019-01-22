package de.iteratec.officemap.workplace

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Workplace(
        @Id
        @GeneratedValue
        val id: Long?,
        val name: String,
        val x: Int,
        val y: Int,
        val mapId: String,
        val equipment: String
)
