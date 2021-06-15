package eu.opertusmundi.admin.web.service;

import eu.opertusmundi.admin.web.model.workflow.EnumIncidentSortField;
import eu.opertusmundi.admin.web.model.workflow.EnumProcessInstanceSortField;
import eu.opertusmundi.admin.web.model.workflow.IncidentDto;
import eu.opertusmundi.admin.web.model.workflow.ProcessInstanceDto;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;

public interface BpmEngineService {

    Long countProcessInstances();
    
    PageResultDto<ProcessInstanceDto> getRunningProcessInstances(
        int page, int size, String businessKey, EnumProcessInstanceSortField orderBy, EnumSortingOrder order
    );

    Long countIncidents();

    PageResultDto<IncidentDto> getIncidents(
        int page, int size, String businessKey, EnumIncidentSortField orderBy, EnumSortingOrder order
    );

}
