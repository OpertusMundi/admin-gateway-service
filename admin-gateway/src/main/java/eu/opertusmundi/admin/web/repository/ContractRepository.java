package eu.opertusmundi.admin.web.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.opertusmundi.admin.web.domain.ContractEntity;
import eu.opertusmundi.admin.web.domain.HelpdeskAccountEntity;
import eu.opertusmundi.admin.web.domain.SectionEntity;
import eu.opertusmundi.admin.web.model.contract.ContractDto;
import eu.opertusmundi.common.model.ApplicationException;
import eu.opertusmundi.common.model.BasicMessageCode;

@Repository
@Transactional(readOnly = true)
public interface ContractRepository extends JpaRepository<ContractEntity, Integer> {

	Optional<ContractEntity> findOneById(Integer id);
	
	//Optional<ContractEntity> findOneByNameAndIdNot(String name, Integer id);

	//Page<ContractEntity> findAllByNameContains(Pageable pageable, String name);

	//@Query("SELECT c FROM Section c WHERE :contractId IS NULL OR c.id = :contractId")
	//List<ContractEntity> findAll(
	//	@Param("id") Integer sectionId,
	//	Sort sort
	//);
	
	@Query("SELECT a FROM HelpdeskAccount a WHERE a.id = :id")
	    HelpdeskAccountEntity findAccountById(
			@Param("id") int id);
	
	@Query("SELECT s FROM Section s WHERE s.contract = :contract")
	List<SectionEntity> findSectionsByContract(
		@Param("contract") ContractEntity contract
	);
	
	@Query("SELECT id FROM Section s WHERE s.contract = :contract")
	List<Integer> findSectionsIdsByContract(
		@Param("contract") ContractEntity contract
	);
	
	
	@Query("SELECT c FROM Contract c WHERE c.account = :account")
	List<ContractEntity> findContractsByAccount(
		@Param("account") HelpdeskAccountEntity account);
	
	@Transactional(readOnly = false)
	default ContractDto saveFrom(ContractDto s) {
		ContractEntity contractEntity = null;
		if (s.getId() != null) {
			// Retrieve entity from repository
			contractEntity = this.findById(s.getId()).orElse(null);

			if (contractEntity == null) {
				throw ApplicationException.fromMessage(
					BasicMessageCode.RecordNotFound, 
					"Record not found"
				);
			}
		} else {
			// Create a new entity
			contractEntity = new ContractEntity();
			contractEntity.setCreatedAt(ZonedDateTime.now());
		}
		contractEntity.setTitle(s.getTitle());
		contractEntity.setSubtitle(s.getSubtitle());
		contractEntity.setId(s.getId());
		contractEntity.setAccount(this.findAccountById(s.getAccount().getId()));
		contractEntity.setState(s.getState());
		contractEntity.setVersion(s.getVersion());
		contractEntity.setModifiedAt(ZonedDateTime.now());
		return saveAndFlush(contractEntity).toDto();
	}

	@Transactional(readOnly = false)
	default void remove(int id) {

		ContractEntity ContractEntity = this.findById(id).orElse(null);

		if (ContractEntity == null) {
			throw ApplicationException.fromMessage(
				BasicMessageCode.RecordNotFound, 
				"Record not found"
			);
		}

		this.deleteById(id);
	}

}
