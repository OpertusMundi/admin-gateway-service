package eu.opertusmundi.admin.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.opertusmundi.admin.web.domain.ContractEntity;
import eu.opertusmundi.admin.web.domain.SectionEntity;
import eu.opertusmundi.admin.web.model.contract.SectionDto;
import eu.opertusmundi.common.model.ApplicationException;
import eu.opertusmundi.common.model.BasicMessageCode;

@Repository
@Transactional(readOnly = true)
public interface SectionRepository extends JpaRepository<SectionEntity, Integer> {

	
	Optional<SectionEntity> findOneById(Integer id);
	
	//Optional<SectionEntity> findOneByNameAndIdNot(String name, Integer id);

	//Page<SectionEntity> findAllByNameContains(Pageable pageable, String name);

	@Query("SELECT o FROM Section o WHERE :sectionId IS NULL OR o.id = :sectionId")
	List<SectionEntity> findAll(
		@Param("sectionId") Integer sectionId,
		Sort sort
	);
	

	//@Query("SELECT s.contract FROM Section s WHERE s.contract = :contractId AND s.id= :sectionId")
   // Optional<ContractEntity> findContractOfSection(@Param("contractId") Integer contractId,
   // 		@Param("contractId") Integer sectionId);
	
	
	@Query("SELECT c FROM Contract c WHERE c.id = :contractId")
    Optional<ContractEntity> findContract(@Param("contractId") Integer contractId);
	
	@Transactional(readOnly = false)
	default SectionDto saveFrom(SectionDto s) {

		SectionEntity sectionEntity = null;
		sectionEntity = this.findById(s.getId()).orElse(null);
		if (sectionEntity == null) {
			
			// Create a new entity
			sectionEntity = new SectionEntity();
		}
		else if(sectionEntity.getContract().getId()!=s.getContract().getId()){
			// Create a new entity if this id exists in another contract
			sectionEntity = new SectionEntity();
		}
		
		//final ContractEntity e = contractRepository.findById(s.getContract().getId()).get();
		sectionEntity.setContract(this.findContract(s.getContract().getId()).get());
		sectionEntity.setTitle(s.getTitle());
		sectionEntity.setIndex(s.getIndex());
		sectionEntity.setIndent(s.getIndent());
		sectionEntity.setVariable(s.isVariable());
		sectionEntity.setOptional(s.isOptional());
		sectionEntity.setDynamic(s.isDynamic());
		sectionEntity.setStyledOptions(s.getStyledOptions());
		sectionEntity.setOptions(s.getOptions());
		sectionEntity.setSuboptions(s.getSuboptions());
		sectionEntity.setSummary(s.getSummary());
		sectionEntity.setIcons(s.getIcons());
		return saveAndFlush(sectionEntity).toDto();
	}

	@Transactional(readOnly = false)
	default void remove(int id) {

		SectionEntity sectionEntity = this.findById(id).orElse(null);

		if (sectionEntity == null) {
			throw ApplicationException.fromMessage(
				BasicMessageCode.RecordNotFound, 
				"Record not found"
			);
		}

		this.deleteById(id);
	}

}
