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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.payment.EnumSubscriptionBillingBatchSortField;
import eu.opertusmundi.common.model.payment.EnumSubscriptionBillingBatchStatus;
import eu.opertusmundi.common.model.payment.PaymentException;
import eu.opertusmundi.common.model.payment.SubscriptionBillingBatchCommandDto;
import eu.opertusmundi.common.model.payment.SubscriptionBillingBatchDto;
import eu.opertusmundi.common.model.pricing.PerCallPricingModelCommandDto;
import eu.opertusmundi.common.model.pricing.QuotationException;
import eu.opertusmundi.common.repository.SubscriptionBillingBatchRepository;
import eu.opertusmundi.common.service.SubscriptionBillingService;

@RestController
public class SubscriptionBillingControllerImpl extends BaseController implements SubscriptionBillingController {

    private SubscriptionBillingBatchRepository subscriptionBillingBatchRepository;
    private SubscriptionBillingService         subscriptionBillingService;

    @Autowired
    public SubscriptionBillingControllerImpl(

        SubscriptionBillingBatchRepository subscriptionBillingBatchRepository,
        SubscriptionBillingService subscriptionBillingService
    ) {
        this.subscriptionBillingBatchRepository = subscriptionBillingBatchRepository;
        this.subscriptionBillingService         = subscriptionBillingService;
    }

    @Override
    @Secured({ "ROLE_ADMIN", "ROLE_USER" })
    public RestResponse<PageResultDto<SubscriptionBillingBatchDto>> findAll(
        int page, int size, Set<EnumSubscriptionBillingBatchStatus> status,
        EnumSubscriptionBillingBatchSortField orderBy, EnumSortingOrder order
    ) {
        final Direction   direction    = order == EnumSortingOrder.DESC ? Direction.DESC : Direction.ASC;
        final PageRequest pageRequest  = PageRequest.of(page, size, Sort.by(direction, orderBy.getValue()));

        final Page<SubscriptionBillingBatchDto> p = this.subscriptionBillingBatchRepository.findAllObjects(status, pageRequest);

        final long count = p.getTotalElements();
        final List<SubscriptionBillingBatchDto> records = p.stream().collect(Collectors.toList());
        final PageResultDto<SubscriptionBillingBatchDto> result = PageResultDto.of(page, size, records, count);

        return RestResponse.result(result);
    }

    @Override
    @Secured({ "ROLE_ADMIN", "ROLE_USER" })
    public RestResponse<SubscriptionBillingBatchDto> findOne(UUID key) {
        final Optional<SubscriptionBillingBatchDto> r = this.subscriptionBillingBatchRepository.findOneObjectByKey(key);
        if (r.isPresent()) {
            return RestResponse.result(r.get());
        }
        return RestResponse.notFound();
    }

    @Override
    @Secured({ "ROLE_ADMIN" })
    public RestResponse<SubscriptionBillingBatchDto> create(SubscriptionBillingBatchCommandDto command, BindingResult validationResult) {
        try {
            command.setUserId(this.currentUserId());

            if (validationResult.hasErrors()) {
                return RestResponse.invalid(validationResult.getFieldErrors(), validationResult.getGlobalErrors());
            }

            final SubscriptionBillingBatchDto batch = this.subscriptionBillingService.start(command);

            return RestResponse.result(batch);
        } catch (PaymentException ex) {
            return RestResponse.error(ex.getCode(), ex.getMessage());
        }
    }

    @Override
    @Secured({ "ROLE_ADMIN" })
    public RestResponse<PerCallPricingModelCommandDto> getPrivateServicePricingModel() {
        var model = this.subscriptionBillingService.getPrivateServicePricingModel();
        return RestResponse.result(model);
    }

    @Override
    @Secured({ "ROLE_ADMIN" })
    public RestResponse<PerCallPricingModelCommandDto> setPrivateServicePricingModel(
        PerCallPricingModelCommandDto model, BindingResult validationResult
    ) {
        try {
            model.validate();
        } catch (QuotationException ex) {
            validationResult.reject(ex.getCode().toString(), ex.getMessage());
        }

        if (validationResult.hasErrors()) {
            return RestResponse.invalid(validationResult.getFieldErrors(), validationResult.getGlobalErrors());
        }

        this.subscriptionBillingService.setPrivateServicePricingModel(this.currentUserId(), model);
        return RestResponse.success();
    }

}
