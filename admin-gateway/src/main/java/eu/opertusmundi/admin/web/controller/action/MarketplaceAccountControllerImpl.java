package eu.opertusmundi.admin.web.controller.action;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.admin.web.model.AdminMessageCode;
import eu.opertusmundi.admin.web.model.account.market.MarketplaceAccountSummaryDto;
import eu.opertusmundi.common.domain.AccountEntity;
import eu.opertusmundi.common.model.BasicMessageCode;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.account.AccountDto;
import eu.opertusmundi.common.model.account.helpdesk.EnumMarketplaceAccountSortField;
import eu.opertusmundi.common.model.account.helpdesk.ExternalProviderCommandDto;
import eu.opertusmundi.common.repository.AccountRepository;

@RestController
@Secured({ "ROLE_ADMIN" })
public class MarketplaceAccountControllerImpl extends BaseController implements MarketplaceAccountController {

	@Autowired
	private AccountRepository accountRepository;

	@Override
	public RestResponse<PageResultDto<MarketplaceAccountSummaryDto>> find(
		int page, int size, String name, EnumMarketplaceAccountSortField orderBy, EnumSortingOrder order
	) {
        return this.find(EnumAccountType.All, page, size, name, orderBy, order);
	}

    @Override
    public RestResponse<PageResultDto<MarketplaceAccountSummaryDto>> findConsumers(
        int page, int size, String name, EnumMarketplaceAccountSortField orderBy, EnumSortingOrder order
    ) {
        return this.find(EnumAccountType.Consumer, page, size, name, orderBy, order);
    }

    @Override
    public RestResponse<PageResultDto<MarketplaceAccountSummaryDto>> findProviders(
        int page, int size, String name, EnumMarketplaceAccountSortField orderBy, EnumSortingOrder order
    ) {
        return this.find(EnumAccountType.Provider, page, size, name, orderBy, order);
    }

    private RestResponse<PageResultDto<MarketplaceAccountSummaryDto>> find(
        EnumAccountType type, int page, int size, String name, EnumMarketplaceAccountSortField orderBy, EnumSortingOrder order
    ) {
        final Direction   direction   = order == EnumSortingOrder.DESC ? Direction.DESC : Direction.ASC;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, orderBy.getValue()));
        final String      param       = "%" + name + "%";

        Page<AccountDto> accounts;

        switch (type) {
            case Consumer :
                accounts = this.accountRepository.findAllConsumersObjects(param, pageRequest, true);
                break;
            case Provider :
                accounts = this.accountRepository.findAllProvidersObjects(param, pageRequest, true);
                break;
            default :
                accounts = this.accountRepository.findAllObjects(param, pageRequest, true);
                break;
        }

        final Page<MarketplaceAccountSummaryDto> p = accounts.map(MarketplaceAccountSummaryDto::from);

        final long count = p.getTotalElements();
        final List<MarketplaceAccountSummaryDto> records = p.stream().collect(Collectors.toList());
        final PageResultDto<MarketplaceAccountSummaryDto> result = PageResultDto.of(page, size, records, count);

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<AccountDto> findOne(UUID key) {
        final AccountDto account = this.accountRepository.findOneByKeyObject(key).orElse(null);

        if (account == null) {
            return RestResponse.failure(BasicMessageCode.RecordNotFound, "Account was not found");
        }

        return RestResponse.result(account);
    }
	
    public RestResponse<AccountDto> assignExternalProvider(UUID key, ExternalProviderCommandDto command) {
        command.setCustomerKey(key);
        command.setUserId(currentUserId());
        
        if (command.getProvider().getRequiredRole() != null) {
            final List<AccountEntity> existingAccounts = this.accountRepository.findAllWithRole(command.getProvider().getRequiredRole());
            if (existingAccounts.size() != 0) {
                return RestResponse.failure(
                    AdminMessageCode.ExternalProviderAlreadyExists,
                    "External provider is already assigned to another user"
                );
            }
        }

	    final AccountDto result = this.accountRepository.assignExternalProvider(command);
	    
	    return RestResponse.result(result);
	}

    private enum EnumAccountType {
        All, 
        Consumer, 
        Provider,
        ;
    }
}
