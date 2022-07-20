package eu.opertusmundi.admin.web.controller.action;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.camunda.bpm.engine.rest.dto.VariableValueDto;
import org.camunda.bpm.engine.rest.dto.runtime.ProcessInstanceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.admin.web.model.workflow.BpmnMessageCode;
import eu.opertusmundi.admin.web.model.workflow.MaintenanceTasksCommandDto;
import eu.opertusmundi.common.model.BaseResponse;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.workflow.EnumProcessInstanceVariable;
import eu.opertusmundi.common.model.workflow.EnumWorkflow;
import eu.opertusmundi.common.util.BpmEngineUtils;
import eu.opertusmundi.common.util.BpmInstanceVariablesBuilder;

@RestController
@Secured({"ROLE_ADMIN"})
public class MaintenanceTasksControllerImpl extends BaseController implements MaintenanceTasksController {

    private final BpmEngineUtils bpmEngineUtils;

    @Autowired
    public MaintenanceTasksControllerImpl(BpmEngineUtils bpmEngineUtils) {
        this.bpmEngineUtils = bpmEngineUtils;
    }

    @Override
    public BaseResponse startTasks(MaintenanceTasksCommandDto command) {
        final List<ProcessInstanceDto> instances = this.bpmEngineUtils
            .findInstancesByProcessDefinitionKey(EnumWorkflow.SYSTEM_MAINTENANCE.getKey());
        
        if(!instances.isEmpty() ) {
            return RestResponse.error(BpmnMessageCode.ProcessInstanceAlreadyExists, "Process instances is already running");
        }

        final String                        businessKey = UUID.randomUUID().toString();
        final Map<String, VariableValueDto> variables   = BpmInstanceVariablesBuilder.builder()
            .variableAsString(EnumProcessInstanceVariable.START_USER_KEY.getValue(), this.currentUserKey().toString())
            .variableAsString("deleteOrphanFileSystemEntries", command.getDeleteOrphanFileSystemEntries().toString())
            .variableAsString("removeOrphanCatalogueItems", command.getRemoveOrphanCatalogueItems().toString())
            .variableAsString("resizeImages", command.getResizeImages().toString())
            .build();

        this.bpmEngineUtils.startProcessDefinitionByKey(EnumWorkflow.SYSTEM_MAINTENANCE, businessKey, variables);

        return RestResponse.success();
    }

}
