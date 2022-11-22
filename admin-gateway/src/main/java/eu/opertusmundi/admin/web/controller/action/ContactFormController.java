package eu.opertusmundi.admin.web.controller.action;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.opertusmundi.common.model.BaseResponse;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.message.ContactFormDto;
import eu.opertusmundi.common.model.message.EnumContactFormSortField;
import eu.opertusmundi.common.model.message.EnumContactFormStatus;

@RequestMapping(value = "/action/contact-forms", produces = MediaType.APPLICATION_JSON_VALUE)
public interface ContactFormController {

    /**
     * Find submitted forms
     *
     * @param email
     * @param status
     * @param dateFrom
     * @param dateTo
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/")
    RestResponse<PageResultDto<ContactFormDto>> findForms(
        @RequestParam(required = false) String email,
        @RequestParam(required = false) EnumContactFormStatus status,
        @RequestParam(required = false) ZonedDateTime dateFrom,
        @RequestParam(required = false) ZonedDateTime dateTo,
        @RequestParam() Integer pageIndex,
        @RequestParam() Integer pageSize,
        @RequestParam() EnumContactFormSortField sortBy,
        @RequestParam() EnumSortingOrder sortOrder
    );

    /**
     * Count pending forms
     *
     * @return
     */
    @GetMapping(value = "/count")
    RestResponse<?> countPendingForms();

    /**
     * Mark form as complete
     *
     * @param formKey
     * @return
     */
    @PutMapping(value = "/{formKey}")
    RestResponse<?> completeForm(UUID formKey);

}
