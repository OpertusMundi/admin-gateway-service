package eu.opertusmundi.admin.web.controller.action;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.account.AccountDto;
import eu.opertusmundi.common.model.order.EnumOrderSortField;
import eu.opertusmundi.common.model.order.EnumOrderStatus;
import eu.opertusmundi.common.model.order.OrderDto;
import eu.opertusmundi.common.model.payment.EnumPayInSortField;
import eu.opertusmundi.common.model.payment.EnumPayOutSortField;
import eu.opertusmundi.common.model.payment.EnumTransactionStatus;
import eu.opertusmundi.common.model.payment.EnumTransferSortField;
import eu.opertusmundi.common.model.payment.PayInDto;
import eu.opertusmundi.common.model.payment.PayInItemDto;
import eu.opertusmundi.common.model.payment.PayOutCommandDto;
import eu.opertusmundi.common.model.payment.PayOutDto;

@RequestMapping(value = "/action/billing", produces = MediaType.APPLICATION_JSON_VALUE)
public interface BillingController {

    @PutMapping(value = { "/wallets/{key}" })
    RestResponse<AccountDto> refreshUserWallets(
        @PathVariable(name = "key") UUID userKey
    );

    @GetMapping(value = { "/orders" })
    RestResponse<PageResultDto<OrderDto>> findOrders(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "25") @Max(100) @Min(1) int size,
        @RequestParam(name = "referenceNumber", required = false, defaultValue = "") String referenceNumber,
        @RequestParam(name = "status", required = false) Set<EnumOrderStatus> status,
        @RequestParam(name = "email", required = false, defaultValue = "") String email,
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
        @RequestParam(name = "referenceNumber", required = false, defaultValue = "") String referenceNumber,
        @RequestParam(name = "email", required = false) String email,
        @RequestParam(name = "status", required = false) Set<EnumTransactionStatus> status,
        @RequestParam(name = "orderBy", defaultValue = "MODIFIED_ON") EnumPayInSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

    @GetMapping(value = { "/payins/{key}" })
    RestResponse<PayInDto> findPayInByKey(
        @PathVariable(name = "key") UUID key
    );

    @GetMapping(value = "/payins/{key}/invoice", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    ResponseEntity<StreamingResponseBody> downloadInvoice(@PathVariable UUID key, HttpServletResponse response) throws IOException;

    @GetMapping(value = { "/transfers" })
    RestResponse<PageResultDto<PayInItemDto>> findTransfers(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "25") @Max(100) @Min(1) int size,
        @RequestParam(name = "referenceNumber", required = false, defaultValue = "") String referenceNumber,
        @RequestParam(name = "status", required = false) Set<EnumTransactionStatus> status,
        @RequestParam(name = "orderBy", defaultValue = "MODIFIED_ON") EnumTransferSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

    @PostMapping(value = { "/transfers/{key}" })
    RestResponse<?> createTransfer(
        @PathVariable(name = "key") UUID key
    );

    @GetMapping(value = { "/payouts" })
    RestResponse<PageResultDto<PayOutDto>> findPayOuts(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "25") @Max(100) @Min(1) int size,
        @RequestParam(name = "bankwireRef", required = false, defaultValue = "") String bankwireRef,
        @RequestParam(name = "email", required = false) String email,
        @RequestParam(name = "status", required = false) Set<EnumTransactionStatus> status,
        @RequestParam(name = "orderBy", defaultValue = "MODIFIED_ON") EnumPayOutSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

    @GetMapping(value = { "/payouts/{key}" })
    RestResponse<PayOutDto> findPayOutByKey(
        @PathVariable(name = "key") UUID key
    );

    @PostMapping(value = { "/payouts/providers/{key}" })
    @Validated
    RestResponse<PayOutDto> createPayOut(
        @PathVariable(name = "key") UUID userKey,
        @RequestBody @Valid PayOutCommandDto command,
        BindingResult validationResult
    );

}
