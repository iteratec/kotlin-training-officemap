package de.iteratec.iteraOfficeMap

data class AddWorkplaceDTO(
        val x: Int,
        val y: Int,
        val name: String,
        val mapId: String,
        val equipment: String) {

    constructor(workplace: Workplace) : this(
            workplace.x,
            workplace.y,
            workplace.name,
            workplace.mapId,
            workplace.equipment
    )

}
