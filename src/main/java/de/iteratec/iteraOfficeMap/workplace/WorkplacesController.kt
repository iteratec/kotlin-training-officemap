package de.iteratec.iteraOfficeMap.workplace

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class WorkplacesController @Autowired constructor(private val workplaceService: WorkplaceService) {

    @GetMapping("/allworkplaces")
    @ApiOperation(value = "getAllWorkplaces", notes = "Returns a list of all workplaces.")
    fun allWorkplaces(): List<WorkplaceDTO> {
        return workplaceService.getAllWorkplaces()
                .map { WorkplaceDTO(it) }
    }

    @GetMapping("/getstatus")
    @ApiOperation(value = "getStatus", notes = "Returns a list of all workplaces.")
    fun getStatus(workplaceID: Long?): String {
        return workplaceService.getStatus(workplaceID!!).toString()
    }

    @PostMapping("/addworkplace", consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "addNewWorkplace", notes = "Adds one new workplace to the database.")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = [ApiResponse(code = 406, message = "Entry already exists")])
    fun addWorkplace(@RequestBody workplaceDTO: AddWorkplaceDTO) {
        workplaceService.addNewWorkplace(Workplace(null, workplaceDTO.name, workplaceDTO.x, workplaceDTO.y, workplaceDTO.mapId, workplaceDTO.equipment))
    }

    @DeleteMapping("/deleteworkplace", consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "deleteWorkplace", notes = "Deletes one workplace from the database.")
    @ApiResponses(value = [ApiResponse(code = 406, message = "Entry does not exists")])
    fun deleteWorkplace(@RequestBody deleteWorkplaceDTO: DeleteWorkplaceDTO) {
        workplaceService.deleteWorkplace(deleteWorkplaceDTO.id)
    }

    @PutMapping("/updateworkplace", consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "updateWorkplace", notes = "Updates one workplace in the database.")
    @ApiResponses(value = [ApiResponse(code = 406, message = "Entry does not exists")])
    fun updateWorkplace(@RequestBody workplaceDTO: UpdateWorkplaceDTO) {
        val updateWorkplace = Workplace(workplaceDTO.id, workplaceDTO.name, workplaceDTO.x, workplaceDTO.y, workplaceDTO.mapId, workplaceDTO.equipment)
        workplaceService.updateWorkplace(updateWorkplace)
    }

}
