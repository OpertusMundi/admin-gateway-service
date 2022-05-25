package eu.opertusmundi.admin.web.controller.action;

import javax.validation.Valid;

import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.opertusmundi.admin.web.model.workflow.CompleteTaskTaskCommandDto;
import eu.opertusmundi.admin.web.model.workflow.EnumIncidentSortField;
import eu.opertusmundi.admin.web.model.workflow.EnumProcessInstanceHistorySortField;
import eu.opertusmundi.admin.web.model.workflow.EnumProcessInstanceSortField;
import eu.opertusmundi.admin.web.model.workflow.EnumProcessInstanceTaskSortField;
import eu.opertusmundi.admin.web.model.workflow.RetryExternalTaskCommandDto;
import eu.opertusmundi.common.model.BaseResponse;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.RestResponse;

@RequestMapping(path = "/action", produces = "application/json")
@Secured({ "ROLE_USER" })
public interface WorkflowController {

    /**
     * Get process definitions
     *
     * @param processInstanceId
     * @return
     */
    @GetMapping(value = "/workflows/process-definitions")
    RestResponse<?> getProcessDefinitions();

    /**
     * Count process instances
     *
     * @return
     */
    @GetMapping(value = "/workflows/process-instances/count")
    RestResponse<?> countProcessInstances();

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
    RestResponse<?> getProcessInstances(
        @RequestParam(name = "page", required = true) Integer page,
        @RequestParam(name = "size", required = true) Integer size,
        @RequestParam(name = "processDefinitionKey", required = false, defaultValue = "") String processDefinitionKey,
        @RequestParam(name = "businessKey", required = false, defaultValue = "") String businessKey,
        @RequestParam(name = "task", required = false, defaultValue = "") String task,
        @RequestParam(name = "orderBy", defaultValue = "START_TIME") EnumProcessInstanceSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

    /**
     * Count process instance tasks
     *
     * @return
     */
    @GetMapping(value = "/workflows/tasks/count")
    RestResponse<?> countProcessInstanceTasks();

    /**
     * Get process instance tasks
     *
     * @param page
     * @param size
     * @param businessKey
     * @param orderBy
     * @param order
     * @return
     */
    @GetMapping(value = "/workflows/tasks")
    RestResponse<?> getProcessInstanceTasks(
        @RequestParam(name = "page", required = true) Integer page,
        @RequestParam(name = "size", required = true) Integer size,
        @RequestParam(name = "processDefinitionKey", required = false, defaultValue = "") String processDefinitionKey,
        @RequestParam(name = "businessKey", required = false, defaultValue = "") String businessKey,
        @RequestParam(name = "task", required = false, defaultValue = "") String task,
        @RequestParam(name = "orderBy", defaultValue = "START_TIME") EnumProcessInstanceTaskSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

    /**
     * Get process instance
     *
     * @param processInstanceId
     * @return
     */
    @GetMapping(value = "/workflows/process-instances/{processInstanceId}")
    RestResponse<?> getProcessInstance(
        @PathVariable(name = "processInstanceId", required = true) String processInstanceId
    );

    /**
     * Get process instance
     *
     * @param processInstanceId
     * @return
     */
    @GetMapping(value = "/workflows/process-instances/business-key/{businessKey}")
    RestResponse<?> getProcessInstanceByBusinessKey(
        @PathVariable(name = "businessKey", required = true) String businessKey
    );

    /**
     * Get process instances history
     *
     * @param page
     * @param size
     * @param businessKey
     * @param orderBy
     * @param order
     * @return
     */
    @GetMapping(value = "/workflows/history/process-instances")
    RestResponse<?> getHistoryProcessInstances(
        @RequestParam(name = "page", required = true) Integer page,
        @RequestParam(name = "size", required = true) Integer size,
        @RequestParam(name = "processDefinitionKey", required = false, defaultValue = "") String processDefinitionKey,
        @RequestParam(name = "businessKey", required = false, defaultValue = "") String businessKey,
        @RequestParam(name = "orderBy", defaultValue = "START_TIME") EnumProcessInstanceHistorySortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

    /**
     * Get process instance historical data
     *
     * @param processInstanceId
     * @return
     */
    @GetMapping(value = "/workflows/history/process-instances/{processInstanceId}")
    RestResponse<?> getHistoryProcessInstance(
        @PathVariable(name = "processInstanceId", required = true) String processInstanceId
    );

    /**
     * Get process instance historical data
     *
     * @param processInstanceId
     * @return
     */
    @GetMapping(value = "/workflows/history/process-instances/business-key/{businessKey}")
    RestResponse<?> getHistoryProcessInstanceByBusinessKey(
        @PathVariable(name = "businessKey", required = true) String businessKey
    );

    /**
     * Retry external task
     *
     * @param processInstanceId
     * @param command
     * @param validationResult
     * @return
     */
    @PostMapping(value = "/workflows/process-instances/{processInstanceId}/retry")
    @Validated
    RestResponse<?> retryExternalTask(
        @PathVariable(name = "processInstanceId", required = true) String processInstanceId,
        @Valid @RequestBody RetryExternalTaskCommandDto command,
        BindingResult validationResult
    );

    /**
     * Set error message
     *
     * @param processInstanceId
     * @param command
     * @param validationResult
     * @return
     */
    @PostMapping(value = "/workflows/process-instances/{processInstanceId}/tasks")
    @Validated
    BaseResponse completeTask(
        @PathVariable(name = "processInstanceId", required = true) String processInstanceId,
        @Valid @RequestBody CompleteTaskTaskCommandDto command,
        BindingResult validationResult
    );

    /**
     * Delete process instance
     *
     * @param processInstanceId
     * @return
     */
    @DeleteMapping(value = "/workflows/process-instances/{processInstanceId}")
    BaseResponse deleteProcessInstance(@PathVariable(name = "processInstanceId") String processInstanceId);

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