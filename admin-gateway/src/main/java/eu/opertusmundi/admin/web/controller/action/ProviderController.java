package eu.opertusmundi.admin.web.controller.action;

import java.util.Set;
import java.util.UUID;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.order.EnumOrderSortField;
import eu.opertusmundi.common.model.order.EnumOrderStatus;
import eu.opertusmundi.common.model.order.OrderDto;
import eu.opertusmundi.common.model.payment.EnumPayInSortField;
import eu.opertusmundi.common.model.payment.EnumPayOutSortField;
import eu.opertusmundi.common.model.payment.EnumTransactionStatus;
import eu.opertusmundi.common.model.payment.EnumTransferSortField;
import eu.opertusmundi.common.model.payment.PayInItemDto;
import eu.opertusmundi.common.model.payment.PayOutDto;
import eu.opertusmundi.common.model.payment.helpdesk.HelpdeskPayInItemDto;

@RequestMapping(value = "/action/billing/provider", produces = MediaType.APPLICATION_JSON_VALUE)
public interface ProviderController {

    @GetMapping(value = { "/orders" })
    RestResponse<PageResultDto<OrderDto>> findOrders(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "25") @Max(100) @Min(1) int size,
        @RequestParam(name = "providerKey", required = true) UUID providerKey,
        @RequestParam(name = "referenceNumber", required = false, defaultValue = "") String referenceNumber,
        @RequestParam(name = "status", required = false) Set<EnumOrderStatus> status,
        @RequestParam(name = "orderBy", defaultValue = "MODIFIED_ON") EnumOrderSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

    @GetMapping(value = { "/payins" })
    RestResponse<PageResultDto<HelpdeskPayInItemDto>> findPayIns(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "25") @Max(100) @Min(1) int size,
        @RequestParam(name = "providerKey", required = true) UUID providerKey,
        @RequestParam(name = "referenceNumber", required = false, defaultValue = "") String referenceNumber,
        @RequestParam(name = "status", required = false) Set<EnumTransactionStatus> status,
        @RequestParam(name = "orderBy", defaultValue = "MODIFIED_ON") EnumPayInSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

    @GetMapping(value = { "/transfers" })
    RestResponse<PageResultDto<PayInItemDto>> findTransfers(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "25") @Max(100) @Min(1) int size,
        @RequestParam(name = "providerKey", required = true) UUID providerKey,
        @RequestParam(name = "referenceNumber", required = false, defaultValue = "") String referenceNumber,
        @RequestParam(name = "status", required = false) Set<EnumTransactionStatus> status,
        @RequestParam(name = "orderBy", defaultValue = "MODIFIED_ON") EnumTransferSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

    @GetMapping(value = { "/payouts" })
    RestResponse<PageResultDto<PayOutDto>> findPayOuts(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "25") @Max(100) @Min(1) int size,
        @RequestParam(name = "providerKey", required = true) UUID providerKey,
        @RequestParam(name = "bankwireRef", required = false, defaultValue = "") String bankwireRef,
        @RequestParam(name = "status", required = false) Set<EnumTransactionStatus> status,
        @RequestParam(name = "orderBy", defaultValue = "MODIFIED_ON") EnumPayOutSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

}
