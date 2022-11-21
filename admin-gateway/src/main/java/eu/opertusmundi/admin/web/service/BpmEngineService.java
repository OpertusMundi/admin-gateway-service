package eu.opertusmundi.admin.web.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.camunda.bpm.engine.rest.dto.VariableValueDto;

import eu.opertusmundi.admin.web.model.workflow.DeploymentDto;
import eu.opertusmundi.admin.web.model.workflow.EnumIncidentSortField;
import eu.opertusmundi.admin.web.model.workflow.EnumProcessInstanceHistorySortField;
import eu.opertusmundi.admin.web.model.workflow.EnumProcessInstanceSortField;
import eu.opertusmundi.admin.web.model.workflow.EnumProcessInstanceTaskSortField;
import eu.opertusmundi.admin.web.model.workflow.HistoryProcessInstanceDetailsDto;
import eu.opertusmundi.admin.web.model.workflow.IncidentDto;
import eu.opertusmundi.admin.web.model.workflow.ProcessDefinitionHeaderDto;
import eu.opertusmundi.admin.web.model.workflow.ProcessInstanceDetailsDto;
import eu.opertusmundi.admin.web.model.workflow.ProcessInstanceDto;
import eu.opertusmundi.admin.web.model.workflow.ProcessInstanceTaskDto;
import eu.opertusmundi.admin.web.model.workflow.RetryExternalTaskCommandDto;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.ServiceException;

public interface BpmEngineService {

    /**
     * Queries for deployments
     *
     * @param sortOrder
     * @param sortBy
     * @return
     */
    List<DeploymentDto> getDeployments(String sortOrder, String sortBy);

    /**
     * Delete deployment
     *
     * @param id
     * @param cascade
     */
    void deleteDeployment(String id, boolean cascade);

    /**
     * Queries for process definitions
     *
     * @return
     */
    List<ProcessDefinitionHeaderDto> getProcessDefinitions();

    /**
     * Retrieves the BPMN 2.0 XML of a process definition.
     *
     * @param processDefinitionId
     * @return
     */
    String getBpmnXml(String processDefinitionId);

    /**
     * Queries for the number of process instances
     *
     * <p>
     * See {@link BpmEngineService#countProcessInstances(String)}
     *
     * @return
     */
    default Long countProcessInstances() {
        return this.countProcessInstances(null);
    }

    /**
     * Queries for the number of process instances
     *
     * @param deploymentId
     *
     * @return
     */
    Long countProcessInstances(String deploymentId);

    /**
     * Queries for process instances that fulfill given parameters
     *
     * @param page
     * @param size
     * @param processDefinitionKey
     * @param businessKey
     * @param task
     * @param orderBy
     * @param order
     * @return
     */
    PageResultDto<ProcessInstanceDto> getProcessInstances(
        int page, int size,
        String processDefinitionKey, String businessKey, String task,
        EnumProcessInstanceSortField orderBy, EnumSortingOrder order
    );

    /**
     * Queries for the number of tasks
     *
     * @return
     */
    Long countProcessInstanceTasks();

    PageResultDto<ProcessInstanceTaskDto> getProcessInstanceTasks(
        int page, int size,
        String processDefinitionKey, String businessKey, String task,
        EnumProcessInstanceTaskSortField orderBy, EnumSortingOrder order
    );

    /**
     * Sets the number of retries left to execute external tasks by id
     * synchronously
     *
     * @param processInstanceId
     * @param externalTaskId
     */
    void retryExternalTask(String processInstanceId, String externalTaskId);

    /**
     * Sets the number of retries left to execute external tasks by id
     * synchronously
     *
     * @param command
     */
    default void retryExternalTask(RetryExternalTaskCommandDto command) {
        this.retryExternalTask(command.getProcessInstanceId(), command.getExternalTaskId());
    }

    /**
     * Completes a task and updates process variables.
     *
     * @param businessKey
     * @param taskName
     * @param variables
     * @throws ServiceException
     */
    void completeTask(String businessKey, String taskName, Map<String, VariableValueDto> variables) throws ServiceException;

    /**
     * Deletes a running process instance by id.
     *
     * @param processInstanceId
     */
    void deleteProcessInstance(String processInstanceId);

    Optional<ProcessInstanceDetailsDto> getProcessInstance(String businessKey, String processInstanceId);

    PageResultDto<ProcessInstanceDto> getHistoryProcessInstances(
        int page, int size,
        String processDefinitionKey, String businessKey,
        EnumProcessInstanceHistorySortField orderBy, EnumSortingOrder order
    );

    Optional<HistoryProcessInstanceDetailsDto> getHistoryProcessInstance(String businessKey, String processInstanceId);

    /**
     * Queries for the number of incidents
     *
     * @return
     */
    Long countIncidents();

    /**
     * Queries for incidents that fulfill given parameters
     *
     * @param page
     * @param size
     * @param businessKey
     * @param orderBy
     * @param order
     * @return
     */
    PageResultDto<IncidentDto> getIncidents(
        int page, int size, String businessKey, EnumIncidentSortField orderBy, EnumSortingOrder order
    );

    /**
     * Submits a list of modification instructions to change a process
     * instance's execution state
     *
     * @param processInstanceId
     * @param cancelActivityInstances
     * @param startActivities
     */
    void modify(String processInstanceId, List<String> cancelActivities, List<String> startActivities);

}
