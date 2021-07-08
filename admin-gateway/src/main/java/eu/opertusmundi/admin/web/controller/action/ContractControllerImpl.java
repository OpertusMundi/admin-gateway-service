package eu.opertusmundi.admin.web.controller.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.common.domain.MasterContractDraftEntity;
import eu.opertusmundi.common.domain.MasterContractEntity;
import eu.opertusmundi.common.domain.MasterContractHistoryEntity;
import eu.opertusmundi.common.domain.HelpdeskAccountEntity;
import eu.opertusmundi.common.domain.MasterSectionDraftEntity;
import eu.opertusmundi.common.domain.MasterSectionEntity;
import eu.opertusmundi.common.domain.MasterSectionHistoryEntity;
import eu.opertusmundi.common.model.contract.MasterContractDraftDto;
import eu.opertusmundi.common.model.contract.MasterContractDto;
import eu.opertusmundi.common.model.contract.MasterContractHistoryDto;
import eu.opertusmundi.common.model.contract.MasterSectionDraftDto;
import eu.opertusmundi.common.model.contract.MasterSectionDto;
import eu.opertusmundi.common.repository.HelpdeskAccountRepository;
import eu.opertusmundi.common.repository.contract.MasterContractDraftRepository;
import eu.opertusmundi.common.repository.contract.MasterContractHistoryRepository;
import eu.opertusmundi.common.repository.contract.MasterContractRepository;
import eu.opertusmundi.common.repository.contract.MasterSectionDraftRepository;
import eu.opertusmundi.common.repository.contract.MasterSectionHistoryRepository;
import eu.opertusmundi.common.repository.contract.MasterSectionRepository;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.account.helpdesk.HelpdeskAccountDto;

@Transactional(readOnly = false)
@RestController
@Secured({ "ROLE_ADMIN", "ROLE_USER" })
public class ContractControllerImpl extends BaseController implements ContractController {

	//private List<String> sortableFields = Arrays.asList("name");

	@Autowired
	private MasterContractRepository contractRepository;

	@Autowired
	private MasterContractDraftRepository contractDraftRepository;
	
	@Autowired
	private MasterContractHistoryRepository contractHistoryRepository;
	
	@Autowired
	private MasterSectionRepository sectionRepository;

	@Autowired
	private MasterSectionDraftRepository sectionDraftRepository;
	
	@Autowired
	private MasterSectionHistoryRepository sectionHistoryRepository;

	@Autowired
	private HelpdeskAccountRepository helpdeskAccountRepo;
	

	@Override
	public RestResponse<MasterContractDto> findContract(int id) {
		final MasterContractEntity e = contractRepository.findById(id).orElse(null);

		if (e == null) {
			return RestResponse.notFound();
		}
		List<MasterSectionEntity> sections = contractRepository.findSectionsByContract(e);
		List<MasterSectionDto> sectionsDto = new ArrayList<MasterSectionDto>() ;
		for(MasterSectionEntity s : sections) sectionsDto.add(s.toDto());
		MasterContractDto contractDto = e.toDto();
		contractDto.setSections(sectionsDto);
		return RestResponse.result(contractDto);
	}
	
	@Override
	public RestResponse<MasterContractDraftDto> findDraft(int id) {
		final MasterContractDraftEntity e = contractDraftRepository.findById(id).orElse(null);

		if (e == null) {
			return RestResponse.notFound();
		}
		List<MasterSectionDraftEntity> sections = contractDraftRepository.findSectionsByContract(e);
		List<MasterSectionDraftDto> sectionsDto = new ArrayList<MasterSectionDraftDto>() ;
		for(MasterSectionDraftEntity s : sections) sectionsDto.add(s.toDto());
		MasterContractDraftDto contractDto = e.toDto();
		contractDto.setSections(sectionsDto);
		return RestResponse.result(contractDto);
	}
	
	@Override
	public RestResponse<List<MasterContractDto>> getContracts(){
		
		final Optional<HelpdeskAccountEntity> accountEntity = helpdeskAccountRepo.findOneByEmail(this.currentUserName());
		final List<MasterContractEntity> contracts = contractRepository.findContractsByAccount(accountEntity.get());
		final List<MasterContractDto> contractsDto = new ArrayList<MasterContractDto>() ;
		for(MasterContractEntity e : contracts) {
			contractsDto.add(e.toDto());
	}
		return RestResponse.result(contractsDto);
	}
	
	@Override
	public RestResponse<List<MasterContractDraftDto>> getDrafts(){
		
		final Optional<HelpdeskAccountEntity> accountEntity = helpdeskAccountRepo.findOneByEmail(this.currentUserName());
		final List<MasterContractDraftEntity> contracts = contractDraftRepository.findContractsByAccount(accountEntity.get());
		final List<MasterContractDraftDto> contractsDto = new ArrayList<MasterContractDraftDto>() ;
		for(MasterContractDraftEntity e : contracts) contractsDto.add(e.toDto());
		return RestResponse.result(contractsDto);
	}


	@Override
	public RestResponse<MasterContractDraftDto> createDraft(MasterContractDraftDto record, BindingResult validationResult) {
		if (validationResult.hasErrors()) {
			return RestResponse.invalid(validationResult.getFieldErrors());
		}
		
		//final Optional<AccountEntity> accountEntity = accountRepo.findOneByUsername(this.currentUserName());

		// Retrieve entity from repository
		final HelpdeskAccountDto account = helpdeskAccountRepo.findOneByEmail(this.currentUserName()).get().toDto();

		record.setAccount(account);
		List<MasterSectionDraftDto> sections = record.getSections();
		record.setSections(null);
		MasterContractDraftDto resultRecord = contractDraftRepository.saveFrom(record);
		// create sections
		final MasterContractDraftEntity e = contractDraftRepository.findById(resultRecord.getId()).get();
		record.setId(e.getId());
		record.setParentId(e.getId());
		contractDraftRepository.saveFrom(record);
		//List<SectionEntity> contractSections = contractRepository.findSectionsByContract(e.getId());
		for (MasterSectionDraftDto s : sections){
			s.setContract(record);
			sectionDraftRepository.saveFrom(s);
		}
		
		return RestResponse.result(resultRecord);
	}

	@Override
	public RestResponse<MasterContractDto> updateContract(int id, MasterContractDto record, BindingResult validationResult) {
		
		final HelpdeskAccountDto account = helpdeskAccountRepo.findOneByEmail(this.currentUserName()).get().toDto();

		record.setAccount(account);
		List<MasterSectionDto> sections = record.getSections();
		record.setSections(null);
		MasterContractDto resultRecord = contractRepository.saveFrom(record);
		

		// create sections
		final MasterContractEntity e = contractRepository.findById(resultRecord.getId()).get();
		
		//save a copy in history table
		MasterContractHistoryDto contractHistoryDto = contractHistoryRepository.saveFrom(e);
			
		MasterContractHistoryEntity cEntity = contractHistoryRepository.findById(contractHistoryDto.getId()).get();
		for (MasterSectionDto s : sections){
			//s.setContract(record);
			sectionHistoryRepository.saveFrom(s, cEntity);
		} 
				
		// increment version
		record.setVersion("" + (Integer.parseInt(record.getVersion())+1));
		contractRepository.saveFrom(record);
		
		
		
		//List<SectionEntity> contractSections = contractRepository.findSectionsByContract(e);
		List<Integer> newSectionIds = new ArrayList<Integer>(); 
		int newId;
		for (MasterSectionDto s : sections){
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
		
		
		return RestResponse.result(resultRecord);
	}
	
	@Override
	public RestResponse<MasterContractDraftDto> updateDraft(int id, MasterContractDraftDto record, BindingResult validationResult) {
	
		
		final HelpdeskAccountDto account = helpdeskAccountRepo.findOneByEmail(this.currentUserName()).get().toDto();

		record.setAccount(account);
		List<MasterSectionDraftDto> sections = record.getSections();
		record.setSections(null);
		MasterContractDraftDto resultRecord = contractDraftRepository.saveFrom(record);
		resultRecord = contractDraftRepository.saveFrom(record);

		// create sections
		final MasterContractDraftEntity e = contractDraftRepository.findById(resultRecord.getId()).get();
		record.setId(e.getId());
		
		
		
		//List<SectionEntity> contractSections = contractRepository.findSectionsByContract(e);
		List<Integer> newSectionIds = new ArrayList<Integer>(); 
		int newId;
		for (MasterSectionDraftDto s : sections){
			s.setContract(record);
			newId =  sectionDraftRepository.saveFrom(s).getId();
			newSectionIds.add(newId);
		}
		
		List<Integer> prevSectionIds = contractDraftRepository.findSectionsIdsByContract(e);
		List<Integer> differences = new ArrayList<>(prevSectionIds);
		differences.removeAll(newSectionIds);
		for (Integer i: differences) {
			sectionDraftRepository.remove(i);
		}
		
		
		return RestResponse.result(resultRecord);
	}

	@Override
	public RestResponse<Void> delete(int id) {
		final MasterContractEntity ce = contractRepository.findById(id).orElse(null);

		if (ce == null) {
			return RestResponse.notFound();
		}
		List<MasterSectionEntity> sections = contractRepository.findSectionsByContract(ce);
		for (MasterSectionEntity s : sections){
			sectionRepository.remove(s.getId());
		}
		// remove all versions
		List <MasterContractHistoryEntity> historyCeList = contractHistoryRepository.findContractVersions(ce.getParentId());
		for (MasterContractHistoryEntity h : historyCeList) {

			List<MasterSectionHistoryEntity> sectionsHistory = contractHistoryRepository.findSectionsByContract(h);
			for (MasterSectionHistoryEntity s : sectionsHistory){
				sectionHistoryRepository.remove(s.getId());
			}
			contractHistoryRepository.remove(h.getId());
		}

		
		contractRepository.remove(id);

		return RestResponse.success();
	}

	@Override
	public RestResponse<Void> deleteDraft(int id) {
		final MasterContractDraftEntity ce = contractDraftRepository.findById(id).orElse(null);

		if (ce == null) {
			return RestResponse.notFound();
		}
		List<MasterSectionDraftEntity> sections = contractDraftRepository.findSectionsByContract(ce);
		for (MasterSectionDraftEntity s : sections){
			sectionDraftRepository.remove(s.getId());
		}
	
		contractDraftRepository.remove(id);

		return RestResponse.success();
	}

	
	@Override
	public RestResponse<Void> updateState(int id, String state) {
		state =  state.replace("=", "");
		if (state.equals("DRAFT")) {
			final MasterContractEntity ce = contractRepository.findById(id).orElse(null);

			if (ce == null) {
				return RestResponse.notFound();
			}
			
			//save contract in draft
			MasterContractDraftDto cDto = contractDraftRepository.saveFrom(ce.toDto());
			
			// save sections
			List<MasterSectionEntity> sections = contractRepository.findSectionsByContract(ce);
			for (MasterSectionEntity s : sections){
				sectionDraftRepository.saveFrom(s.toDto(), contractDraftRepository.findById(cDto.getId()).get() );
			} 
			
			//remove from published
			this.delete(id);
			
			return RestResponse.success();
		}
		else {
			
			final MasterContractDraftEntity ce = contractDraftRepository.findById(id).orElse(null);
			if (ce == null) {
				return RestResponse.notFound();
			}
			final MasterContractEntity ceOld = contractRepository.findById(id).orElse(null);
			
			ce.setState(state);
			
			// increment version
			ce.setVersion("" + (Integer.parseInt(ce.getVersion())+1));
			
			//save contract in history table
			MasterContractHistoryDto contractHistoryDto = contractHistoryRepository.saveFrom(ce);

			
			
			//save contract in published
			MasterContractDto cDto = contractRepository.saveFrom(ce.toDto());
			// save sections
			
			List<MasterSectionDraftEntity> sections = contractRepository.findDraftSectionsByContract(ce);
			for (MasterSectionDraftEntity s : sections){
				sectionRepository.saveFrom(s.toDto(), contractRepository.findById(cDto.getId()).get() );
			} 
			
			
			MasterContractHistoryEntity contractHistoryEntity = contractHistoryRepository.findById(contractHistoryDto.getId()).get();
			//save sections in history table
			for (MasterSectionDraftEntity s : sections){
				//s.setContract(record);
				sectionHistoryRepository.saveFrom(s.toDto(), contractHistoryEntity);
			} 
			//remove from drafts
			this.deleteDraft(id);
			return RestResponse.success();
		}
	}
	// TODO: Create separate service
}
