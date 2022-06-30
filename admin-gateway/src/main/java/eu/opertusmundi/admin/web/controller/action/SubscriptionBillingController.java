package eu.opertusmundi.admin.web.controller.action;

import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.payment.EnumSubscriptionBillingBatchSortField;
import eu.opertusmundi.common.model.payment.EnumSubscriptionBillingBatchStatus;
import eu.opertusmundi.common.model.payment.SubscriptionBillingBatchCommandDto;
import eu.opertusmundi.common.model.payment.SubscriptionBillingBatchDto;

@RequestMapping(value = "/action/subscription-billing", produces = MediaType.APPLICATION_JSON_VALUE)
public interface SubscriptionBillingController {

    @GetMapping(value = { "/quotations" })
    RestResponse<PageResultDto<SubscriptionBillingBatchDto>> findAll(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "25") @Max(100) @Min(1) int size,
        @RequestParam(name = "status", required = false) Set<EnumSubscriptionBillingBatchStatus> status,
        @RequestParam(name = "orderBy", defaultValue = "UPDATED_ON") EnumSubscriptionBillingBatchSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

    @GetMapping(value = { "/quotations/{key}" })
    RestResponse<SubscriptionBillingBatchDto> findOne(
        @PathVariable(name = "key") UUID key
    );

    @PostMapping(value = { "/quotations" })
    @Validated
    RestResponse<SubscriptionBillingBatchDto> create(
        @RequestBody @Valid SubscriptionBillingBatchCommandDto command,
        BindingResult validationResult
    );

}
