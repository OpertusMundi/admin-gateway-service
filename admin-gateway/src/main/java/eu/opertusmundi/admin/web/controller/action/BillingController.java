package eu.opertusmundi.admin.web.controller.action;

import java.util.Set;
import java.util.UUID;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.opertusmundi.admin.web.model.billing.EnumOrderSortField;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.order.EnumOrderStatus;
import eu.opertusmundi.common.model.order.OrderDto;
import eu.opertusmundi.common.model.payment.EnumPayInSortField;
import eu.opertusmundi.common.model.payment.EnumTransactionStatus;
import eu.opertusmundi.common.model.payment.EnumTransferSortField;
import eu.opertusmundi.common.model.payment.PayInDto;
import eu.opertusmundi.common.model.payment.PayInItemDto;

@RequestMapping(value = "/action/billing", produces = MediaType.APPLICATION_JSON_VALUE)
public interface BillingController {

    @GetMapping(value = { "/orders" })
    RestResponse<PageResultDto<OrderDto>> findOrders(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "25") @Max(100) @Min(1) int size,
        @RequestParam(name = "referenceNumber", required = false, defaultValue = "") String name,
        @RequestParam(name = "status", required = false) Set<EnumOrderStatus> status,
        @RequestParam(name = "orderBy", defaultValue = "MODIFIED_ON") EnumOrderSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

    @GetMapping(value = { "/orders/{key}" })
    RestResponse<OrderDto> findOrderByKey(
        @PathVariable(name = "key") UUID key
    );
    
    @GetMapping(value = { "/payins" })
    RestResponse<PageResultDto<PayInDto>> findPayIns(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "25") @Max(100) @Min(1) int size,
        @RequestParam(name = "referenceNumber", required = false, defaultValue = "") String name,
        @RequestParam(name = "email", required = false) String email,
        @RequestParam(name = "status", required = false) Set<EnumTransactionStatus> status,
        @RequestParam(name = "orderBy", defaultValue = "MODIFIED_ON") EnumPayInSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

    @GetMapping(value = { "/payins/{key}" })
    RestResponse<PayInDto> findPayInByKey(
        @PathVariable(name = "key") UUID key
    );
 
    @GetMapping(value = { "/transfers" })
    RestResponse<PageResultDto<PayInItemDto>> findTransfers(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "25") @Max(100) @Min(1) int size,
        @RequestParam(name = "referenceNumber", required = false, defaultValue = "") String name,
        @RequestParam(name = "status", required = false) Set<EnumTransactionStatus> status,
        @RequestParam(name = "orderBy", defaultValue = "MODIFIED_ON") EnumTransferSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

    @PostMapping(value = { "/transfers/{key}" })
    RestResponse<?> createTransfer(
        @PathVariable(name = "key") UUID key
    );
    
}
