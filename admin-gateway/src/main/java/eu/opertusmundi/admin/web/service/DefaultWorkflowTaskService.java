package eu.opertusmundi.admin.web.service;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.rest.dto.VariableValueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.opertusmundi.admin.web.model.workflow.CompleteTaskTaskCommandDto;
import eu.opertusmundi.admin.web.model.workflow.ProcessInstanceDetailsDto;
import eu.opertusmundi.admin.web.model.workflow.SetPublishErrorTaskCommandDto;
import eu.opertusmundi.admin.web.model.workflow.TaskNameConstants;
import eu.opertusmundi.common.model.BasicMessageCode;
import eu.opertusmundi.common.model.ServiceException;
import eu.opertusmundi.common.model.workflow.EnumProcessInstanceVariable;
import eu.opertusmundi.common.repository.DraftRepository;
import eu.opertusmundi.common.util.BpmInstanceVariablesBuilder;
import io.jsonwebtoken.lang.Assert;

@Service
@Transactional
public class DefaultWorkflowTaskService implements WorkflowTaskService {
    
    @Autowired
    private DraftRepository draftRepository;
    
    @Autowired
    private BpmEngineService bpmEngineService;

    @Override
    public void completeTask(CompleteTaskTaskCommandDto command) {
        try {
            switch (command.getTaskName()) {
                case TaskNameConstants.PUBLISH_SET_ERROR_TASK : {
                    this.completePublishSetErrorTask((SetPublishErrorTaskCommandDto) command);
                }
            }
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(BasicMessageCode.BpmServiceError, "Failed to complete task");
        }
    }
    
    private void completePublishSetErrorTask(SetPublishErrorTaskCommandDto command) {
        final String processInstanceId = command.getProcessInstanceId();
        final String taskName          = command.getTaskName();
        final String message           = command.getMessage();

        Assert.hasText(message, "Expected a non-empty error message");
        
        final ProcessInstanceDetailsDto instance = this.bpmEngineService.getProcessInstance(null, processInstanceId).orElse(null);
        if (instance == null) {
            throw new ServiceException(BasicMessageCode.BpmServiceError, "Process instance was not found");
        }

        final String draftKey     = instance.getVariableAsString("draftKey");
        final String publisherKey = instance.getVariableAsString("publisherKey");

        if (StringUtils.isBlank(draftKey)) {
            throw new ServiceException(BasicMessageCode.BpmServiceError, "Draft key variable was not found in process instance");
        }
        if (StringUtils.isBlank(publisherKey)) {
            throw new ServiceException(BasicMessageCode.BpmServiceError, "Publisher key variable was not found in process instance");
        }

        draftRepository.setErrorMessage(UUID.fromString(publisherKey), UUID.fromString(draftKey), message);

        final Map<String, VariableValueDto> variables = BpmInstanceVariablesBuilder.builder()
            .variableAsString(EnumProcessInstanceVariable.HELPDESK_ERROR_MESSAGE.getValue(), message)
            .build();
       
        this.bpmEngineService.completeTask(instance.getInstance().getBusinessKey(), taskName, variables);
    }

}
