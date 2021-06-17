package eu.opertusmundi.admin.web.controller.action;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.admin.web.model.workflow.EnumIncidentSortField;
import eu.opertusmundi.admin.web.model.workflow.EnumProcessInstanceSortField;
import eu.opertusmundi.admin.web.model.workflow.IncidentDto;
import eu.opertusmundi.admin.web.model.workflow.ProcessInstanceDetailsDto;
import eu.opertusmundi.admin.web.model.workflow.ProcessInstanceDto;
import eu.opertusmundi.admin.web.model.workflow.RetryExternalTaskCommandDto;
import eu.opertusmundi.admin.web.service.BpmEngineService;
import eu.opertusmundi.common.model.BasicMessageCode;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;

@RestController
public class WorkflowControllerImpl implements WorkflowController {

    @Autowired
    private BpmEngineService bpmEngineService;


    @Override
    public RestResponse<?> countProcessInstances() {
        final Long result = this.bpmEngineService.countProcessInstances();

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> getProcessInstances(
        Integer page, Integer size, String businessKey, EnumProcessInstanceSortField orderBy, EnumSortingOrder order
    ) {
        final PageResultDto<ProcessInstanceDto> result = this.bpmEngineService.getProcessInstances(
            page, size, businessKey, orderBy, order
        );
        return RestResponse.result(result);
    }

    public RestResponse<?> getProcessInstance(String processInstanceId) {
        final Optional<ProcessInstanceDetailsDto> result = this.bpmEngineService.getProcessInstance(processInstanceId);

        if (result.isPresent()) {
            return RestResponse.result(result.get());
        }

        return RestResponse.notFound();
    }

    @Override
    public RestResponse<?> retryExternalTask(
        String processInstanceId, RetryExternalTaskCommandDto command, BindingResult validationResult
    ) {
        try {
            this.bpmEngineService.retryExternalTask(command);

            return RestResponse.success();
        } catch (Exception ex) {
            return RestResponse.failure(BasicMessageCode.InternalServerError, ex.getMessage());
        }
    }

    @Override
    public RestResponse<?> countIncidents() {
        final Long result = this.bpmEngineService.countIncidents();

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> getIncidents(
        Integer page, Integer size, String businessKey, EnumIncidentSortField orderBy, EnumSortingOrder order
    ) {
        final PageResultDto<IncidentDto> result = this.bpmEngineService.getIncidents(
            page, size, businessKey, orderBy, order
        );

        return RestResponse.result(result);
    }

}