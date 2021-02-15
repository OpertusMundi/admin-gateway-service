package eu.opertusmundi.admin.web.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.array.ListArrayType;

import eu.opertusmundi.admin.web.model.dto.SectionHistoryDto;


@TypeDef(
	    name = "list-array",
	    typeClass = ListArrayType.class)
@Table(
    schema = "web", name = "`section_history`"
    //uniqueConstraints = {
    //    @UniqueConstraint(name = "uq_organization_name", columnNames = {"`name`"}),
    //}
)
@Entity(name = "SectionHistory")
public class SectionHistoryEntity {
	
    @Id
    @Column(name = "`id`", updatable = false)
    @SequenceGenerator(
        sequenceName = "web.section_history_id_seq", name = "web_section_history_id_seq", allocationSize = 1
    )
    @GeneratedValue(generator = "web_section_history_id_seq", strategy = GenerationType.SEQUENCE)
    @lombok.Setter()
    @lombok.Getter()
    Integer id ;

    @NotNull
    @ManyToOne(
		fetch = FetchType.EAGER
	)
    @JoinColumn(name = "`contract`", nullable = false)
    @lombok.Getter
    @lombok.Setter
    ContractHistoryEntity contract;
    
    @Column(name = "`indent`")
    @lombok.Getter()
    @lombok.Setter()
    Integer indent;
    
    @NotNull
    @Size(max = 80)
    @Column(name = "`index`", updatable = true)
    @lombok.Getter()
    @lombok.Setter()
    String index;

    @Column(name = "`title`")
    @lombok.Getter()
    @lombok.Setter()
    String title;
    
    @Column(name = "`variable`")
    @lombok.Getter()
    @lombok.Setter()
    Boolean variable;
    
    @Column(name = "`optional`")
    @lombok.Getter()
    @lombok.Setter()
    Boolean optional;
    
    @Column(name = "`dynamic`")
    @lombok.Getter()
    @lombok.Setter()
    Boolean dynamic;
    
    
    //@ElementCollection
    //@CollectionTable(name = "`section`", joinColumns = @JoinColumn(name = "id")) 
    //@Column(length=10000, name = "`options`")
    @Type(type = "list-array")
    @Column(
        name = "options",
        columnDefinition = "text[]"
    )
    @lombok.Getter()
    @lombok.Setter()
    List<String> options ;
    
    @Type(type = "list-array")
    @Column(
        name = "styled_options",
        columnDefinition = "text[]"
    )
    @lombok.Getter()
    @lombok.Setter()
    List<String> styledOptions ;
    

    @Type(type = "list-array")
    @Column(
        name = "summary",
        columnDefinition = "text[]"
    )
    @lombok.Getter()
    @lombok.Setter()
    List<String> summary ;
    
    @Type(type = "list-array")
    @Column(
    	name="icons",
    	columnDefinition = "text")
    @lombok.Getter()
    @lombok.Setter()
    List <String> icons;
    
    @Column(name = "`description_of_change`")
    @lombok.Getter()
    @lombok.Setter()
    String descriptionOfChange;
    
    //@OneToMany(
    //    mappedBy = "organization", fetch = FetchType.LAZY
    //)
    //@lombok.Getter()
    //@lombok.Setter()
    //List<AccountEntity> customers = new ArrayList<>();

    public SectionHistoryDto toDto() {
    	SectionHistoryDto s = new SectionHistoryDto();

        s.setId(id);
        s.setContract(contract.toDto());
        s.setTitle(title);
        s.setVariable(variable);
        s.setOptional(optional);
        s.setDynamic(dynamic);
        s.setIndex(index);
        s.setIndent(indent);
        s.setSummary(summary);
        s.setOptions(options);
        s.setIcons(icons);
        s.setStyledOptions(styledOptions);
        s.setDescriptionOfChange(descriptionOfChange);
        return s;
    }

}
