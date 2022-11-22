package eu.opertusmundi.admin.web.controller.action;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.message.ContactFormDto;
import eu.opertusmundi.common.model.message.EnumContactFormSortField;
import eu.opertusmundi.common.model.message.EnumContactFormStatus;
import eu.opertusmundi.common.service.messaging.ContactFormService;

@RestController
@Secured({ "ROLE_USER" })
public class ContactFormControllerImpl extends BaseController implements ContactFormController {

    private final ContactFormService contactFormService;

    @Autowired
    public ContactFormControllerImpl(ContactFormService contactFormService) {
        this.contactFormService = contactFormService;
    }

    @Override
    public RestResponse<PageResultDto<ContactFormDto>> findForms(
        String email, EnumContactFormStatus status, ZonedDateTime dateFrom, ZonedDateTime dateTo,
        Integer pageIndex, Integer pageSize, EnumContactFormSortField sortBy, EnumSortingOrder sortOrder
    ) {
        final PageResultDto<ContactFormDto> result = this.contactFormService.find(
            email, status, dateFrom, dateTo, pageIndex, pageSize, sortBy, sortOrder
        );
        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> countPendingForms() {
        final Long result = this.contactFormService.countPendingForms();
        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> completeForm(UUID formKey) {
        final var result = this.contactFormService.completeForm(formKey);
        return RestResponse.result(result);
    }

}
