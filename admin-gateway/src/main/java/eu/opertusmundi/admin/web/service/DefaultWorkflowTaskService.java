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
import eu.opertusmundi.admin.web.model.workflow.SetErrorTaskCommandDto;
import eu.opertusmundi.admin.web.model.workflow.TaskNameConstants;
import eu.opertusmundi.common.model.BasicMessageCode;
import eu.opertusmundi.common.model.ServiceException;
import eu.opertusmundi.common.model.account.EnumCustomerType;
import eu.opertusmundi.common.model.workflow.EnumProcessInstanceVariable;
import eu.opertusmundi.common.repository.AccountRepository;
import eu.opertusmundi.common.repository.DraftRepository;
import eu.opertusmundi.common.repository.UserServiceRepository;
import eu.opertusmundi.common.util.BpmInstanceVariablesBuilder;
import io.jsonwebtoken.lang.Assert;

@Service
@Transactional
public class DefaultWorkflowTaskService implements WorkflowTaskService {

    @Autowired
    private AccountRepository accountRepository;

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
                case TaskNameConstants.CONSUMER_REGISTRATION_SET_ERROR :
                    this.customerRegistrationSetError((SetErrorTaskCommandDto) command, EnumCustomerType.CONSUMER);
                    break;

                case TaskNameConstants.PROVIDER_REGISTRATION_SET_ERROR :
                    this.customerRegistrationSetError((SetErrorTaskCommandDto) command, EnumCustomerType.PROVIDER);
                    break;

                case TaskNameConstants.PUBLISH_CATALOGUE_ASSET_SET_ERROR :
                    this.publishAssetSetError((SetErrorTaskCommandDto) command);
                    break;

                case TaskNameConstants.PUBLISH_USER_SERVICE_SET_ERROR :
                    this.publishUserServiceSetError((SetErrorTaskCommandDto) command);
                    break;
            }
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(BasicMessageCode.BpmServiceError, "Failed to complete task");
        }
    }

    private void customerRegistrationSetError(SetErrorTaskCommandDto command, EnumCustomerType type) {
        final String processInstanceId = command.getProcessInstanceId();
        final String taskName          = command.getTaskName();
        final String message           = command.getMessage();

        Assert.hasText(message, "Expected a non-empty error message");

        final ProcessInstanceDetailsDto instance = this.bpmEngineService.getProcessInstance(null, processInstanceId).orElse(null);
        if (instance == null) {
            throw new ServiceException(BasicMessageCode.BpmServiceError, "Process instance was not found");
        }

        final String userKey = getVariableAsString(instance, "userKey");

        switch (type) {
            case CONSUMER :
                accountRepository.setConsumerRegistrationErrorMessage(UUID.fromString(userKey), message);
                break;
            case PROVIDER :
                accountRepository.setProviderRegistrationErrorMessage(UUID.fromString(userKey), message);
                break;
        }

        final Map<String, VariableValueDto> variables = BpmInstanceVariablesBuilder.builder()
                .variableAsString(EnumProcessInstanceVariable.HELPDESK_ERROR_MESSAGE.getValue(), message)
                .variableAsString("cancelNotificationIdempotentKey", instance.getInstance().getBusinessKey() + "::" + UUID.randomUUID().toString())
                .build();

        this.bpmEngineService.completeTask(instance.getInstance().getBusinessKey(), taskName, variables);
    }

    private void publishAssetSetError(SetErrorTaskCommandDto command) {
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

        draftRepository.setErrorMessage(
            command.getHelpdeskUserKey(),
            UUID.fromString(publisherKey),
            UUID.fromString(draftKey),
            message
        );

        final Map<String, VariableValueDto> variables = BpmInstanceVariablesBuilder.builder()
            .variableAsString(EnumProcessInstanceVariable.HELPDESK_ERROR_MESSAGE.getValue(), message)
            .build();

        this.bpmEngineService.completeTask(instance.getInstance().getBusinessKey(), taskName, variables);
    }

    private void publishUserServiceSetError(SetErrorTaskCommandDto command) {
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

        userServiceRepository.setErrorMessage(
            command.getHelpdeskUserKey(),
            UUID.fromString(ownerKey),
            UUID.fromString(serviceKey),
            message
        );

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
