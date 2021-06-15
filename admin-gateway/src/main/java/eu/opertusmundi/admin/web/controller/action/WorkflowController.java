package eu.opertusmundi.admin.web.controller.action;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.opertusmundi.admin.web.model.workflow.EnumIncidentSortField;
import eu.opertusmundi.admin.web.model.workflow.EnumProcessInstanceSortField;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.RestResponse;

@RequestMapping(path = "/action", produces = "application/json")
@Secured({ "ROLE_USER" })
public interface WorkflowController {

    /**
     * Count process instances
     * 
     * @return
     */
    @GetMapping(value = "/workflows/process-instances/count")
    RestResponse<?> countProcessInstances();
    
	/**
	 * Count incidents
	 * 
	 * @return
	 */
	@GetMapping(value = "/workflows/incidents/count")
	RestResponse<?> countIncidents();

	/**
	 * Get process instances
	 * 
	 * @param page
	 * @param size
	 * @param businessKey
	 * @param orderBy
	 * @param order
	 * @return
	 */
    @GetMapping(value = "/workflows/process-instances")
    RestResponse<?> getInstances(
        @RequestParam(name = "page", required = true) Integer page,
        @RequestParam(name = "size", required = true) Integer size,
        @RequestParam(name = "businessKey", defaultValue = "") String businessKey,
        @RequestParam(name = "orderBy", defaultValue = "START_TIME") EnumProcessInstanceSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

	/**
	 * Get incidents
	 * 
	 * @param page
	 * @param size
	 * @param processInstanceId
	 * @param orderBy
	 * @param order
	 * @return
	 */
	@GetMapping(value = "/workflows/incidents")
	RestResponse<?> getIncidents(
        @RequestParam(name = "page", required = true) Integer page,
        @RequestParam(name = "size", required = true) Integer size,
        @RequestParam(name = "businessKey", defaultValue = "") String businessKey,
        @RequestParam(name = "orderBy", defaultValue = "START_TIME") EnumIncidentSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

}