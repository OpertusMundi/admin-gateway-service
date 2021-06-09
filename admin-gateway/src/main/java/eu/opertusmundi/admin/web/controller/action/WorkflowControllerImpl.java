package eu.opertusmundi.admin.web.controller.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.admin.web.model.workflow.EnumIncidentSortField;
import eu.opertusmundi.admin.web.model.workflow.EnumProcessInstanceSortField;
import eu.opertusmundi.admin.web.model.workflow.IncidentDto;
import eu.opertusmundi.admin.web.model.workflow.ProcessInstanceDto;
import eu.opertusmundi.admin.web.service.BpmEngineService;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;

@RestController
public class WorkflowControllerImpl implements WorkflowController {

    @Autowired
    private BpmEngineService bpmEngineService;

    @Override
    public RestResponse<?> getInstances(
        Integer page, Integer size, String businessKey, EnumProcessInstanceSortField orderBy, EnumSortingOrder order
    ) {               
        final PageResultDto<ProcessInstanceDto> result = this.bpmEngineService.getRunningProcessInstances(
            page, size, businessKey, orderBy, order
        );
        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> countIncidents() {
        final Long result = this.bpmEngineService.countIncidents();

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> getIncidents(
        Integer page, Integer size, String processInstanceId, EnumIncidentSortField orderBy, EnumSortingOrder order
    ) {
        final PageResultDto<IncidentDto> result = this.bpmEngineService.getIncidents(
            page, size, processInstanceId, orderBy, order
        );

        return RestResponse.result(result);
    }

}