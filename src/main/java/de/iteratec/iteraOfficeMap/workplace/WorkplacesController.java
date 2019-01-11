package de.iteratec.iteraOfficeMap.workplace;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class WorkplacesController {

    private final WorkplaceService workplaceService;

    @Autowired
    public WorkplacesController(WorkplaceService workplaceService) {
        this.workplaceService = workplaceService;
    }

    @ApiOperation(value = "getAllWorkplaces", notes = "Returns a list of all workplaces.")
    @RequestMapping(method = RequestMethod.GET, value = "/allworkplaces")
    public List<WorkplaceDTO> allWorkplaces() {
        return workplaceService.getAllWorkplaces().stream().map(WorkplaceDTO::new).collect(toList());
    }

    @ApiOperation(value = "getStatus", notes = "Returns a list of all workplaces.")
    @RequestMapping(method = RequestMethod.GET, value = "/getstatus")
    public String getStatus(Long workplaceID) {
        return workplaceService.getStatus(workplaceID).toString();
    }

    @ApiOperation(value = "addNewWorkplace", notes = "Adds one new workplace to the database.")
    @RequestMapping(method = RequestMethod.POST, value = "/addworkplace", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {@ApiResponse(code = 406, message = "Entry already exists")})
    public void addWorkplace(@RequestBody AddWorkplaceDTO workplaceDTO) {
        workplaceService.addNewWorkplace(new Workplace(workplaceDTO.getName(), workplaceDTO.getX(), workplaceDTO.getY(), workplaceDTO.getMapId(), workplaceDTO.getEquipment()));
    }

    @ApiOperation(value = "deleteWorkplace", notes = "Deletes one workplace from the database.")
    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteworkplace", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {@ApiResponse(code = 406, message = "Entry does not exists")})
    public void deleteWorkplace(@RequestBody DeleteWorkplaceDTO deleteWorkplaceDTO) {
        workplaceService.deleteWorkplace(deleteWorkplaceDTO.getId());
    }

    @ApiOperation(value = "updateWorkplace", notes = "Updates one workplace in the database.")
    @RequestMapping(method = RequestMethod.PUT, value = "/updateworkplace", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {@ApiResponse(code = 406, message = "Entry does not exists")})
    public void updateWorkplace(@RequestBody UpdateWorkplaceDTO workplaceDTO) {
        Workplace updateWorkplace = new Workplace(workplaceDTO.getName(), workplaceDTO.getX(), workplaceDTO.getY(), workplaceDTO.getMapId(), workplaceDTO.getEquipment());
        updateWorkplace.setId(workplaceDTO.getId());
        workplaceService.updateWorkplace(updateWorkplace);
    }

}
