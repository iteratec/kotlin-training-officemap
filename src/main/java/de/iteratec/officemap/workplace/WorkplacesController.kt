package de.iteratec.officemap.workplace

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/workplaces")
class WorkplacesController @Autowired constructor(private val workplaceService: WorkplaceService) {

    @GetMapping
    @ApiOperation(value = "getAllWorkplaces", notes = "Returns a list of all workplaces.")
    fun allWorkplaces(): List<WorkplaceDTO> {
        return workplaceService.getAllWorkplaces()
                .map { it.toWorkplaceDTO() }
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "addNewWorkplace", notes = "Creates a new workplace.")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = [ApiResponse(code = 406, message = "Entry already exists")])
    fun addWorkplace(@RequestBody workplaceDTO: AddWorkplaceDTO) {
        workplaceService.addNewWorkplace(Workplace(null, workplaceDTO.name, workplaceDTO.x, workplaceDTO.y, workplaceDTO.mapId, workplaceDTO.equipment))
    }

    @DeleteMapping("{workplaceId}")
    @ApiOperation(value = "deleteWorkplace", notes = "Deletes a workplace.")
    @ApiResponses(value = [ApiResponse(code = 406, message = "Entry does not exist")])
    fun deleteWorkplace(@PathVariable("workplaceId") workplaceId: Long) {
        workplaceService.deleteWorkplace(workplaceId)
    }

    @PutMapping("{workplaceId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "updateWorkplace", notes = "Updates an existing workplace.")
    @ApiResponses(value = [ApiResponse(code = 406, message = "Entry does not exist")])
    fun updateWorkplace(@RequestBody workplaceDTO: UpdateWorkplaceDTO, @PathVariable("workplaceId") workplaceId: Long) {
        val updateWorkplace = Workplace(workplaceId, workplaceDTO.name, workplaceDTO.x, workplaceDTO.y, workplaceDTO.mapId, workplaceDTO.equipment)
        workplaceService.updateWorkplace(updateWorkplace)
    }

}

private fun Workplace.toWorkplaceDTO() = WorkplaceDTO(
        id!!,
        name,
        mapId,
        x,
        y,
        equipment
)

data class WorkplaceDTO(
        val id: Long,
        val name: String,
        val mapId: String,
        val x: Int = 0,
        val y: Int = 0,
        val equipment: String
)

data class AddWorkplaceDTO(
        val x: Int,
        val y: Int,
        val name: String,
        val mapId: String,
        val equipment: String)

data class UpdateWorkplaceDTO(

        @JsonProperty(required = true)
        val x: Int,

        @JsonProperty(required = true)
        val y: Int,

        @JsonProperty(required = true)
        val name: String,

        @JsonProperty(required = true)
        val mapId: String,

        @JsonProperty(required = true)
        val equipment: String

)
