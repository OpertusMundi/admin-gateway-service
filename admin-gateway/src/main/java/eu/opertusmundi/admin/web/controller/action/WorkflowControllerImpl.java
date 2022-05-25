package eu.opertusmundi.admin.web.controller.action;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.admin.web.model.workflow.CompleteTaskTaskCommandDto;
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
import eu.opertusmundi.admin.web.service.BpmEngineService;
import eu.opertusmundi.admin.web.service.WorkflowTaskService;
import eu.opertusmundi.common.model.BaseResponse;
import eu.opertusmundi.common.model.BasicMessageCode;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.ServiceException;

@RestController
public class WorkflowControllerImpl implements WorkflowController {

    @Autowired
    private BpmEngineService bpmEngineService;

    @Autowired
    private WorkflowTaskService workflowTaskService;

    @Override
    public RestResponse<?> getProcessDefinitions() {
        final List<ProcessDefinitionHeaderDto> result = this.bpmEngineService.getProcessDefinitions();

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> countProcessInstances() {
        final Long result = this.bpmEngineService.countProcessInstances();

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> getProcessInstances(
        Integer page, Integer size,
        String processDefinitionKey, String businessKey, String task,
        EnumProcessInstanceSortField orderBy, EnumSortingOrder order
    ) {
        final PageResultDto<ProcessInstanceDto> result = this.bpmEngineService.getProcessInstances(
            page, size, processDefinitionKey, businessKey, task, orderBy, order
        );
        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> countProcessInstanceTasks() {
        final Long result = this.bpmEngineService.countProcessInstanceTasks();

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> getProcessInstanceTasks(
        Integer page, Integer size,
        String processDefinitionKey, String businessKey, String task,
        EnumProcessInstanceTaskSortField orderBy, EnumSortingOrder order
    ) {
        final PageResultDto<ProcessInstanceTaskDto> result = this.bpmEngineService.getProcessInstanceTasks(
            page, size, processDefinitionKey, businessKey, task, orderBy, order
        );
        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> getProcessInstance(String processInstanceId) {
        final Optional<ProcessInstanceDetailsDto> result = this.bpmEngineService.getProcessInstance(null, processInstanceId);

        if (result.isPresent()) {
            return RestResponse.result(result.get());
        }

        return RestResponse.notFound();
    }

    @Override
    public RestResponse<?> getProcessInstanceByBusinessKey(String businessKey) {
        final Optional<ProcessInstanceDetailsDto> result = this.bpmEngineService.getProcessInstance(businessKey, null);

        if (result.isPresent()) {
            return RestResponse.result(result.get());
        }

        return RestResponse.notFound();
    }

    @Override
    public RestResponse<?> getHistoryProcessInstances(
        Integer page, Integer size,
        String processDefinitionKey, String businessKey,
        EnumProcessInstanceHistorySortField orderBy, EnumSortingOrder order
    ) {
        final PageResultDto<ProcessInstanceDto> result = this.bpmEngineService.getHistoryProcessInstances(
            page, size, processDefinitionKey, businessKey, orderBy, order
        );
        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> getHistoryProcessInstance(String processInstanceId) {
        final Optional<HistoryProcessInstanceDetailsDto> result = this.bpmEngineService.getHistoryProcessInstance(null, processInstanceId);

        if (result.isPresent()) {
            return RestResponse.result(result.get());
        }

        return RestResponse.notFound();
    }

    @Override
    public RestResponse<?> getHistoryProcessInstanceByBusinessKey(String businessKey) {
        final Optional<HistoryProcessInstanceDetailsDto> result = this.bpmEngineService.getHistoryProcessInstance(businessKey, null);

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
            if (validationResult.hasErrors()) {
                return RestResponse.invalid(validationResult.getFieldErrors());
            }

            this.bpmEngineService.retryExternalTask(command);

            return RestResponse.success();
        } catch (Exception ex) {
            return RestResponse.failure(BasicMessageCode.InternalServerError, ex.getMessage());
        }
    }

    public BaseResponse completeTask(String processInstanceId, CompleteTaskTaskCommandDto command, BindingResult validationResult) {
        try {
            command.setProcessInstanceId(processInstanceId);

            if (validationResult.hasErrors()) {
                return RestResponse.invalid(validationResult.getFieldErrors());
            }

            this.workflowTaskService.completeTask(command);

            return RestResponse.success();
        } catch (ServiceException ex) {
            return RestResponse.failure(ex);
        }
    }

    @Override
    public BaseResponse deleteProcessInstance(String processInstanceId) {
        try {
            this.bpmEngineService.deleteProcessInstance(processInstanceId);

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