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

import eu.opertusmundi.common.model.account.helpdesk.EnumHelpdeskAccountSortField;
import eu.opertusmundi.admin.web.model.account.market.MarketplaceAccountSummaryDto;
import eu.opertusmundi.common.domain.AccountEntity;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.account.AccountDto;
import eu.opertusmundi.common.repository.AccountRepository;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_USER" })
public class MarketplaceAccountControllerImpl extends BaseController implements MarketplaceAccountController {

	@Autowired
	private AccountRepository accountRepository;

	@Override
	public RestResponse<PageResultDto<MarketplaceAccountSummaryDto>> find(
		int page, int size, String name, EnumHelpdeskAccountSortField orderBy, EnumSortingOrder order
	) {
        return this.find(EnumAccountType.All, page, size, name, orderBy, order);
	}

    @Override
    public RestResponse<PageResultDto<MarketplaceAccountSummaryDto>> findConsumers(
        int page, int size, String name, EnumHelpdeskAccountSortField orderBy, EnumSortingOrder order
    ) {
        return this.find(EnumAccountType.Consumer, page, size, name, orderBy, order);
    }

    @Override
    public RestResponse<PageResultDto<MarketplaceAccountSummaryDto>> findProviders(
        int page, int size, String name, EnumHelpdeskAccountSortField orderBy, EnumSortingOrder order
    ) {
        return this.find(EnumAccountType.Provider, page, size, name, orderBy, order);
    }

    private RestResponse<PageResultDto<MarketplaceAccountSummaryDto>> find(
        EnumAccountType type, int page, int size, String name, EnumHelpdeskAccountSortField orderBy, EnumSortingOrder order
    ) {
        final Direction   direction   = order == EnumSortingOrder.DESC ? Direction.DESC : Direction.ASC;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, orderBy.getValue()));
        final String      param       = "%" + name + "%";

        Page<AccountEntity> entities;

        switch (type) {
            case Consumer :
                entities = this.accountRepository.findAllConsumers(param, pageRequest);
                break;
            case Provider :
                entities = this.accountRepository.findAllProviders(param, pageRequest);
                break;
            default :
                entities = this.accountRepository.findAll(param, pageRequest);
                break;
        }

        final Page<MarketplaceAccountSummaryDto> p = entities
            .map(AccountEntity::toDto)
            .map(MarketplaceAccountSummaryDto::from);

        final long count = p.getTotalElements();
        final List<MarketplaceAccountSummaryDto> records = p.stream().collect(Collectors.toList());
        final PageResultDto<MarketplaceAccountSummaryDto> result = PageResultDto.of(page, size, records, count);

        return RestResponse.result(result);
    }

	@Override
	public RestResponse<AccountDto> findOne(UUID key) {
		final AccountEntity e = this.accountRepository.findOneByKey(key).orElse(null);

		if (e == null) {
			return RestResponse.notFound();
		}


		return RestResponse.result(e.toDto());
	}

    private enum EnumAccountType {
        All, 
        Consumer, 
        Provider,
        ;
    }
}
