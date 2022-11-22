package eu.opertusmundi.admin.web.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.opertusmundi.admin.web.model.workflow.ProcessInstanceResource;
import eu.opertusmundi.admin.web.model.workflow.VariableDto;
import eu.opertusmundi.common.model.account.AccountDto;
import eu.opertusmundi.common.model.asset.AssetDraftDto;
import eu.opertusmundi.common.model.asset.service.UserServiceDto;
import eu.opertusmundi.common.model.catalogue.client.CatalogueItemDetailsDto;
import eu.opertusmundi.common.model.order.HelpdeskOrderDto;
import eu.opertusmundi.common.model.payment.PayOutDto;
import eu.opertusmundi.common.model.payment.helpdesk.HelpdeskPayInDto;
import eu.opertusmundi.common.model.workflow.EnumProcessInstanceResource;
import eu.opertusmundi.common.model.workflow.EnumWorkflow;
import eu.opertusmundi.common.repository.AccountRepository;
import eu.opertusmundi.common.repository.OrderRepository;
import eu.opertusmundi.common.repository.PayInRepository;
import eu.opertusmundi.common.repository.PayOutRepository;
import eu.opertusmundi.common.service.CatalogueService;
import eu.opertusmundi.common.service.ProviderAssetService;
import eu.opertusmundi.common.service.UserServiceService;

@Service
public class DefaultProcessInstanceResourceResolver implements ProcessInstanceResourceResolver {

    private final AccountRepository    accountRepository;
    private final CatalogueService     catalogueService;
    private final OrderRepository      orderRepository;
    private final PayInRepository      payInRepository;
    private final PayOutRepository     payOutRepository;
    private final ProviderAssetService providerAssetService;
    private final UserServiceService   userServiceService;

    @Autowired
    public DefaultProcessInstanceResourceResolver(
        AccountRepository    accountRepository,
        CatalogueService     catalogueService,
        OrderRepository      orderRepository,
        PayInRepository      payInRepository,
        PayOutRepository     payOutRepository,
        ProviderAssetService providerAssetService,
        UserServiceService   userServiceService
    ) {
        this.accountRepository    = accountRepository;
        this.catalogueService     = catalogueService;
        this.orderRepository      = orderRepository;
        this.payInRepository      = payInRepository;
        this.payOutRepository     = payOutRepository;
        this.providerAssetService = providerAssetService;
        this.userServiceService   = userServiceService;
    }

    @Override
    public ProcessInstanceResource resolve(String workflowKey, String businessKey, List<VariableDto> variables) {
        final EnumWorkflow workflow = EnumWorkflow.fromKey(workflowKey);
        if (workflow == null) {
            return null;
        }
        final EnumProcessInstanceResource resourceType = workflow.getResourceType();
        if (resourceType == null) {
            return null;
        }
        return switch (resourceType) {
            case ACCOUNT -> resolveAccount(workflow, businessKey, variables);
            case ASSET -> resolveCatalogueAsset(workflow, businessKey, variables);
            case CONSUMER, PROVIDER -> resolveConsumerRegistration(workflow, businessKey, variables);
            case DRAFT -> resolveDraft(workflow, businessKey, variables);
            case ORDER -> resolveOrder(workflow, businessKey, variables);
            case PAYIN -> resolvePayIn(workflow, businessKey, variables);
            case PAYOUT -> resolvePayOut(workflow, businessKey, variables);
            case USER_SERVICE -> resolveUserService(workflow, businessKey, variables);
            default -> null;
        };
    }

    private ProcessInstanceResource resolveAccount(EnumWorkflow workflow, String businessKey, List<VariableDto> variables) {
        final String accountKeyAsString = switch(workflow) {
            case SYSTEM_MAINTENANCE_DELETE_USER -> businessKey.split("::")[0];
            default -> businessKey;
        };
        final UUID       accountKey = UUID.fromString(accountKeyAsString);
        final AccountDto resource   = this.accountRepository.findOneByKeyObject(accountKey).orElse(null);
        if (resource == null) {
            // Maintenance task for deleting a user may have already removed the
            // resource
            return null;
        }
        return ProcessInstanceResource.of(workflow.getResourceType(), resource);
    }
    
    private ProcessInstanceResource resolveConsumerRegistration(EnumWorkflow workflow, String businessKey, List<VariableDto> variables) {
        final String userKeyVariableValue = variables.stream()
            .filter(v -> v.getName().equalsIgnoreCase("userKey"))
            .map(v -> v.getValue().toString())
            .findFirst()
            .orElse(null);

        final UUID       accountKey = UUID.fromString(userKeyVariableValue);
        final AccountDto resource   = this.accountRepository.findOneByKeyObject(accountKey).orElse(null);

        return ProcessInstanceResource.of(EnumProcessInstanceResource.ACCOUNT, resource);
    }

    private ProcessInstanceResource resolveCatalogueAsset(EnumWorkflow workflow, String businessKey, List<VariableDto> variables) {
        final String                  pid  = businessKey.split(":")[1];
        final CatalogueItemDetailsDto item = this.catalogueService.findOne(null, pid, null, true);

        return ProcessInstanceResource.of(workflow.getResourceType(), item);
    }

    private ProcessInstanceResource resolveDraft(EnumWorkflow workflow, String businessKey, List<VariableDto> variables) {
        final UUID          draftKey = UUID.fromString(businessKey);
        final AssetDraftDto resource = this.providerAssetService.findOneDraft(draftKey);

        return ProcessInstanceResource.of(workflow.getResourceType(), resource);
    }
    
    private ProcessInstanceResource resolveOrder(EnumWorkflow workflow, String businessKey, List<VariableDto> variables) {
        final UUID             orderKey = UUID.fromString(businessKey);
        final HelpdeskOrderDto resource = this.orderRepository.findOrderObjectByKey(orderKey).orElse(null);

        return ProcessInstanceResource.of(workflow.getResourceType(), resource);
    }

    private ProcessInstanceResource resolvePayIn(EnumWorkflow workflow, String businessKey, List<VariableDto> variables) {
        final UUID             payInKey = UUID.fromString(businessKey);
        final HelpdeskPayInDto resource = this.payInRepository.findOneObjectByKey(payInKey).orElse(null);

        return ProcessInstanceResource.of(workflow.getResourceType(), resource);
    }
    
    private ProcessInstanceResource resolvePayOut(EnumWorkflow workflow, String businessKey, List<VariableDto> variables) {
        final UUID      payOutKey = UUID.fromString(businessKey);
        final PayOutDto resource  = this.payOutRepository.findOneObjectByKey(payOutKey, true).orElse(null);

        return ProcessInstanceResource.of(workflow.getResourceType(), resource);
    }
    
    private ProcessInstanceResource resolveUserService(EnumWorkflow workflow, String businessKey, List<VariableDto> variables) {
        final String serviceKeyAsString = switch(workflow) {
            case PUBLISH_USER_SERVICE -> businessKey;
            case REMOVE_USER_SERVICE -> businessKey.split("::")[0];
            default -> null;
        };
        if (serviceKeyAsString == null) {
            return null;
        }

        final UUID           serviceKey = UUID.fromString(serviceKeyAsString);
        final UserServiceDto resource   = this.userServiceService.findOne(serviceKey);

        return ProcessInstanceResource.of(workflow.getResourceType(), resource);
    }

}
