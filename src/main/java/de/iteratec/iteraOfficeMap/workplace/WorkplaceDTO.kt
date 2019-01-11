package de.iteratec.iteraOfficeMap.workplace

class WorkplaceDTO(
        val id: Long,
        val name: String,
        val mapId: String,
        val x: Int = 0,
        val y: Int = 0,
        val equipment: String
){
    constructor(workplace: Workplace) : this(
            workplace.id!!,
            workplace.name,
            workplace.mapId,
            workplace.x,
            workplace.y,
            workplace.equipment
    )
}
