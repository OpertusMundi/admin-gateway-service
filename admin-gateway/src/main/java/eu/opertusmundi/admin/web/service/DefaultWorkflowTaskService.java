package eu.opertusmundi.admin.web.service;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.rest.dto.VariableValueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.opertusmundi.admin.web.model.workflow.CompleteTaskCommandDto;
import eu.opertusmundi.admin.web.model.workflow.ProcessInstanceDetailsDto;
import eu.opertusmundi.admin.web.model.workflow.SetPublishErrorTaskCommandDto;
import eu.opertusmundi.admin.web.model.workflow.TaskNameConstants;
import eu.opertusmundi.common.model.BasicMessageCode;
import eu.opertusmundi.common.model.ServiceException;
import eu.opertusmundi.common.model.workflow.EnumProcessInstanceVariable;
import eu.opertusmundi.common.repository.DraftRepository;
import eu.opertusmundi.common.repository.UserServiceRepository;
import eu.opertusmundi.common.util.BpmInstanceVariablesBuilder;
import io.jsonwebtoken.lang.Assert;

@Service
@Transactional
public class DefaultWorkflowTaskService implements WorkflowTaskService {
    
    @Autowired
    private DraftRepository draftRepository;
    
    @Autowired
    private UserServiceRepository userServiceRepository;
    
    @Autowired
    private BpmEngineService bpmEngineService;

    @Override
    public void completeTask(CompleteTaskCommandDto command) {
        try {
            switch (command.getTaskName()) {
                case TaskNameConstants.PUBLISH_CATALOGUE_ASSET_SET_ERROR :
                    this.publishAssetSetError((SetPublishErrorTaskCommandDto) command);
                    break;

                case TaskNameConstants.PUBLISH_USER_SERVICE_SET_ERROR :
                    this.publishUserServiceSetError((SetPublishErrorTaskCommandDto) command);
                    break;
            }
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(BasicMessageCode.BpmServiceError, "Failed to complete task");
        }
    }
    
    private void publishAssetSetError(SetPublishErrorTaskCommandDto command) {
        final String processInstanceId = command.getProcessInstanceId();
        final String taskName          = command.getTaskName();
        final String message           = command.getMessage();

        Assert.hasText(message, "Expected a non-empty error message");
        
        final ProcessInstanceDetailsDto instance = this.bpmEngineService.getProcessInstance(null, processInstanceId).orElse(null);
        if (instance == null) {
            throw new ServiceException(BasicMessageCode.BpmServiceError, "Process instance was not found");
        }

        final String draftKey     = getVariableAsString(instance, "draftKey");
        final String publisherKey = getVariableAsString(instance, "publisherKey");

        draftRepository.setErrorMessage(UUID.fromString(publisherKey), UUID.fromString(draftKey), message);

        final Map<String, VariableValueDto> variables = BpmInstanceVariablesBuilder.builder()
            .variableAsString(EnumProcessInstanceVariable.HELPDESK_ERROR_MESSAGE.getValue(), message)
            .build();
       
        this.bpmEngineService.completeTask(instance.getInstance().getBusinessKey(), taskName, variables);
    }
    
    private void publishUserServiceSetError(SetPublishErrorTaskCommandDto command) {
        final String processInstanceId = command.getProcessInstanceId();
        final String taskName          = command.getTaskName();
        final String message           = command.getMessage();

        Assert.hasText(message, "Expected a non-empty error message");
        
        final ProcessInstanceDetailsDto instance = this.bpmEngineService.getProcessInstance(null, processInstanceId).orElse(null);
        if (instance == null) {
            throw new ServiceException(BasicMessageCode.BpmServiceError, "Process instance was not found");
        }

        final String serviceKey = getVariableAsString(instance, "serviceKey");
        final String ownerKey   = getVariableAsString(instance, "ownerKey");

        userServiceRepository.setErrorMessage(UUID.fromString(ownerKey), UUID.fromString(serviceKey), message);

        final Map<String, VariableValueDto> variables = BpmInstanceVariablesBuilder.builder()
            .variableAsString(EnumProcessInstanceVariable.HELPDESK_ERROR_MESSAGE.getValue(), message)
            .build();
       
        this.bpmEngineService.completeTask(instance.getInstance().getBusinessKey(), taskName, variables);
    }
    
    private String getVariableAsString(ProcessInstanceDetailsDto instance, String name) {
        final String value = instance.getVariableAsString(name);

        if (StringUtils.isBlank(value)) {
            throw new ServiceException(BasicMessageCode.BpmServiceError, String.format(
                "Variable was not found in process instance [name=%s]", name
            ));
        }

        return value;
    }

}
