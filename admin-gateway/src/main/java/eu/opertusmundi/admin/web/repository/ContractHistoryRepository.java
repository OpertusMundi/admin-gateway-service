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
import eu.opertusmundi.admin.web.domain.ContractHistoryEntity;
import eu.opertusmundi.admin.web.domain.HelpdeskAccountEntity;
import eu.opertusmundi.admin.web.domain.SectionHistoryEntity;
import eu.opertusmundi.admin.web.model.contract.ContractHistoryDto;
import eu.opertusmundi.common.model.ApplicationException;
import eu.opertusmundi.common.model.BasicMessageCode;

@Repository
@Transactional(readOnly = true)
public interface ContractHistoryRepository extends JpaRepository<ContractHistoryEntity, Integer> {

	Optional<ContractHistoryEntity> findOneById(Integer id);
	
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
	
	@Query("SELECT c FROM ContractHistory c WHERE c.parentId = :parentId AND c.version = :version ")
    Optional<ContractHistoryEntity> findContractHistoryEntity(
    		ContractEntity parentId, @Param("version") String version);
	
	@Query("SELECT o FROM SectionHistory o WHERE o.contract = :contract")
	List<SectionHistoryEntity> findSectionsByContract(
		@Param("contract") ContractHistoryEntity contract
	);
	
	@Query("SELECT c FROM ContractHistory c WHERE c.account = :account")
	List<ContractEntity> findHistoryContractsByAccount(
		@Param("account") HelpdeskAccountEntity account);
	
	@Query("SELECT c FROM ContractHistory c WHERE c.parentId = :parentId")
	List<ContractHistoryEntity> findContractVersions(
			ContractEntity parentId);
	
	@Transactional(readOnly = false)
	default ContractHistoryDto saveFrom(ContractEntity s) {
		ContractHistoryEntity contractHistoryEntity = null;
		contractHistoryEntity = this.findContractHistoryEntity(s, s.getVersion()).orElse(null);
		if (contractHistoryEntity == null) {
			// Create a new entity
			contractHistoryEntity = new ContractHistoryEntity();
			contractHistoryEntity.setCreatedAt(ZonedDateTime.now());

		}
		contractHistoryEntity.setTitle(s.getTitle());
		contractHistoryEntity.setSubtitle(s.getSubtitle());
		contractHistoryEntity.setParentId(s);
		contractHistoryEntity.setAccount(this.findAccountById(s.getAccount().getId()));
		contractHistoryEntity.setState(s.getState());
		contractHistoryEntity.setVersion(s.getVersion());
		contractHistoryEntity.setModifiedAt(ZonedDateTime.now());
		return saveAndFlush(contractHistoryEntity).toDto();
		
	}

	@Transactional(readOnly = false)
	default void remove(int id) {

		ContractHistoryEntity contractHistoryEntity = this.findById(id).orElse(null);

		if (contractHistoryEntity == null) {
			throw ApplicationException.fromMessage(
				BasicMessageCode.RecordNotFound, 
				"Record not found"
			);
		}

		this.deleteById(id);
	}

}
