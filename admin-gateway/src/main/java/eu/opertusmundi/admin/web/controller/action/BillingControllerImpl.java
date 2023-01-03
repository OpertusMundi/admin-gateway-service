package eu.opertusmundi.admin.web.controller.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import eu.opertusmundi.admin.web.model.ResourceNotFoundException;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.account.AccountDto;
import eu.opertusmundi.common.model.order.EnumOrderSortField;
import eu.opertusmundi.common.model.order.EnumOrderStatus;
import eu.opertusmundi.common.model.order.HelpdeskOrderDto;
import eu.opertusmundi.common.model.order.OrderDto;
import eu.opertusmundi.common.model.payment.EnumPayInSortField;
import eu.opertusmundi.common.model.payment.EnumPayOutSortField;
import eu.opertusmundi.common.model.payment.EnumTransactionStatus;
import eu.opertusmundi.common.model.payment.EnumTransferSortField;
import eu.opertusmundi.common.model.payment.PayInDto;
import eu.opertusmundi.common.model.payment.PayInItemDto;
import eu.opertusmundi.common.model.payment.PayOutCommandDto;
import eu.opertusmundi.common.model.payment.PayOutDto;
import eu.opertusmundi.common.model.payment.PaymentException;
import eu.opertusmundi.common.model.payment.TransferDto;
import eu.opertusmundi.common.model.payment.helpdesk.HelpdeskPayInDto;
import eu.opertusmundi.common.repository.OrderRepository;
import eu.opertusmundi.common.repository.PayInRepository;
import eu.opertusmundi.common.repository.PayOutRepository;
import eu.opertusmundi.common.service.invoice.InvoiceFileManager;
import eu.opertusmundi.common.service.mangopay.PayOutService;
import eu.opertusmundi.common.service.mangopay.TransferService;
import eu.opertusmundi.common.service.mangopay.WalletService;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_USER" })
public class BillingControllerImpl extends BaseController implements BillingController {

    private InvoiceFileManager invoiceFileManager;
    private OrderRepository    orderRepository;
    private PayInRepository    payInRepository;
    private PayOutRepository   payOutRepository;
    private PayOutService      payoutService;
    private TransferService    transferService;
    private WalletService      walletService;

    @Autowired
    public BillingControllerImpl(
        InvoiceFileManager invoiceFileManager,
        OrderRepository    orderRepository,
        PayInRepository    payInRepository,
        PayOutRepository   payOutRepository,
        PayOutService      payoutService,
        TransferService    transferService,
        WalletService      walletService
    ) {
        this.invoiceFileManager = invoiceFileManager;
        this.orderRepository    = orderRepository;
        this.payInRepository    = payInRepository;
        this.payOutRepository   = payOutRepository;
        this.payoutService      = payoutService;
        this.transferService    = transferService;
        this.walletService      = walletService;
    }
    
    @Override
    public RestResponse<AccountDto> refreshUserWallets(UUID userKey) {
        final AccountDto account = this.walletService.refreshUserWallets(userKey);

        return RestResponse.result(account);
    }

    @Override
    public RestResponse<PageResultDto<OrderDto>> findOrders(
        int page, int size,
        String referenceNumber, Set<EnumOrderStatus> status, String email,
        EnumOrderSortField orderBy, EnumSortingOrder order
    ) {
        final Direction   direction    = order == EnumSortingOrder.DESC ? Direction.DESC : Direction.ASC;
        final PageRequest pageRequest  = PageRequest.of(page, size, Sort.by(direction, orderBy.getValue()));
        final String      emailLike = StringUtils.isBlank(email) ? null : "%" + email + "%";

        final Page<OrderDto> p = this.orderRepository.findAllObjects(
            null /* consumerKey */,
            emailLike,
            null /* providerKey */,
            referenceNumber,
            status,
            pageRequest,
            false, true
        );

        final long count = p.getTotalElements();
        final List<OrderDto> records = p.stream().collect(Collectors.toList());
        final PageResultDto<OrderDto> result = PageResultDto.of(page, size, records, count);

        return RestResponse.result(result);
    }

    public RestResponse<OrderDto> findOrderByKey(UUID key) {
        final Optional<HelpdeskOrderDto> r = this.orderRepository.findOrderObjectByKey(key);
        if (r.isPresent()) {
            return RestResponse.result(r.get());
        }
        return RestResponse.notFound();
    }

    @Override
    public RestResponse<PageResultDto<PayInDto>> findPayIns(
        int page, int size,
        String referenceNumber, String email, Set<EnumTransactionStatus> status,
        EnumPayInSortField orderBy, EnumSortingOrder order
    ) {
        final Direction   direction   = order == EnumSortingOrder.DESC ? Direction.DESC : Direction.ASC;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, orderBy.getValue()));
        final String      emailLike   = StringUtils.isBlank(email) ? null : "%" + email + "%";

        final Page<HelpdeskPayInDto> p = this.payInRepository.findAllPayInObjects(
            null /* consumerKey */,
            emailLike,
            referenceNumber,
            status,
            pageRequest
        );

        final long count = p.getTotalElements();
        final List<PayInDto> records = p.stream().collect(Collectors.toList());
        final PageResultDto<PayInDto> result = PageResultDto.of(page, size, records, count);

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<PayInDto> findPayInByKey(UUID key) {
        final Optional<HelpdeskPayInDto> r = this.payInRepository.findOneObjectByKey(key);
        if (r.isPresent()) {
            return RestResponse.result(r.get());
        }
        return RestResponse.notFound();
    }
    
    @Override
    public ResponseEntity<StreamingResponseBody> downloadInvoice(@PathVariable UUID key, HttpServletResponse response) throws IOException {
        final HelpdeskPayInDto p = this.payInRepository.findOneObjectByKey(key).orElse(null);
        if (p == null || !p.isInvoicePrinted()) {
            throw new ResourceNotFoundException("Invoice file was not found");
        }

        final Path path = invoiceFileManager.resolvePath(p.getConsumerId(), p.getReferenceNumber());
        final File file = path.toFile();

        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        response.setHeader("Content-Disposition", String.format("attachment; filename=%s", file.getName()));
        response.setHeader("Content-Type", contentType);
        response.setHeader("Content-Length", Long.toString(file.length()));

        final StreamingResponseBody stream = out -> {
            try (InputStream inputStream = new FileInputStream(file)) {
                IOUtils.copyLarge(inputStream, out);
            }
        };

        return new ResponseEntity<StreamingResponseBody>(stream, HttpStatus.OK);
    }

    @Override
    public RestResponse<PageResultDto<PayInItemDto>> findTransfers(
        int page, int size,
        String referenceNumber, Set<EnumTransactionStatus> status,
        EnumTransferSortField orderBy, EnumSortingOrder order
    ) {
        final Direction   direction   = order == EnumSortingOrder.DESC ? Direction.DESC : Direction.ASC;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, orderBy.getValue()));

        final Page<PayInItemDto> p = this.payInRepository.findAllTransferObjects(
            null /* providerKey */,
            status,
            referenceNumber,
            pageRequest
        );

        final long count = p.getTotalElements();
        final List<PayInItemDto> records = p.stream().collect(Collectors.toList());
        final PageResultDto<PayInItemDto> result = PageResultDto.of(page, size, records, count);

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> createTransfer(UUID key) {
        try {
            final List<TransferDto> transfers = this.transferService.createTransfer(this.currentUserKey(), key);

            return RestResponse.result(transfers);
        } catch (PaymentException ex) {
            return RestResponse.error(ex.getCode(), ex.getMessage());
        }
    }

    @Override
    public RestResponse<PageResultDto<PayOutDto>> findPayOuts(
        int page, int size,
        String bankwireRef, String email, Set<EnumTransactionStatus> status,
        EnumPayOutSortField orderBy, EnumSortingOrder order
    ) {
        final Direction   direction   = order == EnumSortingOrder.DESC ? Direction.DESC : Direction.ASC;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, orderBy.getValue()));
        final String      emailLike   = StringUtils.isBlank(email) ? null : "%" + email + "%";

        final Page<PayOutDto> p = this.payOutRepository.findAllPayOutObjects(
            null /* providerKey */,
            emailLike,
            status,
            bankwireRef,
            pageRequest
        );

        final long count = p.getTotalElements();
        final List<PayOutDto> records = p.stream().collect(Collectors.toList());
        final PageResultDto<PayOutDto> result = PageResultDto.of(page, size, records, count);

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<PayOutDto> findPayOutByKey(UUID key) {
        final Optional<PayOutDto> r = this.payOutRepository.findOneObjectByKey(key, true);
        if (r.isPresent()) {
            return RestResponse.result(r.get());
        }
        return RestResponse.notFound();
    }

    @Override
    public RestResponse<PayOutDto> createPayOut(UUID userKey, PayOutCommandDto command, BindingResult validationResult) {
        try {
            command.setAdminUserKey(this.currentUserKey());
            command.setProviderKey(userKey);

            if (validationResult.hasErrors()) {
                return RestResponse.invalid(validationResult.getFieldErrors(), validationResult.getGlobalErrors());
            }

            final PayOutDto payOut = this.payoutService.createPayOutAtOpertusMundi(command);

            return RestResponse.result(payOut);
        } catch (PaymentException ex) {
            return RestResponse.error(ex.getCode(), ex.getMessage());
        }
    }

}
