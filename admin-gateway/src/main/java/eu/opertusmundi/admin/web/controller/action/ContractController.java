package eu.opertusmundi.admin.web.controller.action;

import java.util.List;

import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import eu.opertusmundi.common.model.contract.MasterContractDraftDto;
import eu.opertusmundi.common.model.contract.MasterContractDto;
import eu.opertusmundi.common.model.RestResponse;

public interface ContractController {

	@GetMapping(value = { "/action/user/contracts/{id}" })
	RestResponse<MasterContractDto> findContract(@PathVariable int id);

	@GetMapping(value = { "/action/user/contracts/drafts/{id}" })
	RestResponse<MasterContractDraftDto> findDraft(@PathVariable int id);
	
	@GetMapping(value = { "/action/user/getContracts/" })
	RestResponse<List<MasterContractDto>> getContracts();

	@GetMapping(value = { "/action/user/getDrafts/" })
	RestResponse<List<MasterContractDraftDto>> getDrafts();
	
	@PostMapping(value = "/action/user/contracts")
	RestResponse<MasterContractDraftDto> createDraft(
		@Valid @RequestBody MasterContractDraftDto contract, BindingResult validationResult
	);

	@PostMapping(value = { "/action/user/contracts/{id}" })
	RestResponse<MasterContractDto> updateContract(
		@PathVariable int id, @Valid @RequestBody MasterContractDto contract, BindingResult validationResult
	);
	
	@PostMapping(value = { "/action/user/contracts/drafts/{id}" })
	RestResponse<MasterContractDraftDto> updateDraft(
		@PathVariable int id, @Valid @RequestBody MasterContractDraftDto contract, BindingResult validationResult
	);

	@DeleteMapping(value = { "/action/user/contracts/{id}" })
	RestResponse<Void> delete(@PathVariable int id);
	
	@DeleteMapping(value = { "/action/user/contracts/drafts/{id}" })
	RestResponse<Void> deleteDraft(@PathVariable int id);
	
	@PostMapping(value = { "/action/user/contracts/updateState/{id}" })
	RestResponse<Void> updateState(@PathVariable int id, @RequestBody String state);

}
