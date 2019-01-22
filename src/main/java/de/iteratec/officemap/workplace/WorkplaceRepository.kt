package de.iteratec.officemap.workplace

import org.springframework.data.jpa.repository.JpaRepository

interface WorkplaceRepository : JpaRepository<Workplace, Long> {

    fun findByXEqualsAndYEqualsOrNameEquals(x: Int, y: Int, name: String): Workplace?

}
