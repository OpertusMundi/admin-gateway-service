package eu.opertusmundi.admin.web.controller.action;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import eu.opertusmundi.admin.web.model.account.helpdesk.EnumHelpdeskAccountSortField;
import eu.opertusmundi.admin.web.model.account.market.MarketplaceAccountSummaryDto;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.account.AccountDto;

public interface MarketplaceAccountController {

	@GetMapping(value = { "/action/marketplace/accounts" })
	RestResponse<PageResultDto<MarketplaceAccountSummaryDto>> find(
		@RequestParam(name = "page", defaultValue = "0") int page,
		@RequestParam(name = "size", defaultValue = "25") @Max(100) @Min(1) int size,
		@RequestParam(name = "name", defaultValue = "") String name,
		@RequestParam(name = "orderBy", defaultValue = "EMAIL") EnumHelpdeskAccountSortField orderBy,
		@RequestParam(name = "order", defaultValue = "ASC") EnumSortingOrder order
	);

	@GetMapping(value = { "/action/marketplace/accounts/{id}" })
	RestResponse<AccountDto> findOne(@PathVariable int id);

}