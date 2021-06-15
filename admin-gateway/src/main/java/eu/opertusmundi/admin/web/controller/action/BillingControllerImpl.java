package eu.opertusmundi.admin.web.controller.action;

import java.util.List;
import java.util.Optional;
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

import eu.opertusmundi.admin.web.model.billing.EnumOrderSortField;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.order.EnumOrderStatus;
import eu.opertusmundi.common.model.order.OrderDto;
import eu.opertusmundi.common.model.payment.EnumPayInSortField;
import eu.opertusmundi.common.model.payment.EnumTransactionStatus;
import eu.opertusmundi.common.model.payment.PayInDto;
import eu.opertusmundi.common.repository.OrderRepository;
import eu.opertusmundi.common.repository.PayInRepository;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_USER" })
public class BillingControllerImpl extends BaseController implements BillingController {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private PayInRepository payInRepository;

    @Override
    public RestResponse<PageResultDto<OrderDto>> findOrders(
        int page, int size, 
        String referenceNumber, Set<EnumOrderStatus> status, 
        EnumOrderSortField orderBy, EnumSortingOrder order
    ) {

        final Direction   direction   = order == EnumSortingOrder.DESC ? Direction.DESC : Direction.ASC;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, orderBy.getValue()));

        final Page<OrderDto> p = this.orderRepository.findAllObjects(
            referenceNumber,
            status,
            pageRequest
        );

        final long count = p.getTotalElements();
        final List<OrderDto> records = p.stream().collect(Collectors.toList());
        final PageResultDto<OrderDto> result = PageResultDto.of(page, size, records, count);

        return RestResponse.result(result);
    }

    public RestResponse<OrderDto> findOrderByKey(UUID key) {
        final Optional<OrderDto> r = this.orderRepository.findOrderObjectByKey(key);
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

        final Page<PayInDto> p = this.payInRepository.findAllPayInObjects(
            status,
            email,
            referenceNumber,
            pageRequest
        );

        final long count = p.getTotalElements();
        final List<PayInDto> records = p.stream().collect(Collectors.toList());
        final PageResultDto<PayInDto> result = PageResultDto.of(page, size, records, count);

        return RestResponse.result(result);
    }

    public RestResponse<PayInDto> findPayInByKey(UUID key) {
        final Optional<PayInDto> r = this.payInRepository.findOneObjectByKey(key, true);
        if (r.isPresent()) {
            return RestResponse.result(r.get());
        }
        return RestResponse.notFound();
    }

}
