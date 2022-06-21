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
import eu.opertusmundi.common.model.account.AccountSubscriptionDto;
import eu.opertusmundi.common.model.account.EnumSubscriptionSortField;
import eu.opertusmundi.common.model.account.EnumSubscriptionStatus;
import eu.opertusmundi.common.model.catalogue.client.CatalogueItemDetailsDto;
import eu.opertusmundi.common.model.order.EnumOrderSortField;
import eu.opertusmundi.common.model.order.EnumOrderStatus;
import eu.opertusmundi.common.model.order.OrderDto;
import eu.opertusmundi.common.model.payment.EnumPayInSortField;
import eu.opertusmundi.common.model.payment.EnumTransactionStatus;
import eu.opertusmundi.common.model.payment.PayInDto;
import eu.opertusmundi.common.model.payment.helpdesk.HelpdeskPayInDto;
import eu.opertusmundi.common.repository.AccountSubscriptionRepository;
import eu.opertusmundi.common.repository.OrderRepository;
import eu.opertusmundi.common.repository.PayInRepository;
import eu.opertusmundi.common.service.CatalogueService;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_USER" })
public class ConsumerControllerImpl extends BaseController implements ConsumerController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PayInRepository payInRepository;
    
    @Autowired
    private AccountSubscriptionRepository subscriptionRepository;

    @Autowired
    private CatalogueService catalogueService;

    @Override
    public RestResponse<PageResultDto<OrderDto>> findOrders(
        int page, int size,
        UUID consumerKey, String referenceNumber, Set<EnumOrderStatus> status,
        EnumOrderSortField orderBy, EnumSortingOrder order
    ) {
        final Direction   direction    = order == EnumSortingOrder.DESC ? Direction.DESC : Direction.ASC;
        final PageRequest pageRequest  = PageRequest.of(page, size, Sort.by(direction, orderBy.getValue()));

        final Page<OrderDto> p = this.orderRepository.findAllObjects(
            consumerKey,
            null /* consumerEmail */,
            null /* provider */,
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
    public RestResponse<PageResultDto<PayInDto>> findPayIns(
        int page, int size,
        UUID consumerKey, String referenceNumber, Set<EnumTransactionStatus> status,
        EnumPayInSortField orderBy, EnumSortingOrder order
    ) {
        final Direction   direction   = order == EnumSortingOrder.DESC ? Direction.DESC : Direction.ASC;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, orderBy.getValue()));

        final Page<HelpdeskPayInDto> p = this.payInRepository.findAllPayInObjects(
            consumerKey,
            null /* consumerEmail */,
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
    public RestResponse<PageResultDto<AccountSubscriptionDto>> findSubscriptions(
        int page, int size,
        UUID consumerKey, Set<EnumSubscriptionStatus> status, EnumSubscriptionSortField orderBy,
        EnumSortingOrder order
    ) {
        final Direction   direction   = order == EnumSortingOrder.DESC ? Direction.DESC : Direction.ASC;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, orderBy.getValue()));

        final Page<AccountSubscriptionDto> p = this.subscriptionRepository.findAllObjectsByConsumer(
            consumerKey, 
            null /* providerKey */,
            status, 
            pageRequest 
        );
        
        final long count = p.getTotalElements();
        final List<AccountSubscriptionDto> records = p.stream().collect(Collectors.toList());
        final PageResultDto<AccountSubscriptionDto> result = PageResultDto.of(page, size, records, count);

        final String[]                      pid    = result.getItems().stream().map(a -> a.getAssetId()).distinct().toArray(String[]::new);
        final List<CatalogueItemDetailsDto> assets = this.catalogueService.findAllById(pid);
        
        // Add catalogue items to records
        result.getItems().forEach(r -> {
            final CatalogueItemDetailsDto item = assets.stream()
                .filter(a -> a.getId().equals(r.getAssetId()))
                .findFirst()
                .orElse(null);

            if (item != null) {
                // Remove superfluous data
                item.resetAutomatedMetadata();
                item.resetContract();

                r.setItem(item);
            }
        });
        
        return RestResponse.result(result);
    }   

}
