package eu.opertusmundi.admin.web.controller.action;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import eu.opertusmundi.common.model.contract.EnumContractStatus;
import eu.opertusmundi.common.model.contract.helpdesk.EnumMasterContractSortField;
import eu.opertusmundi.common.model.contract.helpdesk.MasterContractCommandDto;
import eu.opertusmundi.common.model.contract.helpdesk.MasterContractDto;
import eu.opertusmundi.common.model.contract.helpdesk.MasterContractHistoryDto;
import eu.opertusmundi.common.model.contract.helpdesk.MasterContractHistoryResult;
import eu.opertusmundi.common.model.message.client.ClientContactDto;

@RequestMapping(value = "/action/contract", produces = MediaType.APPLICATION_JSON_VALUE)
public interface ContractController {

    /**
     * Find providers
     * 
     * @param email
     * @return
     */
    @GetMapping(value = "/providers")
    RestResponse<List<ClientContactDto>> findProviders(@RequestParam String email);
    
    /**
     * Get all contracts
     *
     * @param page
     * @param size
     * @param orderBy
     * @param order
     * @return
     */
    @GetMapping(value = {"/history"})
    RestResponse<MasterContractHistoryResult> findAllHistory(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "25") @Max(50) @Min(1) int size,
        @RequestParam(name = "defaultContract", required = false) String defaultContract,
        @RequestParam(name = "title", required = false) String title,
        @RequestParam(name = "status", required = false) Set<EnumContractStatus> status,
        @RequestParam(name = "orderBy", defaultValue = "MODIFIED_ON") EnumMasterContractSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

    /**
     * Creates a new draft from an existing template.
     *
     * If a draft already exists, the existing record is returned
     *
     * @param id
     * @return
     */
    @PostMapping(value = {"/history/{id}"})
    RestResponse<MasterContractDto> createDraftForTemplate(
        @PathVariable int id
    );

    /**
     * Creates a new cloned draft from an existing template.
     *
     * <p>
     * It will start as a new draft from version <b>1</b>.
     *
     * @param id
     * @return
     */
    @PostMapping(value = {"/history/{id}/clone"})
    RestResponse<MasterContractDto> cloneDraftFromTemplate(
        @PathVariable int id
    );

    /**
     * Deactivate contract template
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = {"/history/{id}"})
    RestResponse<MasterContractHistoryDto> deactivate(
        @PathVariable int id
    );

    /**
     * Print master contract
     *
     * @param id
     * @return
     */
    @GetMapping(value = {"/history/{id}/print"})
    ResponseEntity<StreamingResponseBody> print(@PathVariable int id);

    /**
     * Set history contract as the default template.
     *
     * <p>
     * A contract must be <code>ACTIVE</code> to become the default contract.
     *
     * @param id
     * @param contract
     * @param validationResult
     * @return
     */
    @PostMapping(value = {"/history/{id}/set-default"})
    RestResponse<MasterContractDto> setDefaultContract(@PathVariable int id);

    /**
     * Get all contract templates
     *
     * @param page
     * @param size
     * @param orderBy
     * @param order
     * @return
     */
    @GetMapping(value = {"/templates"})
    RestResponse<PageResultDto<MasterContractDto>> findAll(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "25") @Max(50) @Min(1) int size,
        @RequestParam(name = "orderBy", defaultValue = "MODIFIED_ON") EnumMasterContractSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

    /**
     * Get a contract template by id
     *
     * @param id
     * @return
     */
    @GetMapping(value = {"/templates/{id}"})
    RestResponse<?> findOne(
        @PathVariable int id
    );

    /**
     * Get all contract drafts
     *
     * @param page
     * @param size
     * @param orderBy
     * @param order
     * @return
     */
    @GetMapping(value = {"/drafts"})
    RestResponse<PageResultDto<MasterContractDto>> findAllDrafts(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "25") @Max(50) @Min(1) int size,
        @RequestParam(name = "orderBy", defaultValue = "MODIFIED_ON") EnumMasterContractSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

	/**
	 * Create new contract draft
	 *
	 * @param contract
	 * @param validationResult
	 * @return
	 */
    @PostMapping(value = {"/drafts"})
    RestResponse<MasterContractDto> createDraft(
        @Valid @RequestBody MasterContractCommandDto command,
        BindingResult validationResult
    );

    /**
     * Get a contract draft by id
     *
     * @param id
     * @return
     */
    @GetMapping(value = {"/drafts/{id}"})
    RestResponse<MasterContractDto> findOneDraft(
        @PathVariable int id
    );

    /**
     * Update existing contract draft
     *
     * @param id
     * @param contract
     * @param validationResult
     * @return
     */
    @PostMapping(value = {"/drafts/{id}"})
    RestResponse<MasterContractDto> updateDraft(
        @PathVariable int id,
        @Valid @RequestBody MasterContractCommandDto contract,
        BindingResult validationResult
    );

    /**
     * Delete contract draft
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = {"/drafts/{id}"})
    RestResponse<Void> deleteDraft(@PathVariable int id);

    /**
     * Publish contract draft
     *
     * @param id
     * @param state
     * @return
     */
    @PutMapping(value = {"/drafts/{id}"})
    RestResponse<MasterContractDto> publishDraft(@PathVariable int id);

}
