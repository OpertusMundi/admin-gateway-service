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
import eu.opertusmundi.admin.web.service.MarketplaceUserService;
import eu.opertusmundi.common.domain.AccountEntity;
import eu.opertusmundi.common.model.BasicMessageCode;
import eu.opertusmundi.common.model.EnumAccountType;
import eu.opertusmundi.common.model.EnumRole;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.ServiceException;
import eu.opertusmundi.common.model.account.AccountDto;
import eu.opertusmundi.common.model.account.helpdesk.EnumMarketplaceAccountSortField;
import eu.opertusmundi.common.model.account.helpdesk.ExternalProviderCommandDto;
import eu.opertusmundi.common.repository.AccountRepository;
import eu.opertusmundi.common.service.mangopay.CustomerVerificationService;
import eu.opertusmundi.common.service.mangopay.WalletService;

@RestController
@Secured({ "ROLE_ADMIN" })
public class MarketplaceAccountControllerImpl extends BaseController implements MarketplaceAccountController {

	@Autowired
	private AccountRepository accountRepository;

    @Autowired
    private CustomerVerificationService customerVerificationService;

    @Autowired
    private MarketplaceUserService marketplaceUserService;

    @Autowired
    private WalletService walletService;

	@Override
	public RestResponse<PageResultDto<MarketplaceAccountSummaryDto>> find(
		int page, int size, String name, EnumMarketplaceAccountSortField orderBy, EnumSortingOrder order
	) {
        return this.find(EnumAccountSelection.All, page, size, name, orderBy, order);
	}

    @Override
    public RestResponse<PageResultDto<MarketplaceAccountSummaryDto>> findConsumers(
        int page, int size, String name, EnumMarketplaceAccountSortField orderBy, EnumSortingOrder order
    ) {
        return this.find(EnumAccountSelection.Consumer, page, size, name, orderBy, order);
    }

    @Override
    public RestResponse<PageResultDto<MarketplaceAccountSummaryDto>> findProviders(
        int page, int size, String name, EnumMarketplaceAccountSortField orderBy, EnumSortingOrder order
    ) {
        return this.find(EnumAccountSelection.Provider, page, size, name, orderBy, order);
    }

    private RestResponse<PageResultDto<MarketplaceAccountSummaryDto>> find(
            EnumAccountSelection type, int page, int size, String name, EnumMarketplaceAccountSortField orderBy, EnumSortingOrder order
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

        final AccountDto parent = account.getType() == EnumAccountType.VENDOR
            ? this.accountRepository.findOneByKeyObject(account.getParentKey()).orElse(null)
            : null;

        account.setParent(parent);

        return RestResponse.result(account);
    }

    @Override
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

    @Override
    public RestResponse<AccountDto> grantOpenDatasetProvider(UUID key) {
        final List<AccountEntity> existingAccounts = this.accountRepository.findAllWithRole(EnumRole.ROLE_PROVIDER_OPEN_DATASET);
        if (existingAccounts.size() != 0) {
            return RestResponse.failure(
                AdminMessageCode.OpenDatasetProviderAlreadyExists,
                "Open Dataset provider is already assigned to another user"
            );
        }

        final AccountDto result = this.accountRepository.grantOpenDatasetProvider(key);

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<AccountDto> revokeOpenDatasetProvider(UUID key) {
        final AccountDto result = this.accountRepository.revokeOpenDatasetProvider(key);

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<AccountDto> refreshCustomerKycLevel(UUID key) {
        final AccountDto result = this.customerVerificationService.refreshCustomerKycLevel(key);

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<AccountDto> refreshCustomerWalletFunds(UUID key) {
        final var result = this.walletService.refreshUserWallets(key);

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<AccountDto> toggleTesterStatus(UUID key) {
        try {
            final AccountDto account = this.marketplaceUserService.toggleTesterStatus(this.currentUserKey(), key);

            return RestResponse.result(account);
        } catch (ServiceException ex) {
            return RestResponse.failure(ex);
        }
    }

    @Override
    public RestResponse<Void> delete(UUID key, boolean accountDeleted, boolean fileSystemDeleted, boolean contractsDeleted) {
        try {
            this.marketplaceUserService.delete(this.currentUserKey(), key, accountDeleted, fileSystemDeleted, contractsDeleted);

            return RestResponse.success();
        } catch (ServiceException ex) {
            return RestResponse.failure(ex);
        }
    }

    private enum EnumAccountSelection {
        All,
        Consumer,
        Provider,
        ;
    }
}
