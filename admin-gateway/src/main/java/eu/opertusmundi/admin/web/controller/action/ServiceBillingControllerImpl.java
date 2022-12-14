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
import eu.opertusmundi.common.model.payment.EnumServiceBillingBatchSortField;
import eu.opertusmundi.common.model.payment.EnumServiceBillingBatchStatus;
import eu.opertusmundi.common.model.payment.PaymentException;
import eu.opertusmundi.common.model.payment.ServiceBillingBatchCommandDto;
import eu.opertusmundi.common.model.payment.ServiceBillingBatchDto;
import eu.opertusmundi.common.model.pricing.PerCallPricingModelCommandDto;
import eu.opertusmundi.common.model.pricing.QuotationException;
import eu.opertusmundi.common.repository.ServiceBillingBatchRepository;
import eu.opertusmundi.common.service.ServiceBillingService;

@RestController
public class ServiceBillingControllerImpl extends BaseController implements ServiceBillingController {

    private ServiceBillingBatchRepository ServiceBillingBatchRepository;
    private ServiceBillingService              serviceBillingService;

    @Autowired
    public ServiceBillingControllerImpl(
        ServiceBillingBatchRepository ServiceBillingBatchRepository,
        ServiceBillingService serviceBillingService
    ) {
        this.ServiceBillingBatchRepository = ServiceBillingBatchRepository;
        this.serviceBillingService              = serviceBillingService;
    }

    @Override
    @Secured({ "ROLE_ADMIN", "ROLE_USER" })
    public RestResponse<PageResultDto<ServiceBillingBatchDto>> findAll(
        int page, int size, Set<EnumServiceBillingBatchStatus> status,
        EnumServiceBillingBatchSortField orderBy, EnumSortingOrder order
    ) {
        final Direction   direction    = order == EnumSortingOrder.DESC ? Direction.DESC : Direction.ASC;
        final PageRequest pageRequest  = PageRequest.of(page, size, Sort.by(direction, orderBy.getValue()));

        final Page<ServiceBillingBatchDto> p = this.ServiceBillingBatchRepository.findAllObjects(status, pageRequest);

        final long count = p.getTotalElements();
        final List<ServiceBillingBatchDto> records = p.stream().collect(Collectors.toList());
        final PageResultDto<ServiceBillingBatchDto> result = PageResultDto.of(page, size, records, count);

        return RestResponse.result(result);
    }

    @Override
    @Secured({ "ROLE_ADMIN", "ROLE_USER" })
    public RestResponse<ServiceBillingBatchDto> findOne(UUID key) {
        final Optional<ServiceBillingBatchDto> r = this.ServiceBillingBatchRepository.findOneObjectByKey(key);
        if (r.isPresent()) {
            return RestResponse.result(r.get());
        }
        return RestResponse.notFound();
    }

    @Override
    @Secured({ "ROLE_ADMIN" })
    public RestResponse<ServiceBillingBatchDto> create(ServiceBillingBatchCommandDto command, BindingResult validationResult) {
        try {
            command.setUserId(this.currentUserId());

            if (validationResult.hasErrors()) {
                return RestResponse.invalid(validationResult.getFieldErrors(), validationResult.getGlobalErrors());
            }

            final ServiceBillingBatchDto batch = this.serviceBillingService.start(command);

            return RestResponse.result(batch);
        } catch (PaymentException ex) {
            return RestResponse.error(ex.getCode(), ex.getMessage());
        }
    }

    @Override
    @Secured({ "ROLE_ADMIN" })
    public RestResponse<PerCallPricingModelCommandDto> getPrivateServicePricingModel() {
        var model = this.serviceBillingService.getPrivateServicePricingModel();
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

        this.serviceBillingService.setPrivateServicePricingModel(this.currentUserId(), model);
        return RestResponse.success();
    }

}
