package de.iteratec.iteraOfficeMap;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return workplaceService.getAllWorkplaces();
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
    public void addWorkplace(@RequestBody AddWorkplaceDTO workplace) {
        workplaceService.addNewWorkplace(workplace);
    }

    @ApiOperation(value = "deleteWorkplace", notes = "Deletes one workplace from the database.")
    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteworkplace", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {@ApiResponse(code = 406, message = "Entry does not exists")})
    public void deleteWorkplace(@RequestBody DeleteWorkplaceDTO workplace) {
        workplaceService.deleteWorkplace(workplace);
    }
}
