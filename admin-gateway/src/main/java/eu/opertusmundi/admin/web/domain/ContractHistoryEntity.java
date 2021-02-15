package eu.opertusmundi.admin.web.domain;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import eu.opertusmundi.admin.web.model.dto.ContractHistoryDto;


@Entity(name = "ContractHistory")
@Table(
    schema = "web", name = "`contract_history`"
)
public class ContractHistoryEntity {
	
    @Id
    @Column(name = "`id`", updatable = false)
    @SequenceGenerator(
        sequenceName = "web.contract_history_id_seq", name = "web_contract_history_id_seq", allocationSize = 1
    )
    @GeneratedValue(generator = "web_contract_history_id_seq", strategy = GenerationType.SEQUENCE)
    @lombok.Setter()
    @lombok.Getter()
    Integer id ;
    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "`parent_id`", nullable = false)
    @lombok.Getter
    @lombok.Setter
    ContractEntity parentId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "`account`", nullable = false)
    @lombok.Getter
    @lombok.Setter
    HelpdeskAccountEntity account;

    @Column(name = "`title`")
    @lombok.Getter()
    @lombok.Setter()
    String title;

    @Column(name = "`subtitle`")
    @lombok.Getter
    @lombok.Setter
    String subtitle;

    @Column(name = "`state`")
    @lombok.Getter
    @lombok.Setter
    String state;

    @Column(name = "`version`")
    @lombok.Getter
    @lombok.Setter
    String version;


    @Column(name = "`created_at`")
    @lombok.Getter
    @lombok.Setter
    ZonedDateTime createdAt;


    @Column(name = "`modified_at`")
    @lombok.Getter
    @lombok.Setter
    ZonedDateTime modifiedAt;
    
   /*@OneToMany(
        mappedBy = "contract", 
        fetch = FetchType.LAZY,
        targetEntity = SectionEntity.class
    )
    @lombok.Getter()
    @lombok.Setter()
    List<SectionEntity> sections = new ArrayList<>();*/

    public ContractHistoryDto toDto() {
    	ContractHistoryDto c = new ContractHistoryDto();

        c.setId(this.id);
        c.setParentId(this.getParentId().toDto());
        c.setTitle(this.title);
        c.setSubtitle(this.subtitle);
        c.setState(this.state);
        c.setAccount(this.account.toDto());
        c.setCreatedAt(this.createdAt);
        c.setModifiedAt(this.modifiedAt);
        c.setVersion(this.version);
        //c.setSections(this.sections);
        
        return c;
    }

}
