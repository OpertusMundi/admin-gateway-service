package eu.opertusmundi.admin.web.controller.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.admin.web.domain.ContractEntity;
import eu.opertusmundi.admin.web.domain.ContractHistoryEntity;
import eu.opertusmundi.admin.web.domain.HelpdeskAccountEntity;
import eu.opertusmundi.admin.web.domain.SectionEntity;
import eu.opertusmundi.admin.web.domain.SectionHistoryEntity;
import eu.opertusmundi.admin.web.model.dto.AccountDto;
import eu.opertusmundi.admin.web.model.dto.ContractDto;
import eu.opertusmundi.admin.web.model.dto.ContractHistoryDto;
import eu.opertusmundi.admin.web.model.dto.SectionDto;
import eu.opertusmundi.admin.web.repository.ContractHistoryRepository;
import eu.opertusmundi.admin.web.repository.ContractRepository;
import eu.opertusmundi.admin.web.repository.HelpdeskAccountRepository;
import eu.opertusmundi.admin.web.repository.SectionHistoryRepository;
import eu.opertusmundi.admin.web.repository.SectionRepository;
import eu.opertusmundi.common.model.RestResponse;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_USER" })
public class ContractControllerImpl extends BaseController implements ContractController {

	//private List<String> sortableFields = Arrays.asList("name");

	@Autowired
	private ContractRepository contractRepository;

	@Autowired
	private ContractHistoryRepository contractHistoryRepository;
	
	@Autowired
	private SectionRepository sectionRepository;

	@Autowired
	private SectionHistoryRepository sectionHistoryRepository;

	@Autowired
	private HelpdeskAccountRepository helpdeskAccountRepo;
	
	
	/*@Override
	public RestResponse<PageResultDto<ContractDto>> find(
		int page, int size, String name, String orderBy, String order
	) {

		Direction direction = order.equalsIgnoreCase("desc") ? Direction.DESC : Direction.ASC;
		if (!sortableFields.contains(orderBy)) {
			orderBy = "name";
		}

		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, orderBy));

		Page<ContractEntity> entities = this.contractRepository.findAllByNameContains(pageRequest, name);

		Page<ContractDto> p = entities.map(ContractEntity::toDto);

		final long count = p.getTotalElements();
		final List<ContractDto> records = p.stream().collect(Collectors.toList());
		final PageResultDto<ContractDto> result = PageResultDto.of(page, size, records, count);

		return RestResponse.result(result);
	}*/

	@Override
	public RestResponse<ContractDto> findOne(int id) {
		final ContractEntity e = contractRepository.findById(id).orElse(null);

		if (e == null) {
			return RestResponse.notFound();
		}
		List<SectionEntity> sections = contractRepository.findSectionsByContract(e);
		List<SectionDto> sectionsDto = new ArrayList<SectionDto>() ;
		for(SectionEntity s : sections) sectionsDto.add(s.toDto());
		ContractDto contractDto = e.toDto();
		contractDto.setSections(sectionsDto);
		return RestResponse.result(contractDto);
	}
	
	@Override
	public RestResponse<List<ContractDto>> findAll(){
		
		final Optional<HelpdeskAccountEntity> accountEntity = helpdeskAccountRepo.findOneByEmail(this.currentUserName());
		final List<ContractEntity> contracts = contractRepository.findContractsByAccount(accountEntity.get());
		final List<ContractDto> contractsDto = new ArrayList<ContractDto>() ;
		for(ContractEntity e : contracts) contractsDto.add(e.toDto());
		return RestResponse.result(contractsDto);
	}


	@Override
	public RestResponse<ContractDto> create(ContractDto record, BindingResult validationResult) {
		if (validationResult.hasErrors()) {
			return RestResponse.invalid(validationResult.getFieldErrors());
		}
		
		//final Optional<AccountEntity> accountEntity = accountRepo.findOneByUsername(this.currentUserName());

		// Retrieve entity from repository
		final AccountDto account = helpdeskAccountRepo.findOneByEmail(this.currentUserName()).get().toDto();

		record.setAccount(account);
		List<SectionDto> sections = record.getSections();
		record.setSections(null);
		ContractDto resultRecord = contractRepository.saveFrom(record);
		// create sections
		final ContractEntity e = contractRepository.findById(resultRecord.getId()).get();
		record.setId(e.getId());
		
		//List<SectionEntity> contractSections = contractRepository.findSectionsByContract(e.getId());
		for (SectionDto s : sections){
			s.setContract(record);
			sectionRepository.saveFrom(s);
		}
		//also save a copy in history table
		ContractHistoryDto contractHistoryDto = contractHistoryRepository.saveFrom(e);
		//sectionHistoryRepository.saveSections
		
		ContractHistoryEntity cEntity = contractHistoryRepository.findById(contractHistoryDto.getId()).get();
		for (SectionDto s : sections){
			//s.setContract(record);
			sectionHistoryRepository.saveFrom(s, cEntity);
		}
		
		return RestResponse.result(resultRecord);
	}

	@Override
	public RestResponse<ContractDto> update(int id, ContractDto record, BindingResult validationResult) {
		//organizationValidator.validate(record, validationResult);
		
		//if (validationResult.hasErrors()) {
		//	return RestResponse.invalid(validationResult.getFieldErrors());
		//}
		
		// increment version
		record.setVersion("" + (Integer.parseInt(record.getVersion())+1));
		final AccountDto account = helpdeskAccountRepo.findOneByEmail(this.currentUserName()).get().toDto();

		record.setAccount(account);
		List<SectionDto> sections = record.getSections();
		record.setSections(null);
		ContractDto resultRecord = contractRepository.saveFrom(record);
		resultRecord = contractRepository.saveFrom(record);

		// create sections
		final ContractEntity e = contractRepository.findById(resultRecord.getId()).get();
		record.setId(e.getId());
		
		//List<SectionEntity> contractSections = contractRepository.findSectionsByContract(e);
		List<Integer> newSectionIds = new ArrayList<Integer>(); 
		int newId;
		for (SectionDto s : sections){
			s.setContract(record);
			newId =  sectionRepository.saveFrom(s).getId();
			newSectionIds.add(newId);
		}
		
		List<Integer> prevSectionIds = contractRepository.findSectionsIdsByContract(e);
		List<Integer> differences = new ArrayList<>(prevSectionIds);
		differences.removeAll(newSectionIds);
		for (Integer i: differences) {
			sectionRepository.remove(i);
		}
		
		//also save a copy in history table
		ContractHistoryDto contractHistoryDto = contractHistoryRepository.saveFrom(e);
				
		ContractHistoryEntity cEntity = contractHistoryRepository.findById(contractHistoryDto.getId()).get();
		for (SectionDto s : sections){
			sectionHistoryRepository.saveFrom(s, cEntity);
		}
		return RestResponse.result(resultRecord);
	}

	@Override
	public RestResponse<Void> delete(int id) {
		final ContractEntity ce = contractRepository.findById(id).orElse(null);

		if (ce == null) {
			return RestResponse.notFound();
		}
		List<SectionEntity> sections = contractRepository.findSectionsByContract(ce);
		for (SectionEntity s : sections){
			sectionRepository.remove(s.getId());
		}
		// remove all versions
		List <ContractHistoryEntity> historyCeList = contractHistoryRepository.findContractVersions(ce);
		for (ContractHistoryEntity h : historyCeList) {

			List<SectionHistoryEntity> sectionsHistory = contractHistoryRepository.findSectionsByContract(h);
			for (SectionHistoryEntity s : sectionsHistory){
				sectionHistoryRepository.remove(s.getId());
			}
			contractHistoryRepository.remove(h.getId());
		}

		
		contractRepository.remove(id);

		return RestResponse.success();
	}

	@Override
	public RestResponse<Void> updateState(int id, String state) {
		state =  state.replace("=", "");
		final ContractEntity ce = contractRepository.findById(id).orElse(null);

		if (ce == null) {
			return RestResponse.notFound();
		}
		ce.setState(state);
		
		contractRepository.saveFrom(ce.toDto());

		return RestResponse.success();
	}
	// TODO: Create separate service
}
