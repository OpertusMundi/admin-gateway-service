package eu.opertusmundi.admin.web.controller.action;

import java.util.List;

import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import eu.opertusmundi.admin.web.model.contract.ContractDto;
import eu.opertusmundi.common.model.RestResponse;

public interface ContractController {

	@GetMapping(value = { "/action/user/contracts/{id}" })
	RestResponse<ContractDto> findOne(@PathVariable int id);

	@GetMapping(value = { "/action/user/getContracts/" })
	RestResponse<List<ContractDto>> findAll();

	
	@PostMapping(value = "/action/user/contracts")
	RestResponse<ContractDto> create(
		@Valid @RequestBody ContractDto contract, BindingResult validationResult
	);

	@PostMapping(value = { "/action/user/contracts/{id}" })
	RestResponse<ContractDto> update(
		@PathVariable int id, @Valid @RequestBody ContractDto contract, BindingResult validationResult
	);

	@DeleteMapping(value = { "/action/user/contracts/{id}" })
	RestResponse<Void> delete(@PathVariable int id);
	
	@PostMapping(value = { "/action/user/contracts/updateState/{id}" })
	RestResponse<Void> updateState(@PathVariable int id, @RequestBody String state);

}
