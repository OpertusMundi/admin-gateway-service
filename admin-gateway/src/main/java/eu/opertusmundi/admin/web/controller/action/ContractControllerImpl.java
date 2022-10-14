package eu.opertusmundi.admin.web.controller.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import eu.opertusmundi.common.model.ApplicationException;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.contract.EnumContractStatus;
import eu.opertusmundi.common.model.contract.helpdesk.EnumMasterContractSortField;
import eu.opertusmundi.common.model.contract.helpdesk.MasterContractCommandDto;
import eu.opertusmundi.common.model.contract.helpdesk.MasterContractDto;
import eu.opertusmundi.common.model.contract.helpdesk.MasterContractHistoryDto;
import eu.opertusmundi.common.model.contract.helpdesk.MasterContractHistoryResult;
import eu.opertusmundi.common.model.contract.helpdesk.MasterContractQueryDto;
import eu.opertusmundi.common.service.contract.MasterTemplateContractService;

@RestController
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class ContractControllerImpl extends BaseController implements ContractController {

    @Autowired
    private MasterTemplateContractService masterService;

    @Override
    public RestResponse<MasterContractHistoryResult> findAllHistory(
        int page,
        int size,
        String defaultContract,
        String title,
        Set<EnumContractStatus> status,
        EnumMasterContractSortField orderBy,
        EnumSortingOrder order
    ) {
        final MasterContractQueryDto query = MasterContractQueryDto.builder()
            .page(page)
            .size(size)
            .defaultContract(defaultContract == null ? null : Boolean.parseBoolean(defaultContract))
            .title(title)
            .status(status)
            .orderBy(orderBy)
            .order(order)
            .build();

        final MasterContractHistoryResult result = masterService.findAllHistory(query);

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<MasterContractDto> createDraftForTemplate(int id) {
        try {
            final MasterContractDto result = this.masterService.createForTemplate(this.currentUserId(), id);

            return RestResponse.result(result);
        } catch (final ApplicationException ex) {
            return RestResponse.error(ex.getCode(), ex.getMessage());
        }
    }

    @Override
    public RestResponse<MasterContractDto> cloneDraftFromTemplate(int id) {
        try {
            final MasterContractDto result = this.masterService.cloneFromTemplate(this.currentUserId(), id);

            return RestResponse.result(result);
        } catch (final ApplicationException ex) {
            return RestResponse.error(ex.getCode(), ex.getMessage());
        }
    }

    @Override
    public RestResponse<MasterContractHistoryDto> deactivate(int id) {
        try {
            final MasterContractHistoryDto result = this.masterService.deactivate(id);

            return RestResponse.result(result);
        } catch (final ApplicationException ex) {
            return RestResponse.error(ex.getCode(), ex.getMessage());
        }
    }

    @Override
    public RestResponse<PageResultDto<MasterContractDto>> findAll(
        int page,
        int size,
        EnumMasterContractSortField orderBy,
        EnumSortingOrder order
    ) {
        final MasterContractQueryDto query = MasterContractQueryDto.builder()
            .page(page)
            .size(size)
            .orderBy(orderBy)
            .order(order)
            .build();

        final PageResultDto<MasterContractDto> result = masterService.findAll(query);

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> findOne(int id) {
        final MasterContractDto result = this.masterService.findOneById(id).orElse(null);

        if (result == null) {
            return RestResponse.notFound();
        }

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<PageResultDto<MasterContractDto>> findAllDrafts(
        int page,
        int size,
        EnumMasterContractSortField orderBy,
        EnumSortingOrder order
    ) {
        final PageResultDto<MasterContractDto> result = this.masterService.findAllDrafts(page, size, orderBy, order);

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<MasterContractDto> findOneDraft(int id) {
        final MasterContractDto result = this.masterService.findOneDraft(id);

        if (result == null) {
            return RestResponse.notFound();
        }

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<MasterContractDto> createDraft(MasterContractCommandDto command, BindingResult validationResult) {
        try {
            command.setUserId(this.currentUserId());

            if (validationResult.hasErrors()) {
                return RestResponse.invalid(validationResult.getFieldErrors());
            }

            final MasterContractDto result = this.masterService.updateDraft(command);

            return RestResponse.result(result);
        } catch (final ApplicationException ex) {
            return RestResponse.error(ex.getCode(), ex.getMessage());
        }
    }

    @Override
    public RestResponse<MasterContractDto> updateDraft(int id, MasterContractCommandDto command, BindingResult validationResult) {
        try {
            command.setId(id);
            command.setUserId(this.currentUserId());

            if (validationResult.hasErrors()) {
                return RestResponse.invalid(validationResult.getFieldErrors());
            }

            final MasterContractDto result = this.masterService.updateDraft(command);

            return RestResponse.result(result);
        } catch (final ApplicationException ex) {
            return RestResponse.error(ex.getCode(), ex.getMessage());
        }
    }

    @Override
    public RestResponse<Void> deleteDraft(int id) {
        try {
            masterService.deleteDraft(id);

            return RestResponse.success();
        } catch (final ApplicationException ex) {
            return RestResponse.error(ex.getCode(), ex.getMessage());
        }
    }

    @Override
    public RestResponse<MasterContractDto> publishDraft(int id) {
        try {
            final MasterContractDto result = this.masterService.publishDraft(id);

            return RestResponse.result(result);
        } catch (final ApplicationException ex) {
            return RestResponse.error(ex.getCode(), ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<StreamingResponseBody> print(int id){
        byte[] data = null;
        try {
            data = masterService.print(id);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        InputStream inputStream = new ByteArrayInputStream(data);
        final StreamingResponseBody stream = out -> {
           IOUtils.copyLarge(inputStream, out);

        };
        return new ResponseEntity<StreamingResponseBody>(stream, HttpStatus.OK);
    }

    @Override
    public RestResponse<MasterContractDto> setDefaultContract(int id) {
        try {
            final MasterContractDto result = this.masterService.setDefaultContract(id);

            return RestResponse.result(result);
        } catch (final ApplicationException ex) {
            return RestResponse.error(ex.getCode(), ex.getMessage());
        }
    }
}
