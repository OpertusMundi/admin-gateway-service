package eu.opertusmundi.admin.web.service;

import java.util.Map;
import java.util.UUID;

import org.camunda.bpm.engine.rest.dto.VariableValueDto;
import org.camunda.bpm.engine.rest.dto.runtime.ProcessInstanceDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.opertusmundi.admin.web.model.AdminMessageCode;
import eu.opertusmundi.common.domain.AccountEntity;
import eu.opertusmundi.common.model.EnumRole;
import eu.opertusmundi.common.model.ServiceException;
import eu.opertusmundi.common.model.account.AccountDto;
import eu.opertusmundi.common.model.account.EnumAccountActiveTask;
import eu.opertusmundi.common.model.workflow.EnumProcessInstanceVariable;
import eu.opertusmundi.common.model.workflow.EnumWorkflow;
import eu.opertusmundi.common.repository.AccountRepository;
import eu.opertusmundi.common.util.BpmEngineUtils;
import eu.opertusmundi.common.util.BpmInstanceVariablesBuilder;

@Service
public class DefaultMarketplaceUserService implements MarketplaceUserService {

    private final AccountRepository accountRepository;
    private final BpmEngineUtils    bpmEngine;

    public DefaultMarketplaceUserService(AccountRepository accountRepository, BpmEngineUtils bpmEngine) {
        this.accountRepository = accountRepository;
        this.bpmEngine         = bpmEngine;
    }

    @Override
    @Transactional
    public AccountDto toggleTesterStatus(UUID adminUserKey, UUID userKey) {
        final EnumRole role = EnumRole.ROLE_TESTER;

        // Validate account
        final AccountEntity admin = this.accountRepository.findOneByKey(adminUserKey).orElse(null);
        final AccountEntity user  = this.accountRepository.findOneByKey(userKey).orElse(null);

        if (user == null) {
            throw new ServiceException(AdminMessageCode.AccountNotFound, "Account was not found");
        }

        if (user.hasRole(role)) {
            user.revoke(role);
        } else {
            user.grant(role, admin);
        }

        final AccountDto result = this.accountRepository.saveAndFlush(user).toDto();
        return result;
    }

    @Override
    @Transactional
    public void delete(
        UUID startUserKey, UUID deletedUserKey, boolean accountDeleted, boolean fileSystemDeleted, boolean contractsDeleted
    ) throws ServiceException {
        // Validate account
        final AccountEntity account = this.accountRepository.findOneByKey(deletedUserKey).orElse(null);

        if (account == null) {
            throw new ServiceException(AdminMessageCode.AccountNotFound, "Account was not found");
        }
        if (!account.hasRole(EnumRole.ROLE_TESTER)) {
            throw new ServiceException(AdminMessageCode.AccountIsNotTester, "Only accounts with role TESTER can be deleted");
        }

        // Mark account as deleted
        account.setActiveTask(accountDeleted ? EnumAccountActiveTask.DELETE : EnumAccountActiveTask.DATA_RESET);
        this.accountRepository.saveAndFlush(account);

        final String       businessKey = deletedUserKey.toString() + "::DELETE";
        ProcessInstanceDto instance    = this.bpmEngine.findInstance(businessKey);

        if (instance == null) {
            final Map<String, VariableValueDto> variables = BpmInstanceVariablesBuilder.builder()
                .variableAsString(EnumProcessInstanceVariable.START_USER_KEY.getValue(), startUserKey.toString())
                .variableAsInteger("userId", account.getId())
                .variableAsString("userKey", account.getKey().toString())
                .variableAsString("userParentKey", account.getParentKey().toString())
                .variableAsString("userName", account.getEmail())
                .variableAsString("userType", account.getType().toString())
                .variableAsString("userGeodataShard", account.getProfile().getGeodataShard())
                .variableAsBoolean("accountDeleted", accountDeleted)
                .variableAsBoolean("fileSystemDeleted", fileSystemDeleted)
                .variableAsBoolean("contractsDeleted", contractsDeleted)
                .build();

            instance = this.bpmEngine.startProcessDefinitionByKey(EnumWorkflow.SYSTEM_MAINTENANCE_DELETE_USER, businessKey, variables);
        }
    }

}
