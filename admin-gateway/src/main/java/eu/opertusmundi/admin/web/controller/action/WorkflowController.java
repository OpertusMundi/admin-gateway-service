package eu.opertusmundi.admin.web.controller.action;

import java.util.List;

import org.camunda.bpm.engine.rest.dto.runtime.IncidentDto;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.opertusmundi.common.model.RestResponse;

@RequestMapping(path = "/action", produces = "application/json")
@Secured({ "ROLE_USER" })
public interface WorkflowController {

	/**
	 * Count incidents
	 * 
	 * @return
	 */
	@GetMapping(value = "/workflows/incidents/count")
	RestResponse<?> countIncidents();

	/**
	 * Get incidents
	 * 
	 * @return
	 */
	@GetMapping(value = "/workflows/incidents")
	RestResponse<List<IncidentDto>> getIncidents();

}