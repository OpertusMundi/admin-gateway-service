package eu.opertusmundi.admin.web.service;

import java.util.List;
import java.util.Optional;

import eu.opertusmundi.admin.web.model.workflow.EnumIncidentSortField;
import eu.opertusmundi.admin.web.model.workflow.EnumProcessInstanceHistorySortField;
import eu.opertusmundi.admin.web.model.workflow.EnumProcessInstanceSortField;
import eu.opertusmundi.admin.web.model.workflow.HistoryProcessInstanceDetailsDto;
import eu.opertusmundi.admin.web.model.workflow.IncidentDto;
import eu.opertusmundi.admin.web.model.workflow.ProcessDefinitionHeaderDto;
import eu.opertusmundi.admin.web.model.workflow.ProcessInstanceDetailsDto;
import eu.opertusmundi.admin.web.model.workflow.ProcessInstanceDto;
import eu.opertusmundi.admin.web.model.workflow.RetryExternalTaskCommandDto;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;

public interface BpmEngineService {

    List<ProcessDefinitionHeaderDto> getProcessDefinitions();

    Long countProcessInstances();

    PageResultDto<ProcessInstanceDto> getProcessInstances(
        int page, int size,
        String processDefinitionKey, String businessKey,
        EnumProcessInstanceSortField orderBy, EnumSortingOrder order
    );

    void retryExternalTask(String processInstanceId, String externalTaskId);

    default void retryExternalTask(RetryExternalTaskCommandDto command) {
        this.retryExternalTask(command.getProcessInstanceId(), command.getExternalTaskId());
    }

    void deleteProcessInstance(String processInstanceId);

    Optional<ProcessInstanceDetailsDto> getProcessInstance(String businessKey, String processInstanceId);

    PageResultDto<ProcessInstanceDto> getHistoryProcessInstances(
        int page, int size,
        String processDefinitionKey, String businessKey,
        EnumProcessInstanceHistorySortField orderBy, EnumSortingOrder order
    );

    Optional<HistoryProcessInstanceDetailsDto> getHistoryProcessInstance(String businessKey, String processInstanceId);

    Long countIncidents();

    PageResultDto<IncidentDto> getIncidents(
        int page, int size, String businessKey, EnumIncidentSortField orderBy, EnumSortingOrder order
    );

}
