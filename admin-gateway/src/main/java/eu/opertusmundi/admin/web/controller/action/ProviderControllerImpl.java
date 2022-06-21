package eu.opertusmundi.admin.web.controller.action;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;

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
import eu.opertusmundi.common.repository.OrderRepository;
import eu.opertusmundi.common.repository.PayInRepository;
import eu.opertusmundi.common.repository.PayOutRepository;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_USER" })
public class ProviderControllerImpl extends BaseController implements ProviderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PayInRepository payInRepository;

    @Autowired
    private PayOutRepository payOutRepository;

    @Override
    public RestResponse<PageResultDto<OrderDto>> findOrders(
        int page, int size,
        UUID providerKey, String referenceNumber, Set<EnumOrderStatus> status,
        EnumOrderSortField orderBy, EnumSortingOrder order
    ) {
        final Direction   direction    = order == EnumSortingOrder.DESC ? Direction.DESC : Direction.ASC;
        final PageRequest pageRequest  = PageRequest.of(page, size, Sort.by(direction, orderBy.getValue()));

        final Page<OrderDto> p = this.orderRepository.findAllObjects(
            null /* consumerKey */,
            null /* consumerEmail */,
            providerKey,
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

    @Override
    public RestResponse<PageResultDto<HelpdeskPayInItemDto>> findPayIns(
        int page, int size,
        UUID providerKey, String referenceNumber, Set<EnumTransactionStatus> status,
        EnumPayInSortField orderBy, EnumSortingOrder order
    ) {
        final Direction   direction   = order == EnumSortingOrder.DESC ? Direction.DESC : Direction.ASC;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, orderBy.getValue()));

        final Page<HelpdeskPayInItemDto> p = this.payInRepository.findAllObjectsProviderPayInItems(
            providerKey, 
            referenceNumber, 
            status, 
            pageRequest
        );

        final long count = p.getTotalElements();
        final List<HelpdeskPayInItemDto> records = p.stream().collect(Collectors.toList());
        final PageResultDto<HelpdeskPayInItemDto> result = PageResultDto.of(page, size, records, count);

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<PageResultDto<PayInItemDto>> findTransfers(
        int page, int size,
        UUID providerKey, String referenceNumber, Set<EnumTransactionStatus> status,
        EnumTransferSortField orderBy, EnumSortingOrder order
    ) {
        final Direction   direction   = order == EnumSortingOrder.DESC ? Direction.DESC : Direction.ASC;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, orderBy.getValue()));

        final Page<PayInItemDto> p = this.payInRepository.findAllTransferObjects(
            providerKey,
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
    public RestResponse<PageResultDto<PayOutDto>> findPayOuts(
        int page, int size,
        UUID providerKey, String bankwireRef, Set<EnumTransactionStatus> status,
        EnumPayOutSortField orderBy, EnumSortingOrder order
    ) {
        final Direction   direction   = order == EnumSortingOrder.DESC ? Direction.DESC : Direction.ASC;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, orderBy.getValue()));

        final Page<PayOutDto> p = this.payOutRepository.findAllPayOutObjects(
            providerKey,
            null /* providerEmail */,
            status,
            bankwireRef,
            pageRequest
        );

        final long count = p.getTotalElements();
        final List<PayOutDto> records = p.stream().collect(Collectors.toList());
        final PageResultDto<PayOutDto> result = PageResultDto.of(page, size, records, count);

        return RestResponse.result(result);
    }


}
