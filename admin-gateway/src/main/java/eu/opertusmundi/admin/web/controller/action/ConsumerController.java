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
import eu.opertusmundi.common.model.account.AccountSubscriptionDto;
import eu.opertusmundi.common.model.account.EnumSubscriptionBillingSortField;
import eu.opertusmundi.common.model.account.EnumSubscriptionBillingStatus;
import eu.opertusmundi.common.model.account.EnumSubscriptionSortField;
import eu.opertusmundi.common.model.account.EnumSubscriptionStatus;
import eu.opertusmundi.common.model.order.EnumOrderSortField;
import eu.opertusmundi.common.model.order.EnumOrderStatus;
import eu.opertusmundi.common.model.order.OrderDto;
import eu.opertusmundi.common.model.payment.EnumPayInSortField;
import eu.opertusmundi.common.model.payment.EnumTransactionStatus;
import eu.opertusmundi.common.model.payment.PayInDto;
import eu.opertusmundi.common.model.payment.SubscriptionBillingDto;

@RequestMapping(value = "/action/consumer", produces = MediaType.APPLICATION_JSON_VALUE)
public interface ConsumerController {

    @GetMapping(value = { "/orders" })
    RestResponse<PageResultDto<OrderDto>> findOrders(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "25") @Max(100) @Min(1) int size,
        @RequestParam(name = "consumerKey", required = true) UUID consumerKey,
        @RequestParam(name = "referenceNumber", required = false, defaultValue = "") String referenceNumber,
        @RequestParam(name = "status", required = false) Set<EnumOrderStatus> status,
        @RequestParam(name = "orderBy", defaultValue = "MODIFIED_ON") EnumOrderSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

    @GetMapping(value = { "/payins" })
    RestResponse<PageResultDto<PayInDto>> findPayIns(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "25") @Max(100) @Min(1) int size,
        @RequestParam(name = "consumerKey", required = true) UUID consumerKey,
        @RequestParam(name = "referenceNumber", required = false, defaultValue = "") String referenceNumber,
        @RequestParam(name = "status", required = false) Set<EnumTransactionStatus> status,
        @RequestParam(name = "orderBy", defaultValue = "MODIFIED_ON") EnumPayInSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

    @GetMapping(value = { "/subscriptions" })
    RestResponse<PageResultDto<AccountSubscriptionDto>> findSubscriptions(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "25") @Max(100) @Min(1) int size,
        @RequestParam(name = "consumerKey", required = true) UUID consumerKey,
        @RequestParam(name = "status", required = false) Set<EnumSubscriptionStatus> status,
        @RequestParam(name = "orderBy", defaultValue = "MODIFIED_ON") EnumSubscriptionSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );
    
    @GetMapping(value = { "/subscription-billing" })
    RestResponse<PageResultDto<SubscriptionBillingDto>> findSubscriptionBillingRecords(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "25") @Max(100) @Min(1) int size,
        @RequestParam(name = "consumerKey", required = true) UUID consumerKey,
        @RequestParam(name = "subscriptionId", required = false) Integer subscriptionId,
        @RequestParam(name = "status", required = false) Set<EnumSubscriptionBillingStatus> status,
        @RequestParam(name = "orderBy", defaultValue = "CREATED_ON") EnumSubscriptionBillingSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );
    
    
    
}
