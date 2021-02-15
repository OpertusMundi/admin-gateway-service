package eu.opertusmundi.admin.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.opertusmundi.admin.web.domain.ContractHistoryEntity;
import eu.opertusmundi.admin.web.domain.SectionHistoryEntity;
import eu.opertusmundi.admin.web.model.dto.SectionDto;
import eu.opertusmundi.admin.web.model.dto.SectionHistoryDto;
import eu.opertusmundi.common.model.ApplicationException;
import eu.opertusmundi.common.model.BasicMessageCode;

@Repository
@Transactional(readOnly = true)
public interface SectionHistoryRepository extends JpaRepository<SectionHistoryEntity, Integer> {

	
	Optional<SectionHistoryEntity> findOneById(Integer id);
	
	//Optional<SectionEntity> findOneByNameAndIdNot(String name, Integer id);

	//Page<SectionEntity> findAllByNameContains(Pageable pageable, String name);

	@Query("SELECT o FROM SectionHistory o WHERE :sectionId IS NULL OR o.id = :sectionId")
	List<SectionHistoryEntity> findAll(
		@Param("sectionId") Integer sectionId,
		Sort sort
	);
	
	@Query("SELECT s FROM SectionHistory s WHERE s.contract = :contract")
    Optional<SectionHistoryEntity> findSectionHistoryEntity(
		ContractHistoryEntity contract);
	

	//@Query("SELECT s.contract FROM Section s WHERE s.contract = :contractId AND s.id= :sectionId")
   // Optional<ContractEntity> findContractOfSection(@Param("contractId") Integer contractId,
   // 		@Param("contractId") Integer sectionId);
	
	
	@Query("SELECT c FROM ContractHistory c WHERE c.id = :contractId")
    Optional<ContractHistoryEntity> findContract(@Param("contractId") Integer contractId);
	
	@Transactional(readOnly = false)
	default SectionHistoryDto saveFrom(SectionDto s, ContractHistoryEntity contract) {
		

		System.out.println(s.getContract());
		SectionHistoryEntity sectionHistoryEntity = null;
		//sectionHistoryEntity = this.findSectionHistoryEntity(contract).orElse(null);
		//if (sectionHistoryEntity == null) {
		//	System.out.println("in SECTION SAVEFROM null");
			
		// Create a new entity
		sectionHistoryEntity = new SectionHistoryEntity();
		//}
		/*else if(sectionHistoryEntity.getContract().getId()!= contract.getId()){
			// Create a new entity if this id exists in another contract
			System.out.println("in SECTION SAVEFROM elseif");
			sectionHistoryEntity = new SectionHistoryEntity();
		}*/
		
		//final ContractEntity e = contractRepository.findById(s.getContract().getId()).get();
		sectionHistoryEntity.setContract(contract);
		sectionHistoryEntity.setTitle(s.getTitle());
		sectionHistoryEntity.setIndex(s.getIndex());
		sectionHistoryEntity.setIndent(s.getIndent());
		sectionHistoryEntity.setVariable(s.isVariable());
		sectionHistoryEntity.setOptional(s.isOptional());
		sectionHistoryEntity.setDynamic(s.isDynamic());
		sectionHistoryEntity.setStyledOptions(s.getStyledOptions());
		sectionHistoryEntity.setOptions(s.getOptions());
		sectionHistoryEntity.setSummary(s.getSummary());
		sectionHistoryEntity.setIcons(s.getIcons());
		return saveAndFlush(sectionHistoryEntity).toDto();
	}

	@Transactional(readOnly = false)
	default void remove(int id) {

		SectionHistoryEntity sectionHistoryEntity = this.findById(id).orElse(null);

		if (sectionHistoryEntity == null) {
			throw ApplicationException.fromMessage(
				BasicMessageCode.RecordNotFound, 
				"Record not found"
			);
		}

		this.deleteById(id);
	}

}
